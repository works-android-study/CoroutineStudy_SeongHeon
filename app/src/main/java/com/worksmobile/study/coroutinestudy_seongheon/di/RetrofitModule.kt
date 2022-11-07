package com.worksmobile.study.coroutinestudy_seongheon.di

import com.worksmobile.study.coroutinestudy_seongheon.api.SearchService
import com.worksmobile.study.coroutinestudy_seongheon.common.Constants.ANOTHER_API
import com.worksmobile.study.coroutinestudy_seongheon.common.Constants.NAVER_API
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    private const val SEARCH_RETROFIT = "SEARCH_RETROFIT"
    private const val ANOTHER_RETROFIT = "ANOTHER_RETROFIT"

    @Named(SEARCH_RETROFIT)
    @Singleton
    @Provides
    fun provideSearchRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NAVER_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Named(ANOTHER_RETROFIT)
    @Singleton
    @Provides
    fun provideAnotherRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ANOTHER_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideSearchService(@Named(SEARCH_RETROFIT) retrofit: Retrofit): SearchService {
        return retrofit.create(SearchService::class.java)
    }
}
