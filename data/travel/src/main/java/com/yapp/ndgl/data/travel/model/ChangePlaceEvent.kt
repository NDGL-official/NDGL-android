package com.yapp.ndgl.data.travel.model

data class ChangePlaceEvent(
    val travelId: Long,
    val itineraryId: Long,
    val day: Int,
    val oldGooglePlaceId: String,
    val newGooglePlaceId: String,
)
