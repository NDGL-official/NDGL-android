package com.yapp.ndgl.feature.travel.model

import com.yapp.ndgl.core.util.formatDecimal
import com.yapp.ndgl.data.travel.model.PlaceDetailResponse
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

data class PlaceInfo(
    val googlePlaceId: String = "",
    val name: String = "",
    val placeType: PlaceType = PlaceType.ATTRACTION,
    val day: Int = 0,
    val sequence: Int = 0,
    val thumbnail: String? = null,
    val priceRange: PriceRange? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
    val googleMapsUri: String? = null,
    val websiteUrl: String? = null,
    val rating: Double? = null,
    val userRatingCount: Int? = null,
    val estimatedDuration: Duration = 1.hours,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    // 화면마다 필수로 표시되는 값은 아닌 부분
    val tipContent: TipContent? = null,
    val alternativePlaces: List<AlternativePlace>? = null,
    val isBookMarked: Boolean = false,
) {
    val formattedRatingCount: String
        get() = userRatingCount?.formatDecimal() ?: ""
}

fun PlaceDetailResponse.toPlaceInfo(): PlaceInfo {
    return PlaceInfo(
        googlePlaceId = place.id,
        name = place.name,
        placeType = place.category.toPlaceType(),
        day = 0,
        sequence = 0,
        latitude = place.location.latitude,
        longitude = place.location.longitude,
        thumbnail = place.thumbnail,
        priceRange = place.priceRange?.let {
            PriceRange(
                startPrice = Price(
                    currencyCode = it.startPrice.currencyCode,
                    units = it.startPrice.units,
                    symbol = it.startPrice.symbol,
                ),
                endPrice = Price(currencyCode = it.endPrice.currencyCode, units = it.endPrice.units, symbol = it.endPrice.symbol),
            )
        },
        rating = place.rating,
        userRatingCount = place.userRatingCount,
        address = place.formattedAddress,
        phoneNumber = place.nationalPhoneNumber ?: place.internationalPhoneNumber,
        googleMapsUri = place.googleMapsUri,
        websiteUrl = place.websiteUri,
    )
}

fun List<String>?.toOpeningHours(startDate: String, day: Int): String? {
    if (this.isNullOrEmpty() || startDate.isBlank() || day <= 0) return null

    val targetDayOfWeek = runCatching {
        val travelStartDate = LocalDate.parse(startDate)
        val targetDate = travelStartDate.plus(day - 1, DateTimeUnit.DAY)
        targetDate.dayOfWeek
    }.getOrNull() ?: return null

    val dayOfWeekName = when (targetDayOfWeek) {
        DayOfWeek.MONDAY -> "월요일"
        DayOfWeek.TUESDAY -> "화요일"
        DayOfWeek.WEDNESDAY -> "수요일"
        DayOfWeek.THURSDAY -> "목요일"
        DayOfWeek.FRIDAY -> "금요일"
        DayOfWeek.SATURDAY -> "토요일"
        DayOfWeek.SUNDAY -> "일요일"
    }

    val openingHour = this.find { it.startsWith(dayOfWeekName) }
    return openingHour?.substringAfter(":")?.trim()
}
