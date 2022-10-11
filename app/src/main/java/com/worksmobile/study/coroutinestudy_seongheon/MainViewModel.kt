package com.worksmobile.study.coroutinestudy_seongheon

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worksmobile.study.coroutinestudy_seongheon.data.Item
import com.worksmobile.study.coroutinestudy_seongheon.data.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: SearchRepository
) : ViewModel() {
    private val _searchResult: MutableLiveData<Item> = MutableLiveData()
    val searchResult: LiveData<Item>
        get() = _searchResult
    val searchQuery = mutableStateOf("")

    fun searchImages() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.searchImages(searchQuery.value)
            _searchResult.postValue(response.items[0])
        }
    }
}
