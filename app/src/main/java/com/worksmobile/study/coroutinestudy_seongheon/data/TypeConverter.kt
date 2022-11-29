package com.worksmobile.study.coroutinestudy_seongheon.data

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.worksmobile.study.coroutinestudy_seongheon.entity.BookmarkItem

@ProvidedTypeConverter
class TypeConverter(private val gson: Gson) {
    @TypeConverter
    fun convertItemToJson(item: Item): String = gson.toJson(item)

    @TypeConverter
    fun convertStringToJson(value: String): Item = gson.fromJson(value, Item::class.java)
}
