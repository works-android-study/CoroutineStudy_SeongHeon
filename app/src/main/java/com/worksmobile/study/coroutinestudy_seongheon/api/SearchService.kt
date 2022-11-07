package com.worksmobile.study.coroutinestudy_seongheon.api

import com.worksmobile.study.coroutinestudy_seongheon.data.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SearchService {
    @GET("/v1/search/image")
    suspend fun searchImages(
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String,
        @Query("query") query: String,
        @Query("display") display: Int? = null,
        @Query("start") start: Int? = null,
        @Query("sort") sort: String? = null,
        @Query("filter") filter: String? = null
    ): SearchResponse
}
