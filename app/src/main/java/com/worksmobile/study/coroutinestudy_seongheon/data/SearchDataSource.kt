package com.worksmobile.study.coroutinestudy_seongheon.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.worksmobile.study.coroutinestudy_seongheon.api.SearchService
import com.worksmobile.study.coroutinestudy_seongheon.common.TokenManager

class SearchDataSource(
    private val query: String,
    private val service: SearchService
) : PagingSource<Int, Item>() {
    override fun getRefreshKey(state: PagingState<Int, Item>): Int? {
        return state.anchorPosition?.let {
            val closestPageToPosition = state.closestPageToPosition(it)
            closestPageToPosition?.prevKey?.plus(defaultDisplay)
                ?: closestPageToPosition?.nextKey?.minus(defaultDisplay)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
        val start = params.key ?: defaultStart

        return try {
            val response = service.searchImages(
                TokenManager.CLIENT_ID,
                TokenManager.CLIENT_SECRET,
                query,
                start
            )
            val items = response.items
            val nextKey = getNextKey(items, start, params.loadSize)
            val prevKey = getPrevKey(start)

            LoadResult.Page(response.items, prevKey, nextKey)
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    private fun getNextKey(items: List<Item>, start: Int, loadSize: Int): Int? {
        return if (items.isEmpty()) {
            null
        } else {
            start + loadSize
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
