package com.worksmobile.study.coroutinestudy_seongheon.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.worksmobile.study.coroutinestudy_seongheon.BuildConfig
import com.worksmobile.study.coroutinestudy_seongheon.R
import com.worksmobile.study.coroutinestudy_seongheon.download.DownloadCenter.Companion.KEY_FOR_PROGRESS
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DownloadWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val fileName = inputData.getString(DownloadCenter.KEY_FOR_TITLE) ?: return Result.failure()
        val fileLink = inputData.getString(DownloadCenter.KEY_FOR_LINK) ?: return Result.failure()

        if (fileName.isEmpty() || fileLink.isEmpty()) return Result.failure()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            channel.description = CHANNEL_DESCRIPTION

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            notificationManager?.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("${BuildConfig.APPLICATION_ID} Downloading File~")
            .setOngoing(true)
            .setProgress(0, 0, true)

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())

        val uri = saveFileAndReturnURI(fileName, fileLink)
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)

        return if (uri != null) {
            Result.success(workDataOf(DownloadCenter.KEY_FOR_LINK to uri.toString()))
        } else {
            Result.failure()
        }
    }

    private suspend fun saveFileAndReturnURI(fileName: String, fileUrl: String): Uri? {
        var count: Int
        var total = 0
        val byteArray = ByteArray(4096)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/*")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/Coroutine_Study")
            }

            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            return uri?.apply {
                val connection = URL(fileUrl).openConnection()
                connection.connect()

                val fileSize = connection.contentLength
                val inputStream = connection.getInputStream()

                context.contentResolver.openOutputStream(uri).use { outputStream ->
                    while (run {
                            count = inputStream.read(byteArray)
                            count
                        } != -1) {
                        total += count
                        setProgress(workDataOf(KEY_FOR_PROGRESS to (total * 100 / fileSize)))
                        outputStream?.write(byteArray, 0, count)
                    }
                }
            }
        } else {
            val target = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )

            URL(fileUrl).apply {
                val connection = URL(fileUrl).openConnection()
                connection.connect()

                val fileSize = connection.contentLength
                val inputStream = connection.getInputStream()

                FileOutputStream(target).use { outputStream ->
                    while (run {
                            count = inputStream.read(byteArray)
                            count
                        } != -1) {
                        total += count
                        setProgress(workDataOf(KEY_FOR_PROGRESS to (total * 100 / fileSize)))
                        outputStream.write(byteArray, 0, count)
                    }
                }
            }

            return target.toUri()
        }

    }

    companion object {
        private const val CHANNEL_NAME = "download_channel"
        private const val CHANNEL_DESCRIPTION = "download_file_worker_demo_description"
        private const val CHANNEL_ID = "download_file_worker_demo_channel_123456"
        private const val NOTIFICATION_ID = 1
    }
}
