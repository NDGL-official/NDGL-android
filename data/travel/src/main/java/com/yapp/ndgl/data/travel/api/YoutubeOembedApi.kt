package com.yapp.ndgl.data.travel.api

import com.yapp.ndgl.data.travel.model.YoutubeOembedResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeOembedApi {
    @GET("oembed")
    suspend fun getMetadata(
        @Query("url") url: String,
        @Query("format") format: String = "json",
    ): YoutubeOembedResponse
}
