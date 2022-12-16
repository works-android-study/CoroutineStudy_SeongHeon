package com.worksmobile.study.coroutinestudy_seongheon.download

sealed class DownloadState {
    class Complete(val uri: String?) : DownloadState()
    class Progress(val progress: Int? = 0) : DownloadState()
    object Start : DownloadState()
    object Fail : DownloadState()
}
