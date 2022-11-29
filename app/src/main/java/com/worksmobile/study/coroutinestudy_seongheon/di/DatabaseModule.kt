package com.worksmobile.study.coroutinestudy_seongheon.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.worksmobile.study.coroutinestudy_seongheon.Constants.IMAGE_DB_NAME
import com.worksmobile.study.coroutinestudy_seongheon.data.ImageDatabase
import com.worksmobile.study.coroutinestudy_seongheon.data.BookmarkDAO
import com.worksmobile.study.coroutinestudy_seongheon.data.TypeConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context, gson: Gson): ImageDatabase {
        return Room
            .databaseBuilder(context, ImageDatabase::class.java, IMAGE_DB_NAME)
            .addTypeConverter(TypeConverter(gson = gson))
            .build()
    }

    @Provides
    fun provideBookmarkDAO(appDatabase: ImageDatabase): BookmarkDAO {
        return appDatabase.bookmarkDAO
    }
}
