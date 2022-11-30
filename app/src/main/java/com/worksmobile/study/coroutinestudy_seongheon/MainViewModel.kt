package com.worksmobile.study.coroutinestudy_seongheon

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.work.*
import com.worksmobile.study.coroutinestudy_seongheon.data.ImageRepository
import com.worksmobile.study.coroutinestudy_seongheon.data.Item
import com.worksmobile.study.coroutinestudy_seongheon.data.SearchResultType
import com.worksmobile.study.coroutinestudy_seongheon.download.DownloadCenter
import com.worksmobile.study.coroutinestudy_seongheon.download.DownloadCenter.Companion.KEY_FOR_TITLE
import com.worksmobile.study.coroutinestudy_seongheon.download.DownloadState
import com.worksmobile.study.coroutinestudy_seongheon.download.DownloadWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ImageRepository,
    private val downloadCenter: DownloadCenter
) : ViewModel() {
    val searchQuery = mutableStateOf("")
    val searchResultType = mutableStateOf(SearchResultType.UNKNOWN)

    private val queryFlow = MutableSharedFlow<String>()

    private val _downloadEventFlow = MutableSharedFlow<DownloadState>()
    val downloadEventFlow = _downloadEventFlow.asSharedFlow()

    private val _downloadProgressStateFlow = MutableStateFlow(0)
    val downloadProgressStateFlow = _downloadProgressStateFlow.asStateFlow()

    val pagingDataFlow = queryFlow
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                searchResultType.value = SearchResultType.LOCAL
                selectBookmarkImage()
            } else {
                searchResultType.value = SearchResultType.REMOTE
                searchImages(query)
            }
        }
        .cachedIn(viewModelScope)

    private fun selectBookmarkImage() = repository.selectBookmarkImage()

    private fun searchImages(query: String) = repository.searchImages(query)

    private fun insertBookmarkImage(item: Item) = repository.insertBookmarkImage(item)

    private fun deleteBookmarkImage(item: Item) = repository.deleteBookmarkImage(item)

    fun handleQuery() {
        viewModelScope.launch {
            queryFlow.emit(searchQuery.value)
        }
    }

    fun handleBookmark(item: Item, isBookmark: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isBookmark) insertBookmarkImage(item)
            else deleteBookmarkImage(item)
        }
    }

    fun downloadImage(item: Item) {
        downloadCenter.startDownload(item) { workInfo ->
            viewModelScope.launch {
                when (workInfo?.state) {
                    WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> _downloadEventFlow.emit(DownloadState.DOWNLOAD_FAIL)
                    WorkInfo.State.SUCCEEDED -> _downloadEventFlow.emit(DownloadState.COMPLETE)
                    WorkInfo.State.ENQUEUED -> _downloadEventFlow.emit(DownloadState.START)
                    WorkInfo.State.RUNNING -> _downloadEventFlow.emit(DownloadState.PROGRESS)
                    else -> Unit
                }
            }
        }
    }
}
