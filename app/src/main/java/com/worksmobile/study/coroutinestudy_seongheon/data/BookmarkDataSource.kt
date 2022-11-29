package com.worksmobile.study.coroutinestudy_seongheon.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookmarkDataSource(
    private val dao: BookmarkDAO
) : PagingSource<Int, Item>() {
    //<key, 반환 아이템>
    //flow, observable 모두 지원
    override fun getRefreshKey(state: PagingState<Int, Item>): Int? {
        //refresh할때 쓰이는 키, 다시 시작할 key를 반환해주면 됨
        return state.anchorPosition?.let {
            val closestPageToPosition = state.closestPageToPosition(it)
            closestPageToPosition?.prevKey?.plus(defaultDisplay)
                ?: closestPageToPosition?.nextKey?.minus(defaultDisplay)
        }
    }

    // load()의 인자로 넘어오는 params의 key 값이 페이지 정보
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
        val start = params.key ?: defaultStart

        return try {
            val items = withContext(Dispatchers.IO) {
                dao.selectAllBookmark(start - 1, defaultDisplay).map { it.item }
            }
            val nextKey = getNextKey(items, start)
            val prevKey = getPrevKey(start)
            LoadResult.Page(items, prevKey, nextKey)
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    private fun getNextKey(items: List<Item>, start: Int): Int? {
        return if (items.isEmpty()) {
            null
        } else {
            start + defaultDisplay
        }
    }

    private fun getPrevKey(start: Int): Int? {
        return if (start == defaultStart) {
            null
        } else {
            start - defaultDisplay
        }
    }

    companion object {
        const val defaultStart = 1
        const val defaultDisplay = 10
    }
}
