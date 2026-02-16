package com.yapp.ndgl.feature.travel.followtravel

import com.yapp.ndgl.core.base.UiIntent
import com.yapp.ndgl.core.base.UiSideEffect
import com.yapp.ndgl.core.base.UiState
import com.yapp.ndgl.feature.travel.model.PlaceType
import com.yapp.ndgl.feature.travel.model.TransportSegment
import kotlin.time.Duration

data class FollowTravelState(
    val contentInfo: ContentInfo = ContentInfo(),
    val country: String = "",
    val selectedDay: Int = 1,
    val itineraries: List<Itinerary> = emptyList(),
) : UiState

data class ContentInfo(
    val travelId: String = "",
    val country: String = "",
    val city: String = "",
    val budgetPerPerson: Budget = Budget(0),
    val nights: Int = 0,
    val days: Int = 0,
    val videoInfo: VideoInfo = VideoInfo(),
)

data class VideoInfo(
    val title: String = "",
    val name: String = "",
    val profileImage: String = "",
    val thumbnail: String = "",
    val link: String = "",
    val summary: String = "",
)

data class Budget(
    val amount: Int,
) {
    fun formatString(): String {
        return when {
            amount < 10000 -> "만원"
            amount % 10000 == 0 -> "${amount / 10000}만원"
            amount % 1000 == 0 -> "${amount / 10000}만 ${(amount % 10000) / 1000}천원"
            else -> "${amount}원"
        }
    }
}

data class Itinerary(
    val budget: Budget = Budget(0),
    val places: List<TravelPlace> = emptyList(),
    val transportSegments: List<TransportSegment> = emptyList(),
)

data class TravelPlace(
    val id: Int,
    val day: Int,
    val sequence: Int,
    val estimatedDuration: Duration,
    val googlePlaceId: String,
    val thumbnail: String,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val regularOpeningHours: String,
    val googleMapsUri: String,
    val placeType: PlaceType,
)

sealed interface FollowTravelIntent : UiIntent {
    data class SelectDay(val day: Int) : FollowTravelIntent
    data object ClickFollowTravel : FollowTravelIntent
}

sealed interface FollowTravelSideEffect : UiSideEffect
