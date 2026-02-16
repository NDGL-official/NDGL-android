package com.yapp.ndgl.feature.travel.traveldetail

import com.yapp.ndgl.core.base.UiIntent
import com.yapp.ndgl.core.base.UiSideEffect
import com.yapp.ndgl.core.base.UiState
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

data class TravelDetailState(
    val contentInfo: ContentInfo = ContentInfo(),
    val selectedDay: Int = 1,
    val itineraries: List<Itinerary> = emptyList(),
    val tempItineraries: List<Itinerary> = emptyList(),
    val isEditMode: Boolean = false,
    val selectedPlaceIds: Set<Int> = emptySet(),
    val showDeleteModal: Boolean = false,
    val showCancelEditModal: Boolean = false,
    val showTimelineBottomSheet: Boolean = false,
    val selectedPlace: TravelPlace? = null,
    val showPlaceBottomSheet: Boolean = false,
    val showTimeBottomSheet: Boolean = false,
    val showTransportBottomSheet: Boolean = false,
    val showCostModal: Boolean = false,
    val showMemoModal: Boolean = false,
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
    val startTime: Duration? = null,
    val endTime: Duration? = null,
    val places: List<TravelPlace> = emptyList(),
) {
    val totalDuration: Duration
        get() = places.fold(0.hours) { acc, place ->
            acc + place.duration + (place.transportToNext?.duration ?: 0.hours)
        }
}

data class TravelPlace(
    val id: Int,
    val day: Int,
    val sequence: Int,
    val googlePlaceId: String,
    val thumbnail: String,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val regularOpeningHours: String,
    val googleMapsUri: String,
    val placeType: PlaceType,
    val userData: UserData = UserData(),
    val startTime: Duration,
    val transportToNext: TransportSegment? = null,
) {
    val duration: Duration
        get() = userData.estimatedDuration

    data class UserData(
        val memo: String? = null,
        val cost: Int? = null,
        val estimatedDuration: Duration = 0.hours,
    )
}

sealed interface TravelDetailIntent : UiIntent {
    data class SelectDay(val day: Int) : TravelDetailIntent
    data object ClickStartTimeSetting : TravelDetailIntent
    data object ClickEditTravel : TravelDetailIntent
    data object ClickAddScheduleButton : TravelDetailIntent
    data class CheckPlaceItem(val placeId: Int) : TravelDetailIntent
    data object CheckSelectAll : TravelDetailIntent
    data object ClickDeleteSelectedPlaces : TravelDetailIntent
    data object ConfirmDeleteSelectedPlaces : TravelDetailIntent
    data object DismissDeleteModal : TravelDetailIntent
    data object ClickBack : TravelDetailIntent
    data object ConfirmCancelEditMode : TravelDetailIntent
    data object DismissCancelEditModal : TravelDetailIntent
    data object LongClickPlaceItem : TravelDetailIntent
    data object DismissTimelineBottomSheet : TravelDetailIntent
    data class ConfirmTimelineSetting(val startTime: Duration) : TravelDetailIntent
    data class ReorderPlaces(val dayIndex: Int, val fromIndex: Int, val toIndex: Int) : TravelDetailIntent
    data object ConfirmEditMode : TravelDetailIntent
    data class ClickTransportSegment(val place: TravelPlace) : TravelDetailIntent
    data class ConfirmChangeTransportSegment(val segment: TransportSegment) : TravelDetailIntent
    data object DismissTransportBottomSheet : TravelDetailIntent
    data class ClickPlaceItem(val place: TravelPlace) : TravelDetailIntent
    data class ClickAddTime(val placeId: Int) : TravelDetailIntent
    data class ClickAddCost(val placeId: Int) : TravelDetailIntent
    data class ClickAddMemo(val placeId: Int) : TravelDetailIntent
    data class ClickFindRoute(val googleMapsUri: String) : TravelDetailIntent
    data object DismissPlaceBottomSheet : TravelDetailIntent
    data class NavigateToPlaceDetail(val placeId: String) : TravelDetailIntent
    data object DismissTimeBottomSheet : TravelDetailIntent
    data class ConfirmDuration(val duration: Duration) : TravelDetailIntent
    data object DismissCostModal : TravelDetailIntent
    data class ConfirmCost(val cost: Int) : TravelDetailIntent
    data object DismissMemoModal : TravelDetailIntent
    data class ConfirmMemo(val memo: String) : TravelDetailIntent
}

sealed interface TravelDetailSideEffect : UiSideEffect {
    data object NavigateBack : TravelDetailSideEffect
    data class NavigateToPlaceDetail(val placeId: String) : TravelDetailSideEffect
    data class NavigateToBrowser(val url: String) : TravelDetailSideEffect
}
