package com.yapp.ndgl.data.travel.model

data class AddPlaceEvent(
    val travelId: Long,
    val day: Int,
    val googlePlaceId: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val thumbnail: String?,
    val placeType: PlaceCategory,
    val address: String?,
    val phoneNumber: String?,
    val googleMapsUri: String?,
)
