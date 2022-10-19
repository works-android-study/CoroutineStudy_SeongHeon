package com.worksmobile.study.coroutinestudy_seongheon

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worksmobile.study.coroutinestudy_seongheon.data.Item
import com.worksmobile.study.coroutinestudy_seongheon.data.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: SearchRepository,
) : ViewModel() {
    val searchResult = mutableStateListOf<Item>()
    val searchQuery = mutableStateOf("")

    fun searchImages() {
        viewModelScope.launch {
            val response = repository.searchImages(searchQuery.value)
            searchResult.clear()
            searchResult.addAll(response.items)
        }
    }
}
