package com.worksmobile.study.coroutinestudy_seongheon.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.worksmobile.study.coroutinestudy_seongheon.api.SearchService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImageRepository @Inject constructor(
    private val service: SearchService,
    private val imageDao: BookmarkDAO
) {
    fun searchImages(query: String): Flow<PagingData<Item>> {
        return Pager(
            config = PagingConfig(
                pageSize = SearchDataSource.defaultDisplay,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                SearchDataSource(query, service)
            }
        ).flow
    }

    fun selectBookmarkImage(): Flow<PagingData<Item>> {
        return Pager(
            config = PagingConfig(
                pageSize = SearchDataSource.defaultDisplay,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                BookmarkDataSource(imageDao)
            }
        ).flow
    }
}
