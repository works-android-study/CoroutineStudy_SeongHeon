package com.worksmobile.study.coroutinestudy_seongheon.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.worksmobile.study.coroutinestudy_seongheon.entity.BookmarkItem

@Dao
interface BookmarkDAO {
    @Query("SELECT * FROM user WHERE bookmark != 0")
    fun selectAllBookmark(): List<BookmarkItem>

    @Insert
    fun insertBookmarkTime(bookmarkItem: BookmarkItem)

    @Delete
    fun deleteBookmarkTime(bookmarkItem: BookmarkItem)
}
