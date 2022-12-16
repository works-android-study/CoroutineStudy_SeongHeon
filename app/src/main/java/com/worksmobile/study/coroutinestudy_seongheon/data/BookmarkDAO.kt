package com.worksmobile.study.coroutinestudy_seongheon.data

import androidx.room.*
import com.worksmobile.study.coroutinestudy_seongheon.entity.BookmarkItem

@Dao
interface BookmarkDAO {
    @Query("SELECT * FROM bookmark WHERE time != 0 ORDER BY time asc limit :loadSize OFFSET :index")
    fun selectAllBookmark(index : Int, loadSize : Int): List<BookmarkItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookmarkTime(bookmarkItem: BookmarkItem)

    @Delete
    fun deleteBookmarkTime(bookmarkItem: BookmarkItem)
}
