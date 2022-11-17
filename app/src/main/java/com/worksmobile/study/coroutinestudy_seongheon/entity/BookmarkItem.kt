package com.worksmobile.study.coroutinestudy_seongheon.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.worksmobile.study.coroutinestudy_seongheon.data.Item

@Entity
data class BookmarkItem(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "item") val item: Item,
    @ColumnInfo(name = "time") val bookmarkCreatedTime: Long
)
