package com.yapp.ndgl.feature.travel.traveldetail

import com.google.android.gms.maps.model.LatLng
import com.yapp.ndgl.core.base.UiIntent
import com.yapp.ndgl.core.base.UiSideEffect
import com.yapp.ndgl.core.base.UiState
import com.yapp.ndgl.feature.travel.model.AlternativePlace
import com.yapp.ndgl.feature.travel.model.ContentInfo
import com.yapp.ndgl.feature.travel.model.PlaceInfo
import com.yapp.ndgl.feature.travel.model.TipContent
import com.yapp.ndgl.feature.travel.model.TransportSegment
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

data class TravelDetailState(
    val contentInfo: ContentInfo = ContentInfo(),
    val country: String = "",
    val selectedDay: Int = 1,
    val itineraries: List<Itinerary> = emptyList(),
    val tempItineraries: List<Itinerary> = emptyList(),
    val isEditMode: Boolean = false,
    val selectedPlaceIds: Set<Long> = emptySet(),
    val showDeleteModal: Boolean = false,
    val showCancelEditModal: Boolean = false,
    val showTimelineBottomSheet: Boolean = false,
    val selectedPlace: TravelPlace? = null,
    val showPlaceBottomSheet: Boolean = false,
    val showTimeBottomSheet: Boolean = false,
    val showTransportBottomSheet: Boolean = false,
    val showCostModal: Boolean = false,
    val showMemoModal: Boolean = false,
) : UiState {
    val representativeLatLng: LatLng
        get() {
            val currentDayPlaces = itineraries.getOrNull(selectedDay - 1)?.places
            val firstPlaceInSelectedDay = currentDayPlaces?.firstOrNull()

            if (firstPlaceInSelectedDay != null) {
                return LatLng(firstPlaceInSelectedDay.placeInfo.latitude, firstPlaceInSelectedDay.placeInfo.longitude)
            }

            return itineraries
                .flatMap { it.places }
                .firstOrNull()
                ?.let { LatLng(it.placeInfo.latitude, it.placeInfo.longitude) } ?: LatLng(37.5665, 126.9780) // 모든 일차가 비어 있다면 '서울' 좌표 반환
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
    val id: Long,
    val placeInfo: PlaceInfo,
    val regularOpeningHours: String? = null,
    val userData: UserData = UserData(),
    val startTime: Duration,
    val transportToNext: TransportSegment? = null,
) {
    val duration: Duration
        get() = userData.estimatedDuration

    data class UserData(
        val memo: String? = null,
        val cost: Int? = null,
        val estimatedDuration: Duration = 1.hours,
    )
}

sealed interface TravelDetailIntent : UiIntent {
    data class SelectDay(val day: Int) : TravelDetailIntent
    data object ClickStartTimeSetting : TravelDetailIntent
    data object ClickEditTravel : TravelDetailIntent
    data object ClickAddScheduleButton : TravelDetailIntent
    data class CheckPlaceItem(val placeId: Long) : TravelDetailIntent
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
    data class ClickAddTime(val placeId: Long) : TravelDetailIntent
    data class ClickAddCost(val placeId: Long) : TravelDetailIntent
    data class ClickAddMemo(val placeId: Long) : TravelDetailIntent
    data class ClickFindRoute(val googleMapsUri: String) : TravelDetailIntent
    data object DismissPlaceBottomSheet : TravelDetailIntent
    data class NavigateToTravelPlaceDetail(val placeId: String) : TravelDetailIntent
    data object DismissTimeBottomSheet : TravelDetailIntent
    data class ConfirmDuration(val duration: Duration) : TravelDetailIntent
    data object DismissCostModal : TravelDetailIntent
    data class ConfirmCost(val cost: Int) : TravelDetailIntent
    data object DismissMemoModal : TravelDetailIntent
    data class ConfirmMemo(val memo: String) : TravelDetailIntent
}

sealed interface TravelDetailSideEffect : UiSideEffect {
    data object NavigateBack : TravelDetailSideEffect
    data class NavigateToTravelPlaceDetail(
        val googlePlaceId: String,
        val tipContent: TipContent?,
        val alternativePlaces: List<AlternativePlace>?,
    ) : TravelDetailSideEffect

    data class NavigateToBrowser(val url: String) : TravelDetailSideEffect
    data class NavigateToAddItinerary(
        val travelId: Long,
        val day: Int,
        val country: String,
        val representativeLatLng: LatLng,
    ) : TravelDetailSideEffect
}
