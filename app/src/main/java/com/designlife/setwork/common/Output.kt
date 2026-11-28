package com.designlife.setwork.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.concurrent.Executors

object AppOutput {

    private const val TAG = "AppOutput"
    private const val NOTIF_CHANNEL_ID = "app_output_exports"
    private const val PDF_PAGE_W = 595
    private const val PDF_PAGE_H = 842
    private const val PDF_MARGIN_X = 40f
    private const val PDF_MARGIN_TOP = 50f
    private const val PDF_MARGIN_BOT = 50f
    private const val PDF_TEXT_SIZE = 14f

    private val io = Executors.newSingleThreadExecutor { r -> Thread(r, "AppOutput-IO") }
    private val main = Handler(Looper.getMainLooper())

    // ─────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────

    fun exportAsPdf(
        context: Context,
        fileName: String,
        content: String,
        openAfterSave: Boolean = true
    ) {
        if (content.isBlank()) {
            toast(context, "Nothing to export"); return
        }
        val appCtx = context.applicationContext
        ensureNotifChannel(appCtx)
        io.execute {
            val notifId = fileName.hashCode()
            notify(appCtx, notifId, "Saving PDF…")
            Log.d(TAG, "exportAsPdf: starting — $fileName")

            val result = runCatching { buildAndSavePdf(appCtx, fileName, content) }
            cancelNotif(appCtx, notifId)

            result
                .onSuccess { (uri, _) ->
                    Log.d(TAG, "exportAsPdf: success → $uri")
                    toast(appCtx, "PDF saved to Downloads")
                    notifyDone(appCtx, fileName, uri, "application/pdf")
                    if (openAfterSave) openUri(appCtx, uri, "application/pdf")
                }
                .onFailure { err ->
                    Log.e(TAG, "exportAsPdf: failed", err)
                    toast(appCtx, "PDF failed: ${err.message?.take(60)}")
                }
        }
    }

    fun exportAsPng(context: Context, fileName: String, content: String) {
        if (content.isBlank()) {
            toast(context, "Nothing to export"); return
        }
        val appCtx = context.applicationContext
        ensureNotifChannel(appCtx)
        io.execute {
            val notifId = (fileName + "_png").hashCode()
            notify(appCtx, notifId, "Saving image…")
            Log.d(TAG, "exportAsPng: starting — $fileName")

            val result = runCatching { buildAndSavePng(appCtx, fileName, content) }
            cancelNotif(appCtx, notifId)

            result
                .onSuccess { (uri, _) ->
                    Log.d(TAG, "exportAsPng: success → $uri")
                    toast(appCtx, "Image saved to Downloads")
                    notifyDone(appCtx, fileName, uri, "image/png")
                }
                .onFailure { err ->
                    Log.e(TAG, "exportAsPng: failed", err)
                    toast(appCtx, "Image failed: ${err.message?.take(60)}")
                }
        }
    }

    fun openUri(context: Context, uri: Uri, mimeType: String) {
        main.post {
            try {
                context.startActivity(
                    Intent.createChooser(
                        Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, mimeType)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        },
                        "Open with…"
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "openUri: no handler for $mimeType", e)
                toast(context, "No app found to open this file")
            }
        }
    }

    // ─────────────────────────────────────────────
    // PDF build
    // ─────────────────────────────────────────────

    private fun buildAndSavePdf(
        context: Context,
        fileName: String,
        content: String
    ): Pair<Uri, File> {
        val doc = PdfDocument()
        val usableW = PDF_PAGE_W - PDF_MARGIN_X * 2
        val maxTextH = PDF_PAGE_H - PDF_MARGIN_TOP - PDF_MARGIN_BOT

        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = PDF_TEXT_SIZE
        }

        try {
            val full = makeLayout(content, paint, usableW.toInt(), 1.4f, 8f)
            var startLine = 0
            var pageNum = 1

            while (startLine < full.lineCount) {
                var endLine = startLine
                while (endLine < full.lineCount) {
                    val relBottom = full.getLineBottom(endLine) - full.getLineTop(startLine)
                    if (relBottom > maxTextH) break
                    endLine++
                }
                if (endLine == startLine) endLine =
                    startLine + 1  // always advance at least one line

                val page = doc.startPage(
                    PdfDocument.PageInfo.Builder(PDF_PAGE_W, PDF_PAGE_H, pageNum).create()
                )
                val canvas = page.canvas
                canvas.drawColor(Color.WHITE)

                val chStart = full.getLineStart(startLine)
                val chEnd = full.getLineEnd(endLine - 1)
                val pageLayout = makeLayout(
                    content.substring(chStart, chEnd), paint, usableW.toInt(), 1.4f, 8f
                )

                canvas.save()
                canvas.translate(PDF_MARGIN_X, PDF_MARGIN_TOP)
                pageLayout.draw(canvas)
                canvas.restore()
                doc.finishPage(page)
                startLine = endLine
                pageNum++
            }

            return writeFile(context, "$fileName.pdf", "application/pdf") { out ->
                doc.writeTo(out)
            }
        } finally {
            doc.close()
        }
    }

    // ─────────────────────────────────────────────
    // PNG build
    // ─────────────────────────────────────────────

    private fun buildAndSavePng(
        context: Context,
        fileName: String,
        content: String
    ): Pair<Uri, File> {
        val width = 1080
        val padH = width * 0.07f
        val padV = width * 0.08f
        val usableW = (width - padH * 2).toInt()

        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#1C1C1E")
            textSize = width * 0.045f
            typeface = Typeface.create("serif", Typeface.BOLD)
        }
        val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#1C1C1E")
            textSize = width * 0.030f
        }
        val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#8E8E93")
            textSize = width * 0.022f
        }
        val titleLayout = makeLayout(fileName, titlePaint, usableW, 1.2f, 4f)
        val bodyLayout = makeLayout(content, bodyPaint, usableW, 1.6f, 6f)
        val dividerY = padV + titleLayout.height + padV * 0.25f
        val bodyY = dividerY + padV * 0.35f
        val totalH = (bodyY + bodyLayout.height + padV).toInt()
        val bitmap = Bitmap.createBitmap(width, totalH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.parseColor("#FAFAF8"))
        canvas.drawLine(padH, 0f, padH + usableW, 0f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4A90D9"); strokeWidth = width * 0.008f
        })
        canvas.save(); canvas.translate(padH, padV); titleLayout.draw(canvas); canvas.restore()
        canvas.drawLine(padH, dividerY, padH + usableW, dividerY, Paint().apply {
            color = Color.LTGRAY; strokeWidth = 1.5f
        })
        canvas.save(); canvas.translate(padH, bodyY); bodyLayout.draw(canvas); canvas.restore()
        canvas.drawText(
            "${content.length} chars · ${content.lines().size} lines",
            padH, totalH - padV * 0.5f, footerPaint
        )
        return try {
            writeFile(context, "$fileName.png", "image/png") { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        } finally {
            bitmap.recycle()
        }
    }

    @Suppress("DEPRECATION")
    private fun writeFile(
        context: Context,
        fileName: String,
        mimeType: String,
        writer: (OutputStream) -> Unit
    ): Pair<Uri, File> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d(TAG, "writeFile: tier-1 MediaStore")
            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
            val msUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            if (msUri != null) {
                try {
                    resolver.openOutputStream(msUri)?.use(writer)
                        ?: error("openOutputStream returned null for $msUri")
                    values.clear()
                    values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(msUri, values, null, null)
                    val stub = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        fileName
                    )
                    Log.d(TAG, "writeFile: tier-1 OK → $msUri")
                    return Pair(msUri, stub)
                } catch (t: Throwable) {
                    Log.w(TAG, "writeFile: tier-1 write failed, falling back", t)
                    runCatching { resolver.delete(msUri, null, null) }
                }
            } else {
                Log.w(TAG, "writeFile: tier-1 MediaStore.insert returned null, falling back")
            }
        }
        val extMounted = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        val destDir = when {
            extMounted -> {
                Log.d(TAG, "writeFile: tier-2 public Downloads")
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            }

            else -> {
                Log.w(TAG, "writeFile: tier-3 fallback to app-private dir")
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
            }
        }

        destDir.mkdirs()
        val file = File(destDir, fileName)
        FileOutputStream(file).use(writer)
        Log.d(TAG, "writeFile: file written → ${file.absolutePath}")
        MediaScannerConnection.scanFile(
            context,
            arrayOf(file.absolutePath),
            arrayOf(mimeType),
            null
        )
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } else {
            Uri.fromFile(file)
        }
        Log.d(TAG, "writeFile: URI → $uri")
        return Pair(uri, file)
    }

    // ─────────────────────────────────────────────
    // StaticLayout compat
    // ─────────────────────────────────────────────

    @Suppress("DEPRECATION")
    private fun makeLayout(
        text: String,
        paint: TextPaint,
        width: Int,
        spacingMult: Float,
        spacingAdd: Float
    ): StaticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        StaticLayout.Builder
            .obtain(text, 0, text.length, paint, width)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(spacingAdd, spacingMult)
            .setIncludePad(false)
            .build()
    } else {
        StaticLayout(
            text, paint, width,
            Layout.Alignment.ALIGN_NORMAL,
            spacingMult, spacingAdd, false
        )
    }

    // ─────────────────────────────────────────────
    // Notifications
    // ─────────────────────────────────────────────

    private fun ensureNotifChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (nm.getNotificationChannel(NOTIF_CHANNEL_ID) != null) return
            nm.createNotificationChannel(
                NotificationChannel(
                    NOTIF_CHANNEL_ID, "Exports", NotificationManager.IMPORTANCE_LOW
                ).apply { description = "Export progress and results" }
            )
        }
    }

    private fun notify(context: Context, id: Int, text: String) {
        runCatching {
            val nm =
                ContextCompat.getSystemService(context, NotificationManager::class.java) ?: return
            nm.notify(
                id, NotificationCompat.Builder(context, NOTIF_CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setContentTitle("Exporting…")
                    .setContentText(text)
                    .setProgress(0, 0, true)
                    .setOngoing(true)
                    .build()
            )
        }
    }

    private fun notifyDone(context: Context, fileName: String, uri: Uri, mimeType: String) {
        runCatching {
            val nm =
                ContextCompat.getSystemService(context, NotificationManager::class.java) ?: return
            val id = (fileName + "_done").hashCode()
            val pi = PendingIntent.getActivity(
                context, id,
                Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, mimeType)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            nm.notify(
                id, NotificationCompat.Builder(context, NOTIF_CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
                    .setContentTitle("Saved")
                    .setContentText(fileName)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build()
            )
        }
    }

    private fun cancelNotif(context: Context, id: Int) {
        runCatching {
            ContextCompat.getSystemService(context, NotificationManager::class.java)?.cancel(id)
        }
    }

    private fun toast(context: Context, message: String) {
        main.post { Toast.makeText(context, message, Toast.LENGTH_SHORT).show() }
    }
}