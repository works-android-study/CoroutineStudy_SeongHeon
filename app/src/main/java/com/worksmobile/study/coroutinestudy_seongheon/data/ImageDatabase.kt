package com.worksmobile.study.coroutinestudy_seongheon.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.worksmobile.study.coroutinestudy_seongheon.entity.BookmarkItem

@Database(entities = [BookmarkItem::class], version = 1, exportSchema = false)
@TypeConverters(TypeConverter::class)
abstract class ImageDatabase : RoomDatabase() {
    abstract val bookmarkDAO: BookmarkDAO
}
