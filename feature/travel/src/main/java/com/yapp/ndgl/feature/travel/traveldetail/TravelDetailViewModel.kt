package com.yapp.ndgl.feature.travel.traveldetail

import com.yapp.ndgl.core.base.BaseViewModel
import com.yapp.ndgl.feature.travel.model.PlaceType
import com.yapp.ndgl.feature.travel.model.TipContent
import com.yapp.ndgl.feature.travel.model.TransportSegment
import com.yapp.ndgl.feature.travel.model.TransportType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

// TODO("테스트용으로 지워야함")
private const val TEST_PROFILE_IMAGE_URL =
    "https://yt3.ggpht.com/Sr5y4IxegXCEZ0SYNvFB749crrAZmNpurZqfq2KvPEpiCYeakoMjBWMnW_56rMuYW_HipJOBRtU=s88-c-k-c0x00ffffff-no-rj"
private const val TEST_THUMBNAIL_URL =
    "https://picsum.photos/200"

@HiltViewModel(assistedFactory = TravelDetailViewModel.Factory::class)
class TravelDetailViewModel @AssistedInject constructor(
    @Assisted private val travelId: Long,
) : BaseViewModel<TravelDetailState, TravelDetailIntent, TravelDetailSideEffect>(
    initialState = TravelDetailState(),
) {
    init {
        loadTravelData()
        applyDefaultStartTime()
    }

    private fun applyDefaultStartTime() {
        reduce {
            val updatedItineraries = itineraries.map { itinerary ->
                val baseStartTime = itinerary.startTime ?: DEFAULT_START_TIME.hours
                val updatedPlaces = calculatePlaceStartTimes(itinerary.places, baseStartTime)
                itinerary.copy(
                    places = updatedPlaces,
                )
            }

            copy(itineraries = updatedItineraries)
        }
    }

    private fun loadTravelData() {
        // TODO: Load from repository
        val loadedItineraries = listOf(
            Itinerary(
                places = listOf(
                    TravelPlace(
                        id = 1,
                        day = 1,
                        sequence = 1,
                        googlePlaceId = "ChIJKWGrTn8hQTUR7zeTLtzYJL4",
                        thumbnail = TEST_THUMBNAIL_URL,
                        latitude = 35.6585805,
                        longitude = 139.7454329,
                        name = "도쿄 타워",
                        regularOpeningHours = "09:00~23:00",
                        googleMapsUri = "",
                        placeType = PlaceType.ATTRACTION,
                        userData = TravelPlace.UserData(estimatedDuration = 90.minutes),
                        transportToNext = TransportSegment(type = TransportType.CAR, duration = 25.minutes, distance = 3500),
                        startTime = 0.hours,
                    ),
                    TravelPlace(
                        id = 2,
                        day = 1,
                        sequence = 2,
                        googlePlaceId = "ChIJSc8jdZORQTURu6BMwxrKbGg",
                        thumbnail = TEST_THUMBNAIL_URL,
                        latitude = 35.6654,
                        longitude = 139.7707,
                        name = "츠키지 스시 다이",
                        regularOpeningHours = "11:00~22:00",
                        googleMapsUri = "",
                        placeType = PlaceType.RESTAURANT,
                        userData = TravelPlace.UserData(estimatedDuration = 60.minutes),
                        transportToNext = TransportSegment(type = TransportType.WALK, duration = 10.minutes, distance = 800),
                        startTime = 0.hours,
                    ),
                    TravelPlace(
                        id = 3,
                        day = 1,
                        sequence = 3,
                        googlePlaceId = "ChIJSc8jdZORQTURu6BMwxrKbGg",
                        thumbnail = TEST_THUMBNAIL_URL,
                        latitude = 35.6812,
                        longitude = 139.7671,
                        name = "신주쿠 프린스 호텔",
                        regularOpeningHours = "24시간",
                        googleMapsUri = "",
                        placeType = PlaceType.ACCOMMODATION,
                        userData = TravelPlace.UserData(estimatedDuration = 120.minutes),
                        startTime = 0.hours,
                        transportToNext = TransportSegment(type = TransportType.CAR, duration = 15.minutes, distance = 2100),
                    ),
                    TravelPlace(
                        id = 4,
                        day = 1,
                        sequence = 4,
                        googlePlaceId = "ChIJSc8jdZORQTURu6BMwxrKbGg",
                        thumbnail = TEST_THUMBNAIL_URL,
                        latitude = 35.6944,
                        longitude = 139.7006,
                        name = "오모이데 요코초",
                        regularOpeningHours = "17:00~24:00",
                        googleMapsUri = "",
                        placeType = PlaceType.RESTAURANT,
                        userData = TravelPlace.UserData(estimatedDuration = 90.minutes),
                        startTime = 0.hours,
                        transportToNext = null,
                    ),
                ),
            ),
            Itinerary(
                places = listOf(
                    TravelPlace(
                        id = 8,
                        day = 2,
                        sequence = 1,
                        googlePlaceId = "ChIJSc8jdZORQTURu6BMwxrKbGg",
                        thumbnail = TEST_THUMBNAIL_URL,
                        latitude = 35.7148,
                        longitude = 139.7967,
                        name = "센소지 절",
                        regularOpeningHours = "06:00~17:00",
                        googleMapsUri = "",
                        placeType = PlaceType.ATTRACTION,
                        userData = TravelPlace.UserData(estimatedDuration = 90.minutes),
                        startTime = 0.hours,
                        transportToNext = TransportSegment(type = TransportType.WALK, duration = 8.minutes, distance = 600),
                    ),
                    TravelPlace(
                        id = 9,
                        day = 2,
                        sequence = 2,
                        googlePlaceId = "ChIJU8SvORUCNTERtCYCUqP64OY",
                        thumbnail = TEST_THUMBNAIL_URL,
                        latitude = 35.7120,
                        longitude = 139.7960,
                        name = "아사쿠사 카페",
                        regularOpeningHours = "08:00~20:00",
                        googleMapsUri = "",
                        placeType = PlaceType.CAFE,
                        userData = TravelPlace.UserData(estimatedDuration = 45.minutes),
                        startTime = 0.hours,
                        transportToNext = null,
                    ),
                ),
            ),
        )

        reduce {
            copy(
                contentInfo = ContentInfo(
                    travelId = "TRAVEL_001",
                    country = "태국",
                    city = "방콕",
                    budgetPerPerson = Budget(1200000),
                    nights = 1,
                    days = 2,
                    videoInfo = VideoInfo(
                        title = "방콕 풀코스, 동남아 안 가본 곽튜브와 함께 【방콕】",
                        creatorName = "빠니보틀",
                        profileImage = TEST_PROFILE_IMAGE_URL,
                        thumbnail = TEST_THUMBNAIL_URL,
                        link = "https://www.youtube.com/watch?v=F2utz6L76D0",
                        summary = "빠니보틀은 주말을 이용해 직장인들도 충분히 다녀올 수 있는 '금요일 퇴근 후 방콕 여행'의 가능성을 보여주며, 곽튜브와의 티격태격 케미를 통해 방콕의 매력을 소개합니다",
                    ),
                ),
                itineraries = loadedItineraries,
                tempItineraries = loadedItineraries,
            )
        }
    }

    override suspend fun handleIntent(intent: TravelDetailIntent) {
        when (intent) {
            is TravelDetailIntent.SelectDay -> selectDay(intent.day)
            is TravelDetailIntent.ClickStartTimeSetting -> clickStartTimeSetting()
            is TravelDetailIntent.ClickEditTravel -> clickEditTravel()
            is TravelDetailIntent.ClickAddScheduleButton -> clickAddScheduleButton()
            is TravelDetailIntent.CheckPlaceItem -> checkPlaceItem(intent.placeId)
            is TravelDetailIntent.CheckSelectAll -> checkSelectAll()
            is TravelDetailIntent.ClickDeleteSelectedPlaces -> clickDeleteSelectedPlaces()
            is TravelDetailIntent.ConfirmDeleteSelectedPlaces -> confirmDeleteSelectedPlaces()
            is TravelDetailIntent.DismissDeleteModal -> dismissDeleteModal()
            is TravelDetailIntent.ClickBack -> clickBack()
            is TravelDetailIntent.ConfirmCancelEditMode -> confirmCancelEditMode()
            is TravelDetailIntent.DismissCancelEditModal -> dismissCancelEditModal()
            is TravelDetailIntent.LongClickPlaceItem -> longClickPlaceItem()
            is TravelDetailIntent.DismissTimelineBottomSheet -> dismissTimelineBottomSheet()
            is TravelDetailIntent.ConfirmTimelineSetting -> confirmTimelineSetting(intent.startTime)
            is TravelDetailIntent.ReorderPlaces -> reorderPlaces(intent.dayIndex, intent.fromIndex, intent.toIndex)
            is TravelDetailIntent.ConfirmEditMode -> confirmEditMode()
            is TravelDetailIntent.ClickTransportSegment -> clickTransportSegment(intent.place)
            is TravelDetailIntent.DismissTransportBottomSheet -> dismissTransportBottomSheet()
            is TravelDetailIntent.ConfirmChangeTransportSegment -> confirmChangeTransportSegment(intent.segment)
            is TravelDetailIntent.ClickPlaceItem -> clickPlaceItem(intent.place)
            is TravelDetailIntent.DismissPlaceBottomSheet -> dismissPlaceBottomSheet()
            is TravelDetailIntent.NavigateToTravelPlaceDetail -> navigateToPlaceDetail(intent.placeId)
            is TravelDetailIntent.ClickAddTime -> clickAddTime()
            is TravelDetailIntent.ClickAddCost -> clickAddCost()
            is TravelDetailIntent.ClickAddMemo -> clickAddMemo()
            is TravelDetailIntent.ClickFindRoute -> clickFindRoute(intent.googleMapsUri)
            is TravelDetailIntent.DismissTimeBottomSheet -> dismissTimeBottomSheet()
            is TravelDetailIntent.ConfirmDuration -> confirmDuration(intent.duration)
            is TravelDetailIntent.DismissCostModal -> dismissCostModal()
            is TravelDetailIntent.ConfirmCost -> confirmCost(intent.cost)
            is TravelDetailIntent.DismissMemoModal -> dismissMemoModal()
            is TravelDetailIntent.ConfirmMemo -> confirmMemo(intent.memo)
        }
    }

    private fun selectDay(day: Int) {
        reduce { copy(selectedDay = day) }
    }

    private fun clickStartTimeSetting() {
        reduce { copy(showTimelineBottomSheet = true) }
    }

    private fun clickEditTravel() {
        reduce {
            copy(
                isEditMode = true,
                selectedPlaceIds = emptySet(),
                tempItineraries = itineraries,
            )
        }
    }

    private fun clickAddScheduleButton() {
        postSideEffect(
            TravelDetailSideEffect.NavigateToAddItinerary(
                travelId = travelId,
                day = state.value.selectedDay,
                country = state.value.country,
                representativeLatLng = state.value.representativeLatLng,
            ),
        )
    }

    private fun checkPlaceItem(placeId: Int) {
        reduce {
            copy(
                selectedPlaceIds = if (selectedPlaceIds.contains(placeId)) {
                    selectedPlaceIds - placeId
                } else {
                    selectedPlaceIds + placeId
                },
            )
        }
    }

    private fun checkSelectAll() {
        reduce {
            val currentDayPlaceIds = tempItineraries.getOrNull(selectedDay - 1)
                ?.places?.map { it.id }?.toSet() ?: emptySet()

            copy(
                selectedPlaceIds = if (selectedPlaceIds.size == currentDayPlaceIds.size) {
                    emptySet()
                } else {
                    currentDayPlaceIds
                },
            )
        }
    }

    private fun clickBack() {
        if (state.value.isEditMode) {
            reduce { copy(showCancelEditModal = true) }
        } else {
            postSideEffect(TravelDetailSideEffect.NavigateBack)
        }
    }

    private fun clickDeleteSelectedPlaces() {
        reduce { copy(showDeleteModal = true) }
    }

    private fun confirmDeleteSelectedPlaces() {
        reduce {
            val updatedItineraries = tempItineraries.map { itinerary ->
                val remainingPlaces = itinerary.places
                    .filter { it.id !in selectedPlaceIds }
                    .mapIndexed { newIndex, place -> place.copy(sequence = newIndex + 1) }
                itinerary.copy(places = recalculateTransportSegments(remainingPlaces))
            }

            copy(
                tempItineraries = updatedItineraries,
                selectedPlaceIds = emptySet(),
                showDeleteModal = false,
            )
        }
    }

    private fun dismissDeleteModal() {
        reduce { copy(showDeleteModal = false) }
    }

    private fun confirmCancelEditMode() {
        reduce {
            copy(
                isEditMode = false,
                selectedPlaceIds = emptySet(),
                showCancelEditModal = false,
                tempItineraries = itineraries,
            )
        }
    }

    private fun dismissCancelEditModal() {
        reduce { copy(showCancelEditModal = false) }
    }

    private fun longClickPlaceItem() {
        reduce {
            copy(
                isEditMode = true,
                selectedPlaceIds = emptySet(),
            )
        }
    }

    private fun dismissTimelineBottomSheet() {
        reduce { copy(showTimelineBottomSheet = false) }
    }

    private fun confirmTimelineSetting(startTime: Duration) {
        reduce {
            val dayIndex = selectedDay - 1
            val updatedItineraries = itineraries.mapIndexed { index, itinerary ->
                if (index == dayIndex) {
                    itinerary.copy(
                        places = calculatePlaceStartTimes(itinerary.places, startTime),
                        startTime = startTime,
                        endTime = startTime + itinerary.totalDuration,
                    )
                } else {
                    itinerary
                }
            }

            copy(
                itineraries = updatedItineraries,
                showTimelineBottomSheet = false,
            )
        }
    }

    private fun calculatePlaceStartTimes(
        places: List<TravelPlace>,
        startTime: Duration,
    ): List<TravelPlace> {
        if (places.isEmpty()) return places
        var currentTime = startTime
        return places.map { place ->
            val updatedPlace = place.copy(startTime = currentTime)
            currentTime += place.duration + (place.transportToNext?.duration ?: 0.hours)
            updatedPlace
        }
    }

    private fun reorderPlaces(dayIndex: Int, fromIndex: Int, toIndex: Int) {
        reduce {
            val updatedItineraries = tempItineraries.mapIndexed { index, itinerary ->
                if (index != dayIndex) return@mapIndexed itinerary
                val mutablePlaces = itinerary.places.toMutableList()
                if (fromIndex !in mutablePlaces.indices || toIndex !in mutablePlaces.indices) return@mapIndexed itinerary
                val movedItem = mutablePlaces.removeAt(fromIndex)
                mutablePlaces.add(toIndex, movedItem)
                val resequenced = mutablePlaces.mapIndexed { i, place -> place.copy(sequence = i + 1) }
                itinerary.copy(places = recalculateTransportSegments(resequenced))
            }
            copy(tempItineraries = updatedItineraries)
        }
    }

    private fun confirmEditMode() {
        reduce {
            copy(
                itineraries = tempItineraries,
                isEditMode = false,
                selectedPlaceIds = emptySet(),
            )
        }
    }

    private fun clickTransportSegment(place: TravelPlace) {
        reduce {
            copy(
                selectedPlace = place,
                showTransportBottomSheet = true,
            )
        }
    }

    private fun confirmChangeTransportSegment(newTransportSegment: TransportSegment) {
        reduce {
            val updatedItineraries = itineraries.mapIndexed { index, dayItinerary ->
                if (index == selectedDay - 1) {
                    val updatedPlaces = dayItinerary.places.map { place ->
                        if (place.id == selectedPlace?.id) {
                            place.copy(transportToNext = newTransportSegment)
                        } else {
                            place
                        }
                    }
                    val timedPlaces = calculatePlaceStartTimes(
                        places = updatedPlaces,
                        startTime = dayItinerary.startTime ?: DEFAULT_START_TIME.hours,
                    )

                    dayItinerary.copy(places = timedPlaces)
                } else {
                    dayItinerary
                }
            }
            copy(itineraries = updatedItineraries, selectedPlace = null, showTransportBottomSheet = false)
        }
    }

    private fun dismissTransportBottomSheet() {
        reduce { copy(selectedPlace = null, showTransportBottomSheet = false) }
    }

    private fun clickPlaceItem(place: TravelPlace) {
        reduce {
            copy(
                showPlaceBottomSheet = true,
                selectedPlace = place,
            )
        }
    }

    private fun dismissPlaceBottomSheet() {
        reduce {
            copy(
                showPlaceBottomSheet = false,
                selectedPlace = null,
            )
        }
    }

    private fun navigateToPlaceDetail(placeId: String) {
        val place = state.value.selectedPlace ?: return
        val tipContent = place.travelerTips.takeIf { it.isNotEmpty() }?.let {
            TipContent(creatorName = state.value.contentInfo.videoInfo.creatorName, tips = it)
        }
        val alternativePlaces = place.alternativePlaces.takeIf { it.isNotEmpty() }
        postSideEffect(
            TravelDetailSideEffect.NavigateToTravelPlaceDetail(
                placeId = placeId,
                tipContent = tipContent,
                alternativePlaces = alternativePlaces,
            ),
        )
        reduce {
            copy(
                showPlaceBottomSheet = false,
                selectedPlace = null,
            )
        }
    }

    private fun clickAddTime() {
        reduce {
            copy(showTimeBottomSheet = true)
        }
    }

    private fun dismissTimeBottomSheet() {
        reduce {
            copy(showTimeBottomSheet = false)
        }
    }

    private fun confirmDuration(duration: Duration) {
        reduce {
            val updatedItineraries = itineraries.map { itinerary ->
                if (itinerary.places.none { it.id == selectedPlace?.id }) return@map itinerary

                val durationUpdatedPlaces = itinerary.places.map { place ->
                    if (place.id == selectedPlace?.id) {
                        place.copy(userData = place.userData.copy(estimatedDuration = duration))
                    } else {
                        place
                    }
                }
                itinerary.copy(
                    places = calculatePlaceStartTimes(durationUpdatedPlaces, itinerary.startTime ?: DEFAULT_START_TIME.hours),
                    endTime = (itinerary.startTime ?: DEFAULT_START_TIME.hours) + itinerary.totalDuration,
                )
            }
            copy(
                itineraries = updatedItineraries,
                selectedPlace = null,
                showTimeBottomSheet = false,
            )
        }
    }

    private fun clickAddCost() {
        reduce {
            copy(showCostModal = true)
        }
    }

    private fun dismissCostModal() {
        reduce { copy(showCostModal = false) }
    }

    private fun confirmCost(cost: Int) {
        reduce {
            var updatedPlace: TravelPlace? = null
            val updatedItineraries = itineraries.map { itinerary ->
                itinerary.copy(
                    places = itinerary.places.map { place ->
                        if (place.id == selectedPlace?.id) {
                            val updated = place.copy(userData = place.userData.copy(cost = cost))
                            updatedPlace = updated
                            updated
                        } else {
                            place
                        }
                    },
                )
            }
            copy(
                itineraries = updatedItineraries,
                selectedPlace = updatedPlace,
                showCostModal = false,
            )
        }
    }

    private fun clickAddMemo() {
        reduce {
            copy(showMemoModal = true)
        }
    }

    private fun dismissMemoModal() {
        reduce { copy(showMemoModal = false) }
    }

    private fun confirmMemo(memo: String) {
        reduce {
            var updatedPlace: TravelPlace? = null
            val updatedItineraries = itineraries.map { itinerary ->
                itinerary.copy(
                    places = itinerary.places.map { place ->
                        if (place.id == selectedPlace?.id) {
                            val updated = place.copy(userData = place.userData.copy(memo = memo.trim()))
                            updatedPlace = updated
                            updated
                        } else {
                            place
                        }
                    },
                )
            }
            copy(
                itineraries = updatedItineraries,
                selectedPlace = updatedPlace,
                showMemoModal = false,
            )
        }
    }

    private fun clickFindRoute(url: String) {
        postSideEffect(TravelDetailSideEffect.NavigateToBrowser(url))
    }

    // TODO: 실제 라우팅 API 연동 시 교체
    private fun createTransportSegment(from: TravelPlace, to: TravelPlace): TransportSegment {
        return TransportSegment(type = TransportType.CAR, duration = 15.minutes, distance = 1000)
    }

    private fun recalculateTransportSegments(places: List<TravelPlace>): List<TravelPlace> {
        return places.mapIndexed { index, place ->
            val nextPlace = places.getOrNull(index + 1)
            place.copy(
                transportToNext = if (nextPlace != null) createTransportSegment(place, nextPlace) else null,
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(travelId: Long): TravelDetailViewModel
    }

    companion object {
        private const val DEFAULT_START_TIME = 8
    }
}
