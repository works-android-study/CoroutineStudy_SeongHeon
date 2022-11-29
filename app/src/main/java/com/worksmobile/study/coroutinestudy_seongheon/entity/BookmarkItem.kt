package com.worksmobile.study.coroutinestudy_seongheon.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.worksmobile.study.coroutinestudy_seongheon.data.Item

@Entity(tableName = "bookmark")
data class BookmarkItem(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "item") val item: Item,
    @ColumnInfo(name = "time") val bookmarkCreatedTime: Long = System.currentTimeMillis()
)
