package com.worksmobile.study.coroutinestudy_seongheon

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.worksmobile.study.coroutinestudy_seongheon.data.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: SearchRepository,
) : ViewModel() {
    val searchQuery = mutableStateOf("")
    private val queryFlow = MutableSharedFlow<String>()

    val pagingDataFlow = queryFlow
        .flatMapLatest {
            searchImages(it)
        }
        .cachedIn(viewModelScope)

    private fun searchImages(query: String) = repository.searchImages(query)

    fun handleQuery() {
        viewModelScope.launch {
            queryFlow.emit(searchQuery.value)
        }
    }

    fun TestFunction() {
        val Test = searchQuery.component1();
        val Test2 = searchQuery.component1().plus(1).plus(2).plus(3).plus(4).plus(5).length.and(1).and(2)
    }

}
