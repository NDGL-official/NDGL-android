package com.yapp.ndgl.feature.travel.followtravel

import com.yapp.ndgl.core.base.UiIntent
import com.yapp.ndgl.core.base.UiSideEffect
import com.yapp.ndgl.core.base.UiState
import com.yapp.ndgl.feature.travel.model.AlternativePlace
import com.yapp.ndgl.feature.travel.model.ContentInfo
import com.yapp.ndgl.feature.travel.model.PlaceInfo
import com.yapp.ndgl.feature.travel.model.TipContent
import com.yapp.ndgl.feature.travel.model.TransportSegment

data class FollowTravelState(
    val travelId: Long = 0,
    val days: Int = 1,
    val countryCode: String = "",
    val itineraries: List<Itinerary> = emptyList(),
    val contentInfo: ContentInfo = ContentInfo(),
    val selectedDay: Int = 1,
) : UiState

data class Itinerary(
    val places: List<TravelPlace> = emptyList(),
)

data class TravelPlace(
    val id: Long = 0,
    val placeInfo: PlaceInfo = PlaceInfo(),
    val regularOpeningHours: String? = null,
    val transportToNext: TransportSegment? = null,
)

sealed interface FollowTravelIntent : UiIntent {
    data class SelectDay(val day: Int) : FollowTravelIntent
    data object ClickFollowTravel : FollowTravelIntent
    data class ClickPlaceItem(val place: TravelPlace) : FollowTravelIntent
}

sealed interface FollowTravelSideEffect : UiSideEffect {
    data class NavigateToFollowPlaceDetail(
        val placeId: String,
        val tipContent: TipContent?,
        val alternativePlaces: List<AlternativePlace>?,
    ) :
        FollowTravelSideEffect
}
