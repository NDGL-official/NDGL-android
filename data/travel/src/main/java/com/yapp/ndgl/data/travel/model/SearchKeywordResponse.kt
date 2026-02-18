package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchKeywordResponse(
    val results: List<SearchResult>,
)

@Serializable
data class SearchResult(
    val googlePlaceId: String,
    val name: String,
)
