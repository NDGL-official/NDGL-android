package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.Serializable

@Serializable
data class PlaceDetailResponse(
    val place: PlaceDetail,
)

@Serializable
data class PlaceDetail(
    val id: String,
    val name: String,
    val category: PlaceCategory,
    val thumbnail: String? = null,
    val nationalPhoneNumber: String? = null,
    val internationalPhoneNumber: String? = null,
    val formattedAddress: String? = null,
    val location: Location,
    val userRatingCount: Int? = null,
    val rating: Double? = null,
    val regularOpeningHours: List<String>? = null,
    val googleMapsUri: String? = null,
    val websiteUri: String? = null,
    val priceRange: PriceRange? = null,
) {
    @Serializable
    data class Location(
        val latitude: Double,
        val longitude: Double,
    )

    @Serializable
    data class PriceRange(
        val startPrice: Price,
        val endPrice: Price,
    )

    @Serializable
    data class Price(
        val currencyCode: String,
        val units: String,
        val symbol: String,
    )
}
