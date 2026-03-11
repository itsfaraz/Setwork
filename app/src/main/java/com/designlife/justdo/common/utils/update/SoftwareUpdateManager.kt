package com.designlife.justdo.common.utils.update

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import androidx.core.content.ContextCompat
import com.designlife.justdo.BuildConfig
import com.designlife.justdo.common.domain.repositories.SoftwareUpdateRepository
import com.designlife.orchestrator.NotificationScheduler
import com.designlife.orchestrator.data.NotificationInfo
import com.designlife.orchestrator.data.NotificationStatus
import com.designlife.orchestrator.data.NotificationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import kotlin.math.absoluteValue

class SoftwareUpdateManager(
    private val context: Context,
    private val scope : CoroutineScope,
    private val updateRepository: SoftwareUpdateRepository,
    private val notificationScheduler: NotificationScheduler
) {

    private var downloadId : Long = 0L
    private var sha256CheckSum : String = ""

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (id == downloadId) {
                handleDownloadCompleted(context, id)
            }
        }

        private fun handleDownloadCompleted(context: Context, id: Long) {

            val apkFile = getDownloadedFile(context, id) ?: return

            val calculated = sha256(apkFile)

            if (calculated == sha256CheckSum) {
                val uri = getDownloadedApkUri(context,downloadId)
                uri?.let {uri ->
                    installApk(context, uri)
                }
            } else {
                apkFile.delete()
            }
        }
    }

    fun checkForUpdate() {
        scope.launch(Dispatchers.IO) {
            updateRepository.fetchReleaseUpdates()?.let { appMetaResponse ->
                if (isUpdateAvailable(appMetaResponse.tag_name)){
                    val updateNotification = NotificationInfo(
                        scheduledTime = System.currentTimeMillis() + 10,
                        taskTitle = "Software Update",
                        taskSubTitle = "Tap to install update",
                        taskId = 10001,
                        notificationType = NotificationType.APP_UPDATE,
                        notificationStatus = NotificationStatus.ACTIVE,
                        createdTime = System.currentTimeMillis(),
                        deliveredTime = 0L
                    )
                    notificationScheduler.scheduleNotification(updateNotification)
                }
            }
        }

    }

    private fun isUpdateAvailable(latestVersion: String): Boolean {
        return compareVersions(latestVersion, BuildConfig.VERSION_NAME) > 0
    }

    private fun compareVersions(v1: String, v2: String): Int {
        val parts1 = v1.split(".")
        val parts2 = v2.split(".")

        val maxLength = maxOf(parts1.size, parts2.size)

        for (i in 0 until maxLength) {
            val p1 = parts1.getOrNull(i)?.toIntOrNull() ?: 0
            val p2 = parts2.getOrNull(i)?.toIntOrNull() ?: 0

            if (p1 > p2) return 1
            if (p1 < p2) return -1
        }

        return 0
    }

    fun installUpdate() {
        scope.launch(Dispatchers.IO) {
            updateRepository.fetchReleaseUpdates()?.let { appMetaResponse ->
                appMetaResponse.assets?.let { appMeta ->
                    downloadId = downloadApk(context,appMeta.browser_download_url,appMeta.name)
                    ContextCompat.registerReceiver(
                        context,
                        downloadReceiver,
                        IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                        ContextCompat.RECEIVER_NOT_EXPORTED
                    )
                }
            }
        }
    }

    private fun downloadApk(
        context: Context,
        apkUrl: String,
        packageName : String,
    ): Long {
        val request = DownloadManager.Request(Uri.parse(apkUrl))
            .setTitle("Software Update")
            .setDescription("Tap to install latest update")
            .setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            .setDestinationInExternalFilesDir(
                context,
                Environment.DIRECTORY_DOWNLOADS,
                packageName
            )

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        return dm.enqueue(request)
    }

    private fun getDownloadedApkUri(context: Context, downloadId: Long): Uri? {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        return dm.getUriForDownloadedFile(downloadId)
    }

    private fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val input = FileInputStream(file)

        val buffer = ByteArray(8192)
        var read: Int

        while (input.read(buffer).also { read = it } != -1) {
            digest.update(buffer, 0, read)
        }

        val hash = digest.digest()

        return hash.joinToString("") {
            "%02x".format(it)
        }
    }
    private fun installApk(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(intent)
    }

    private fun getDownloadedFile(context: Context, downloadId: Long): File? {

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val query = DownloadManager.Query()
        query.setFilterById(downloadId)

        val cursor = dm.query(query)

        if (cursor.moveToFirst()) {

            val status = cursor.getInt(
                cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
            )

            if (status == DownloadManager.STATUS_SUCCESSFUL) {

                val uriString = cursor.getString(
                    cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI)
                )

                cursor.close()

                val uri = Uri.parse(uriString)
                return File(uri.path!!)
            }
        }

        cursor.close()
        return null
    }

}

