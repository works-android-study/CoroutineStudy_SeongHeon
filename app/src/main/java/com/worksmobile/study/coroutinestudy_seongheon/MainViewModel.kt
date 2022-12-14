package com.worksmobile.study.coroutinestudy_seongheon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.work.*
import com.worksmobile.study.coroutinestudy_seongheon.data.ImageRepository
import com.worksmobile.study.coroutinestudy_seongheon.data.Item
import com.worksmobile.study.coroutinestudy_seongheon.data.SearchResultType
import com.worksmobile.study.coroutinestudy_seongheon.download.DownloadCenter
import com.worksmobile.study.coroutinestudy_seongheon.download.DownloadCenter.Companion.KEY_FOR_LINK
import com.worksmobile.study.coroutinestudy_seongheon.download.DownloadCenter.Companion.KEY_FOR_PROGRESS
import com.worksmobile.study.coroutinestudy_seongheon.download.DownloadState
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
    private val _searchQueryFlow = MutableStateFlow("")
    val searchQueryFlow = _searchQueryFlow.asStateFlow()

    private val _searchResultTypeFlow = MutableStateFlow(SearchResultType.UNKNOWN)
    val searchResultTypeFlow = _searchResultTypeFlow.asStateFlow()

    private val _downloadEventFlow = MutableSharedFlow<DownloadState>()
    val downloadEventFlow = _downloadEventFlow.asSharedFlow()

    private val searchButtonEventFlow = MutableSharedFlow<String>()

    val pagingDataFlow = searchButtonEventFlow
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                selectBookmarkImage()
            } else {
                searchImages(query)
            }
        }
        .cachedIn(viewModelScope)

    private fun selectBookmarkImage() = repository.selectBookmarkImage()

    private fun searchImages(query: String) = repository.searchImages(query)

    private fun insertBookmarkImage(item: Item) = repository.insertBookmarkImage(item)

    private fun deleteBookmarkImage(item: Item) = repository.deleteBookmarkImage(item)

    fun changeQuery(newQuery: String) {
        _searchQueryFlow.value = newQuery
    }

    fun handleQuery() {
        viewModelScope.launch {
            searchQueryFlow.value.let {
                searchButtonEventFlow.emit(it)
                if (it.isEmpty()) _searchResultTypeFlow.value = SearchResultType.LOCAL
                else _searchResultTypeFlow.value = SearchResultType.REMOTE
            }
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
                val resultUri = workInfo?.outputData?.getString(KEY_FOR_LINK)
                val progress = workInfo?.progress?.getInt(KEY_FOR_PROGRESS, 0)
                when (workInfo?.state) {
                    WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> _downloadEventFlow.emit(
                        DownloadState.Fail
                    )
                    WorkInfo.State.SUCCEEDED -> _downloadEventFlow.emit(
                        DownloadState.Complete(
                            resultUri
                        )
                    )
                    WorkInfo.State.ENQUEUED -> _downloadEventFlow.emit(DownloadState.Start)
                    WorkInfo.State.RUNNING -> _downloadEventFlow.emit(
                        DownloadState.Progress(
                            progress
                        )
                    )
                    else -> Unit
                }
            }
        }
    }
}
