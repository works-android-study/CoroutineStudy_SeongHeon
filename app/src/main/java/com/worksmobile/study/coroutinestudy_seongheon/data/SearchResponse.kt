package com.worksmobile.study.coroutinestudy_seongheon.data

import com.google.gson.annotations.SerializedName
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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
    @SerializedName("sizewidth") val sizeWidth: Int,
    val isBookmark: Boolean = false
): java.io.Serializable {
    fun getEncodedItem(): Item {
        return Item(
            this.title,
            URLEncoder.encode(
                this.link,
                StandardCharsets.UTF_8.toString()
            ),
            URLEncoder.encode(
                this.thumbnail,
                StandardCharsets.UTF_8.toString()
            ),
            this.sizeWidth,
            this.sizeHeight
        )
    }
}
