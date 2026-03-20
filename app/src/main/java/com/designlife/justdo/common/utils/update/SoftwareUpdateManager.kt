package com.designlife.justdo.common.utils.update

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.designlife.justdo.BuildConfig
import com.designlife.justdo.common.domain.repositories.SoftwareUpdateRepository
import com.designlife.orchestrator.NotificationScheduler
import com.designlife.orchestrator.data.NotificationInfo
import com.designlife.orchestrator.data.NotificationStatus
import com.designlife.orchestrator.data.NotificationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import kotlin.math.absoluteValue

class SoftwareUpdateManager(
    private val context: Context,
    private val scope: CoroutineScope,
    private val updateRepository: SoftwareUpdateRepository,
    private val notificationScheduler: NotificationScheduler
) {

    private var downloadId: Long = 0L
    private var sha256CheckSum: String = ""
    private var isReceiverRegistered = false

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Log.i("SoftwareUpdateManager", "onReceive: downloadReceiver :: id : $id")

            if (id == downloadId) {
                handleDownloadCompleted(context, id)
            }
        }

        private fun handleDownloadCompleted(context: Context, id: Long) {
            Log.i("SoftwareUpdateManager", "onReceive: handleDownloadCompleted :: id : $id")

            val apkFile = getDownloadedFile(context, id)
            if (apkFile == null) {
                Log.e("SoftwareUpdateManager", "Failed to get downloaded file")
                return
            }

            val calculated = sha256(apkFile)
            Log.i("SoftwareUpdateManager", "onReceive: handleDownloadCompleted :: calculated-sha : $calculated :: existing-sha256CheckSum $sha256CheckSum")

            if (calculated == sha256CheckSum) {
                val uri = getDownloadedApkUri(context, downloadId)
                if (uri != null) {
                    try {
                        installApk(context, uri)
                        Log.i("SoftwareUpdateManager", "APK installation intent started successfully")
                    } catch (e: Exception) {
                        Log.e("SoftwareUpdateManager", "Failed to install APK", e)
                        apkFile.delete()
                    }
                } else {
                    Log.e("SoftwareUpdateManager", "Failed to get download URI")
                    apkFile.delete()
                }
            } else {
                Log.e(
                    "SoftwareUpdateManager",
                    "SHA256 checksum mismatch. Calculated: $calculated, Expected: $sha256CheckSum"
                )
                apkFile.delete()
            }

            // Unregister receiver after download completion
            unregisterDownloadReceiver()
        }
    }

    fun checkForUpdate() {
        scope.launch(Dispatchers.IO) {
            try {
                updateRepository.fetchReleaseUpdates()?.let { appMetaResponse ->
                    if (isUpdateAvailable(appMetaResponse.tag_name)) {
                        val updateNotification = NotificationInfo(
                            scheduledTime = System.currentTimeMillis() + 10,
                            taskTitle = "Software Update Available",
                            taskSubTitle = "Tap to install latest update",
                            taskId = 10001,
                            notificationType = NotificationType.APP_UPDATE,
                            notificationStatus = NotificationStatus.ACTIVE,
                            createdTime = System.currentTimeMillis(),
                            deliveredTime = 0L
                        )
                        notificationScheduler.scheduleNotification(updateNotification)
                        Log.i("SoftwareUpdateManager", "Update available: ${appMetaResponse.tag_name}")
                    } else {
                        Log.i("SoftwareUpdateManager", "No update available")
                        withContext(Dispatchers.Main.immediate) {
                            Toast.makeText(context, "Installed software version is updated", Toast.LENGTH_SHORT).show()
                        }                    }
                }
            } catch (e: Exception) {
                Log.e("SoftwareUpdateManager", "Error checking for updates", e)
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
            try {
                Log.i("SoftwareUpdateManager", "Starting installUpdate process")
                updateRepository.fetchReleaseUpdates()?.let { appMetaResponse ->
                    appMetaResponse.assets.firstOrNull()?.let { appMeta ->
                        downloadId = downloadApk(context, appMeta.browser_download_url, appMeta.name)
                        sha256CheckSum = appMeta.digest.removePrefix("sha256:")

                        Log.i("SoftwareUpdateManager", "Download started with ID: $downloadId, Expected SHA256: $sha256CheckSum")
                        scope.launch(Dispatchers.Main) {
                            registerDownloadReceiver()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SoftwareUpdateManager", "Error initiating update", e)
            }
        }
    }

    private fun registerDownloadReceiver() {
        if (!isReceiverRegistered) {
            try {
                Log.i("SoftwareUpdateManager", "=== Starting Receiver Registration ===")
                Log.i("SoftwareUpdateManager", "Context type: ${context::class.simpleName}")
                Log.i("SoftwareUpdateManager", "Context is Activity: ${context is android.app.Activity}")

                val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)

                ContextCompat.registerReceiver(
                    context,
                    downloadReceiver,
                    intentFilter,
                    ContextCompat.RECEIVER_EXPORTED
                )

                isReceiverRegistered = true
                Log.i("SoftwareUpdateManager", "Download receiver registered successfully")
                Log.i("SoftwareUpdateManager", "Receiver will listen for: ${DownloadManager.ACTION_DOWNLOAD_COMPLETE}")
                Log.i("SoftwareUpdateManager", "Download ID to match: $downloadId")
                Log.i("SoftwareUpdateManager", "=== Receiver Registration Complete ===")

            } catch (e: Exception) {
                Log.e("SoftwareUpdateManager", "Error registering download receiver", e)
                e.printStackTrace()
                isReceiverRegistered = false
            }
        } else {
            Log.w("SoftwareUpdateManager", "Receiver already registered, skipping")
        }
    }

    private fun unregisterDownloadReceiver() {
        if (isReceiverRegistered) {
            try {
                context.unregisterReceiver(downloadReceiver)
                isReceiverRegistered = false
                Log.i("SoftwareUpdateManager", "Download receiver unregistered")
            } catch (e: Exception) {
                Log.e("SoftwareUpdateManager", "Error unregistering download receiver", e)
            }
        }
    }

    private fun downloadApk(
        context: Context,
        apkUrl: String,
        packageName: String,
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
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        return dm.enqueue(request)
    }

    private fun getDownloadedApkUri(context: Context, downloadId: Long): Uri? {
        return try {
            val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.getUriForDownloadedFile(downloadId)
        } catch (e: Exception) {
            Log.e("SoftwareUpdateManager", "Error getting downloaded file URI", e)
            null
        }
    }

    private fun sha256(file: File): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")

            FileInputStream(file).use { input ->
                val buffer = ByteArray(8192)
                var read: Int

                while (input.read(buffer).also { read = it } != -1) {
                    digest.update(buffer, 0, read)
                }
            }

            val hash = digest.digest()
            hash.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Log.e("SoftwareUpdateManager", "Error calculating SHA256", e)
            ""
        }
    }

    private fun installApk(context: Context, uri: Uri) {
        try {
            // Convert file:// URI to content:// URI using FileProvider
            val contentUri = if (uri.scheme == "file") {
                val file = File(uri.path!!)
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } else {
                uri
            }

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

            Log.i("SoftwareUpdateManager", "Starting APK installation with URI: $contentUri")
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e("SoftwareUpdateManager", "No activity found to handle APK installation", e)
            throw e
        } catch (e: Exception) {
            Log.e("SoftwareUpdateManager", "Error installing APK", e)
            throw e
        }
    }

    private fun getDownloadedFile(context: Context, downloadId: Long): File? {
        return try {
            val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val query = DownloadManager.Query().apply { setFilterById(downloadId) }

            dm.query(query)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(
                        cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
                    )

                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        val uriString = cursor.getString(
                            cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI)
                        )

                        val uri = Uri.parse(uriString)
                        val file = File(uri.path!!)

                        Log.i("SoftwareUpdateManager", "Downloaded file found at: ${file.absolutePath}")
                        return@use file
                    } else {
                        Log.e("SoftwareUpdateManager", "Download status is not successful: $status")
                    }
                }
                null
            }
        } catch (e: Exception) {
            Log.e("SoftwareUpdateManager", "Error getting downloaded file", e)
            null
        }
    }

    fun cleanup() {
        unregisterDownloadReceiver()
    }
}
