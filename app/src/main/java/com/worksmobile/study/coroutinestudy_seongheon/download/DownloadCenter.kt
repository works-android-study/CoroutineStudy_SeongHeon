package com.worksmobile.study.coroutinestudy_seongheon.download

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.work.*
import com.worksmobile.study.coroutinestudy_seongheon.data.Item
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.Executor

@Singleton
class DownloadCenter @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    fun startDownload(item: Item, downloadListener: DownloadListener) {
        val request = OneTimeWorkRequestBuilder<DownloadWorker>().apply {
            setInputData(
                workDataOf(
                    KEY_FOR_TITLE to item.title,
                    KEY_FOR_LINK to item.link
                )
            )
        }.build()

        workManager.enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request)
        workManager.getWorkInfoByIdLiveData(request.id).observeForever { workInfo ->
            downloadListener.onDownloadStateChanged(workInfo)
        }
    }

    companion object {
        const val WORK_NAME = "DOWNLOAD_WORK"
        const val KEY_FOR_TITLE = "LINK"
        const val KEY_FOR_LINK = "LINK"
        const val KEY_FOR_PROGRESS = "PROGRESS"
    }
}
