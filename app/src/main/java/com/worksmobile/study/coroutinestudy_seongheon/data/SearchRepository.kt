package com.worksmobile.study.coroutinestudy_seongheon.data

import com.worksmobile.study.coroutinestudy_seongheon.api.SearchService
import com.worksmobile.study.coroutinestudy_seongheon.common.TokenManager
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val service: SearchService
) {
    suspend fun searchImages(query: String): SearchResponse {
        return service.searchImages(TokenManager.CLIENT_ID, TokenManager.CLIENT_SECRET, query)
    }
}
