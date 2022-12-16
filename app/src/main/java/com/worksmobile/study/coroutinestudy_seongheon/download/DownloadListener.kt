package com.worksmobile.study.coroutinestudy_seongheon.download

import androidx.work.WorkInfo

interface DownloadListener {
    fun onDownloadStateChanged(workInfo: WorkInfo?)
}
