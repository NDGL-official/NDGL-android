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
    val countryCode: String = "",
    val days: Int = 1,
    val selectedDay: Int = 1,
    val itineraries: List<Itinerary> = emptyList(),
    val tempItineraries: List<Itinerary> = emptyList(),
    val isEditMode: Boolean = false,
    val selectedPlaceIds: Set<Long> = emptySet(),
    val showDeleteModal: Boolean = false,
    val showCancelEditModal: Boolean = false,
    val showStartTimeSettingBottomSheet: Boolean = false,
    val selectedPlace: TravelPlace? = null,
    val showPlaceBottomSheet: Boolean = false,
    val showTimeBottomSheet: Boolean = false,
    val showTransportBottomSheet: Boolean = false,
    val showCostModal: Boolean = false,
    val showMemoModal: Boolean = false,
    val availableTransports: List<TransportSegment> = emptyList(),
    val isLoadingTransports: Boolean = false,
) : UiState {
    val currentItineraries: List<Itinerary>
        get() = if (isEditMode) tempItineraries else itineraries

    val currentItinerary: Itinerary?
        get() = currentItineraries.getOrNull(selectedDay - 1)

    val currentPlaces: List<TravelPlace>
        get() = currentItinerary?.places.orEmpty()

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

    // 헤더(0) + stickyHeader(1) + 맵 아이템(2) = 3개가 장소 아이템 앞에 위치
    val placesOffset: Int
        get() = 3
}

data class Itinerary(
    val isStartTimeSet: Boolean = false,
    val places: List<TravelPlace> = emptyList(),
) {
    val startTime = places.firstOrNull()?.startTime ?: DEFAULT_START_TIME.hours

    val totalDuration: Duration
        get() = places.fold(0.hours) { acc, place ->
            acc + place.duration + (place.transportToNext?.duration ?: 0.hours)
        }

    companion object {
        const val DEFAULT_START_TIME = 8
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
    data object DismissStartTimeSettingBottomSheet : TravelDetailIntent
    data class ConfirmStartTimeSetting(val startTime: Duration) : TravelDetailIntent
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
        val countryCode: String,
        val representativeLatLng: LatLng,
    ) : TravelDetailSideEffect

    data object NavigateToMyTravel : TravelDetailSideEffect
    data class ScrollToPlace(val placeId: Long) : TravelDetailSideEffect
}
