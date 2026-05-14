package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class YoutubeOembedResponse(
    val title: String,
    @SerialName("author_name") val authorName: String,
    @SerialName("thumbnail_url") val thumbnailUrl: String,
)
