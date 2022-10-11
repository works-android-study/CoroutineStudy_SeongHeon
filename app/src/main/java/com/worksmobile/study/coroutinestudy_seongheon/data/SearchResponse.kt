package com.worksmobile.study.coroutinestudy_seongheon.data

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    val lastBuildDate: String,
    val total: Int,
    val start: Int,
    val display: Int,
    val items: List<Item>
)

data class Item(
    val title: String,
    val link: String,
    val thumbnail: String,
    @SerializedName("sizeheight") val sizeHeight: Int,
    @SerializedName("sizewidth") val sizeWidth: Int
)
