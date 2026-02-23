package com.yapp.ndgl.feature.travel.traveldetail

import androidx.lifecycle.viewModelScope
import com.yapp.ndgl.core.base.BaseViewModel
import com.yapp.ndgl.core.util.parseDurationToTimeString
import com.yapp.ndgl.core.util.parseTimeStringToDuration
import com.yapp.ndgl.core.util.suspendRunCatching
import com.yapp.ndgl.data.travel.model.AddPlaceEvent
import com.yapp.ndgl.data.travel.model.ItineraryUpdateItem
import com.yapp.ndgl.data.travel.model.StartTimeUpdateItem
import com.yapp.ndgl.data.travel.model.TransportationItem
import com.yapp.ndgl.data.travel.model.TravelMode
import com.yapp.ndgl.data.travel.model.UserTravelTemplateContentInfo
import com.yapp.ndgl.data.travel.model.UserTravelTemplateItinerary
import com.yapp.ndgl.data.travel.repository.RouteRepository
import com.yapp.ndgl.data.travel.repository.UserTravelRepository
import com.yapp.ndgl.feature.travel.model.AlternativePlace
import com.yapp.ndgl.feature.travel.model.Budget
import com.yapp.ndgl.feature.travel.model.ContentInfo
import com.yapp.ndgl.feature.travel.model.PlaceInfo
import com.yapp.ndgl.feature.travel.model.TipContent
import com.yapp.ndgl.feature.travel.model.TransportSegment
import com.yapp.ndgl.feature.travel.model.TransportType
import com.yapp.ndgl.feature.travel.model.VideoInfo
import com.yapp.ndgl.feature.travel.model.toPlaceType
import com.yapp.ndgl.feature.travel.model.toTransportCategory
import com.yapp.ndgl.feature.travel.model.toTransportType
import com.yapp.ndgl.feature.travel.model.toTravelMode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import com.yapp.ndgl.feature.travel.model.toTransportType as toTransportTypeFromMode

@Suppress("LargeClass")
@HiltViewModel(assistedFactory = TravelDetailViewModel.Factory::class)
class TravelDetailViewModel @AssistedInject constructor(
    @Assisted private val travelId: Long,
    @Assisted private val days: Int,
    private val userTravelRepository: UserTravelRepository,
    private val routeRepository: RouteRepository,
) : BaseViewModel<TravelDetailState, TravelDetailIntent, TravelDetailSideEffect>(
    initialState = TravelDetailState(days = days),
) {
    init {
        loadUserTravelTemplateItinerary()
        loadUserTravelTemplateContentInfo()
        subscribeToAddPlaceEvent()
    }

    private fun loadUserTravelTemplateItinerary() = viewModelScope.launch {
        reduce { copy(itineraries = List(days) { Itinerary() }) }
        for (day in 1..days) {
            launch {
                suspendRunCatching {
                    userTravelRepository.getUserTravelTemplateItinerary(travelId = travelId, day = day)
                }.onSuccess { itinerary ->
                    reduce {
                        val updated = itineraries.toMutableList()
                        val convertedItinerary = itinerary.toItinerary()
                        updated[day - 1] = convertedItinerary
                        copy(itineraries = updated)
                    }
                }.onFailure {
                    // FIXME: 일차 화면 별 에러 뷰
                }
            }
        }
    }

    private fun loadUserTravelTemplateContentInfo() = viewModelScope.launch {
        suspendRunCatching {
            userTravelRepository.getUserTravelTemplateContentInfo(travelId = travelId)
        }.onSuccess { info ->
            reduce {
                copy(
                    contentInfo = info.toContentInfo(),
                    countryCode = info.countryCode,
                )
            }
        }.onFailure {
            // TODO: 에러 뷰
        }
    }

    private fun subscribeToAddPlaceEvent() = viewModelScope.launch {
        userTravelRepository.addPlaceEvent.collect { event ->
            if (event.travelId == travelId) {
                handleAddPlace(event)
            }
        }
    }

    private suspend fun handleAddPlace(event: AddPlaceEvent) {
        val dayIndex = event.day - 1
        val currentItinerary = state.value.itineraries.getOrNull(dayIndex) ?: return

        val newSequence = currentItinerary.places.size + 1

        val newPlaceInfo = PlaceInfo(
            googlePlaceId = event.googlePlaceId,
            name = event.name,
            placeType = event.placeType.toPlaceType(),
            day = event.day,
            sequence = newSequence,
            thumbnail = event.thumbnail,
            latitude = event.latitude,
            longitude = event.longitude,
            address = event.address,
            phoneNumber = event.phoneNumber,
            googleMapsUri = event.googleMapsUri,
            websiteUrl = event.websiteUrl,
            rating = event.rating,
            userRatingCount = event.userRatingCount,
            estimatedDuration = event.estimatedDuration.minutes,
        )

        val newPlace = TravelPlace(
            id = event.googlePlaceId.hashCode().toLong(), // FIXME: 임시 ID (googlePlaceId 해시), API 응답에서 실제 itinerary item ID 필요
            placeInfo = newPlaceInfo,
            regularOpeningHours = null,
            userData = TravelPlace.UserData(
                estimatedDuration = event.estimatedDuration.minutes,
            ),
            startTime = 0.hours,
            transportToNext = null,
        )

        val updatedPlaces = if (currentItinerary.places.isNotEmpty()) {
            val lastPlace = currentItinerary.places.last()
            val transportSegment = calculateTransport(lastPlace, newPlace)

            val placesWithTransport = currentItinerary.places.dropLast(1) +
                lastPlace.copy(transportToNext = transportSegment)
            placesWithTransport + newPlace
        } else {
            listOf(newPlace)
        }

        val firstPlaceStartTime = currentItinerary.places.firstOrNull()?.startTime
            ?: Itinerary.DEFAULT_START_TIME.hours
        val timedPlaces = calculatePlaceStartTimes(updatedPlaces, firstPlaceStartTime)

        reduce {
            val updatedItineraries = itineraries.mapIndexed { index, itinerary ->
                if (index == dayIndex) {
                    itinerary.copy(places = timedPlaces)
                } else {
                    itinerary
                }
            }
            copy(itineraries = updatedItineraries)
        }

        postSideEffect(TravelDetailSideEffect.ScrollToPlace(newPlace.id))
        // FIXME: 일정 추가 API 연동
    }

    private suspend fun calculateTransport(
        from: TravelPlace,
        to: TravelPlace,
    ): TransportSegment? {
        // FIXME: 기획상 변경될 수 있음, 현재는 대중교통 고정
        return suspendRunCatching {
            routeRepository.computeRoute(
                originLatitude = from.placeInfo.latitude,
                originLongitude = from.placeInfo.longitude,
                destinationLatitude = to.placeInfo.latitude,
                destinationLongitude = to.placeInfo.longitude,
                travelMode = TravelMode.TRANSIT,
            )
        }.getOrNull()?.let { routeInfo ->
            if (routeInfo.distanceMeters > 0) {
                TransportSegment(
                    googlePlaceId = to.placeInfo.googlePlaceId,
                    type = TransportType.TRANSIT,
                    duration = routeInfo.duration.removeSuffix("s").toInt().seconds,
                    distance = routeInfo.distanceMeters,
                )
            } else {
                null
            }
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
            is TravelDetailIntent.DismissStartTimeSettingBottomSheet -> dismissStartTimeSettingBottomSheet()
            is TravelDetailIntent.ConfirmStartTimeSetting -> confirmStartTimeSetting(intent.startTime)
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
        reduce { copy(showStartTimeSettingBottomSheet = true) }
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
                countryCode = state.value.countryCode,
                representativeLatLng = state.value.representativeLatLng,
            ),
        )
    }

    private fun checkPlaceItem(placeId: Long) {
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
            val currentDayPlaceIds = tempItineraries.getOrNull(selectedDay - 1)?.places?.map { it.id }?.toSet() ?: emptySet()

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
                val remainingPlaces = itinerary.places.filter { it.id !in selectedPlaceIds }
                    .mapIndexed { newIndex, place -> place.copy(placeInfo = place.placeInfo.copy(sequence = newIndex + 1)) }
                itinerary.copy(places = remainingPlaces)
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

    private fun dismissStartTimeSettingBottomSheet() {
        reduce { copy(showStartTimeSettingBottomSheet = false) }
    }

    private fun confirmStartTimeSetting(startTime: Duration) = viewModelScope.launch {
        val firstPlace = state.value.currentPlaces.firstOrNull()

        if (firstPlace != null) {
            val timedPlaces = calculatePlaceStartTimes(state.value.currentPlaces, startTime)
            val updates = timedPlaces.map { place ->
                StartTimeUpdateItem(
                    id = place.id,
                    startTime = place.startTime.parseDurationToTimeString(),
                )
            }

            suspendRunCatching {
                userTravelRepository.bulkUpdateStartTime(
                    travelId = travelId,
                    updates = updates,
                )
            }.onSuccess {
                applyStartTimeUpdate(timedPlaces)
            }.onFailure {
                // TODO: Handle failure
            }
        }
    }

    private fun applyStartTimeUpdate(timedPlaces: List<TravelPlace>) {
        reduce {
            val dayIndex = selectedDay - 1
            val updatedItineraries = itineraries.mapIndexed { index, itinerary ->
                if (index == dayIndex) {
                    itinerary.copy(
                        places = timedPlaces,
                        isStartTimeSet = true,
                    )
                } else {
                    itinerary
                }
            }

            copy(
                itineraries = updatedItineraries,
                showStartTimeSettingBottomSheet = false,
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
                val resequenced = mutablePlaces.mapIndexed { i, place -> place.copy(placeInfo = place.placeInfo.copy(sequence = i + 1)) }
                itinerary.copy(places = resequenced)
            }

            copy(tempItineraries = updatedItineraries)
        }
    }

    private fun confirmEditMode() = viewModelScope.launch {
        // 일차별로 변경 사항 감지, 교통수단 재계산 및 시간 재계산
        val updatedItineraries = state.value.tempItineraries.mapIndexed { dayIndex, tempItinerary ->
            val originalItinerary = state.value.itineraries.getOrNull(dayIndex)
            val hasChanges = originalItinerary == null || tempItinerary.places.map { it.id } != originalItinerary.places.map { it.id }

            if (hasChanges && tempItinerary.places.isNotEmpty()) {
                val recalculatedPlaces = recalculateTransportSegments(tempItinerary.places)
                val firstPlaceStartTime = originalItinerary?.places?.firstOrNull()?.startTime ?: Itinerary.DEFAULT_START_TIME.hours
                val timedPlaces = calculatePlaceStartTimes(recalculatedPlaces, firstPlaceStartTime)

                tempItinerary.copy(places = timedPlaces)
            } else {
                tempItinerary
            }
        }

        // 낙관적 업데이트: UI 먼저 업데이트
        reduce {
            copy(
                itineraries = updatedItineraries,
                isEditMode = false,
                selectedPlaceIds = emptySet(),
            )
        }

        // FIXME: API 실패 시 롤백 로직 필요
        updateItinerary()
    }

    private fun clickTransportSegment(place: TravelPlace) = viewModelScope.launch {
        val currentTransport = place.transportToNext
        reduce {
            copy(
                selectedPlace = place,
                showTransportBottomSheet = true,
                isLoadingTransports = true,
            )
        }

        val currentItinerary = state.value.currentItinerary
        val placeIndex = currentItinerary?.places?.indexOfFirst { it.id == place.id } ?: -1
        val nextPlace = currentItinerary?.places?.getOrNull(placeIndex + 1)

        if (nextPlace != null && currentTransport != null) {
            val transportOptions = getTransportOptions(
                from = place,
                to = nextPlace,
                excludeTransportType = currentTransport.type,
            )

            reduce {
                copy(
                    availableTransports = listOf(currentTransport) + transportOptions,
                    isLoadingTransports = false,
                )
            }
        } else {
            reduce {
                copy(
                    availableTransports = emptyList(),
                    isLoadingTransports = false,
                )
            }
        }
    }

    private fun confirmChangeTransportSegment(newTransportSegment: TransportSegment) = viewModelScope.launch {
        val updatedItineraries = state.value.itineraries.mapIndexed { index, dayItinerary ->
            if (index == state.value.selectedDay - 1) {
                val updatedPlaces = dayItinerary.places.map { place ->
                    if (place.id == state.value.selectedPlace?.id) {
                        place.copy(transportToNext = newTransportSegment)
                    } else {
                        place
                    }
                }
                val firstPlaceStartTime = dayItinerary.places.firstOrNull()?.startTime ?: Itinerary.DEFAULT_START_TIME.hours
                val timedPlaces = calculatePlaceStartTimes(
                    places = updatedPlaces,
                    startTime = firstPlaceStartTime,
                )

                dayItinerary.copy(places = timedPlaces)
            } else {
                dayItinerary
            }
        }

        // 낙관적 업데이트: UI 먼저 업데이트
        reduce {
            copy(
                itineraries = updatedItineraries,
                selectedPlace = null,
                showTransportBottomSheet = false,
                availableTransports = emptyList(),
            )
        }

        // FIXME: API 실패 시 롤백 로직 필요
        updateItinerary()
    }

    private fun dismissTransportBottomSheet() {
        reduce {
            copy(
                selectedPlace = null,
                showTransportBottomSheet = false,
                availableTransports = emptyList(),
            )
        }
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

    private fun navigateToPlaceDetail(googlePlaceId: String) {
        val place = state.value.selectedPlace ?: return
        postSideEffect(
            TravelDetailSideEffect.NavigateToTravelPlaceDetail(
                googlePlaceId = googlePlaceId,
                tipContent = place.placeInfo.tipContent?.let {
                    TipContent(creatorName = it.creatorName, tips = it.tips)
                },
                alternativePlaces = place.placeInfo.alternativePlaces,
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

    private fun confirmDuration(duration: Duration) = viewModelScope.launch {
        val updatedItineraries = state.value.itineraries.map { itinerary ->
            if (itinerary.places.none { it.id == state.value.selectedPlace?.id }) return@map itinerary

            val durationUpdatedPlaces = itinerary.places.map { place ->
                if (place.id == state.value.selectedPlace?.id) {
                    place.copy(userData = place.userData.copy(estimatedDuration = duration))
                } else {
                    place
                }
            }
            val firstPlaceStartTime = itinerary.places.firstOrNull()?.startTime ?: Itinerary.DEFAULT_START_TIME.hours
            itinerary.copy(
                places = calculatePlaceStartTimes(durationUpdatedPlaces, firstPlaceStartTime),
            )
        }

        // 낙관적 업데이트: UI 먼저 업데이트
        reduce {
            copy(
                itineraries = updatedItineraries,
                selectedPlace = null,
                showTimeBottomSheet = false,
            )
        }

        // FIXME: API 실패 시 롤백 로직 필요
        updateItinerary()
    }

    private fun clickAddCost() {
        reduce {
            copy(showCostModal = true)
        }
    }

    private fun dismissCostModal() {
        reduce { copy(showCostModal = false) }
    }

    // FIXME : 비용 추가 관련 API 미제작
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

    // FIXME: 메모 추가 관련 API 미제작
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

    suspend fun getTransportOptions(
        from: TravelPlace,
        to: TravelPlace,
        excludeTransportType: TransportType? = null,
    ): List<TransportSegment> = coroutineScope {
        val allTravelModes = listOf(
            TravelMode.DRIVE,
            TravelMode.TRANSIT,
            TravelMode.WALK,
            TravelMode.BICYCLE,
            TravelMode.TWO_WHEELER,
        )

        val excludeTravelMode = excludeTransportType?.toTravelMode()
        val travelModes = allTravelModes.filter { it != excludeTravelMode }

        val transportOptions = travelModes.map { mode ->
            async {
                suspendRunCatching {
                    routeRepository.computeRoute(
                        originLatitude = from.placeInfo.latitude,
                        originLongitude = from.placeInfo.longitude,
                        destinationLatitude = to.placeInfo.latitude,
                        destinationLongitude = to.placeInfo.longitude,
                        travelMode = mode,
                    )
                }.getOrNull()?.let { routeInfo ->
                    if (routeInfo.distanceMeters > 0) {
                        TransportSegment(
                            googlePlaceId = to.placeInfo.googlePlaceId,
                            type = mode.toTransportTypeFromMode(),
                            duration = routeInfo.duration.removeSuffix("s").toInt().seconds,
                            distance = routeInfo.distanceMeters,
                        )
                    } else {
                        null
                    }
                }
            }
        }

        transportOptions.awaitAll().filterNotNull()
    }

    // FIXME: 기획상 변경될 수 있음, 현재는 대중교통 고정
    private suspend fun recalculateTransportSegments(places: List<TravelPlace>): List<TravelPlace> {
        return places.mapIndexed { index, place ->
            val nextPlace = places.getOrNull(index + 1)
            val transportSegment = if (nextPlace != null) {
                // googlePlaceId가 일치하면 기존 transportToNext 재사용
                if (place.transportToNext?.googlePlaceId == nextPlace.placeInfo.googlePlaceId) {
                    place.transportToNext
                } else {
                    // googlePlaceId가 다르면 새로 계산
                    suspendRunCatching {
                        routeRepository.computeRoute(
                            originLatitude = place.placeInfo.latitude,
                            originLongitude = place.placeInfo.longitude,
                            destinationLatitude = nextPlace.placeInfo.latitude,
                            destinationLongitude = nextPlace.placeInfo.longitude,
                            travelMode = TravelMode.TRANSIT,
                        )
                    }.getOrNull()?.let { routeInfo ->
                        if (routeInfo.distanceMeters > 0) {
                            TransportSegment(
                                googlePlaceId = nextPlace.placeInfo.googlePlaceId,
                                type = TransportType.TRANSIT,
                                duration = routeInfo.duration.removeSuffix("s").toInt().seconds,
                                distance = routeInfo.distanceMeters,
                            )
                        } else {
                            null
                        }
                    }
                }
            } else {
                null
            }

            place.copy(transportToNext = transportSegment)
        }
    }

    private fun updateItinerary() = viewModelScope.launch {
        val allItineraryItems = state.value.itineraries.flatMapIndexed { dayIndex, itinerary ->
            val day = dayIndex + 1
            itinerary.places.map { place ->
                ItineraryUpdateItem(
                    placeId = place.id,
                    day = day,
                    sequence = place.placeInfo.sequence,
                    startTime = place.startTime.parseDurationToTimeString(),
                    estimatedDuration = place.userData.estimatedDuration.inWholeMinutes.toInt(),
                    memo = place.userData.memo,
                    distanceKm = place.transportToNext?.let { it.distance / 1000.0 },
                    transportation = place.transportToNext?.let {
                        listOf(
                            TransportationItem(
                                mode = it.type.toTransportCategory(),
                                timeMin = it.duration.inWholeMinutes.toInt(),
                            ),
                        )
                    },
                )
            }
        }

        suspendRunCatching {
            userTravelRepository.updateItinerary(
                travelId = travelId,
                itineraries = allItineraryItems,
            )
        }.onSuccess {
            // FIXME: 성공 처리
        }.onFailure {
            // FIXME: 에러 처리
        }
    }

    private fun UserTravelTemplateItinerary.toItinerary(): Itinerary {
        val firstItemStartTime = parseTimeStringToDuration(itineraries.firstOrNull()?.startTime)
        val isStartTimeSet = firstItemStartTime != null

        val places = itineraries.mapIndexed { index, item ->
            val nextItem = itineraries.getOrNull(index + 1)

            TravelPlace(
                id = item.id,
                placeInfo = PlaceInfo(
                    googlePlaceId = item.place.googlePlaceId,
                    name = item.place.name,
                    day = item.day,
                    sequence = item.sequence,
                    thumbnail = item.place.thumbnail,
                    latitude = item.place.latitude,
                    longitude = item.place.longitude,
                    googleMapsUri = item.place.googleMapsUri,
                    placeType = item.place.category.toPlaceType(),
                    tipContent = if (item.travelerTips.isNullOrEmpty()) {
                        null
                    } else {
                        TipContent(
                            tips = item.travelerTips,
                        )
                    },
                    alternativePlaces = item.planB.orEmpty().map { planB ->
                        AlternativePlace(
                            id = planB.googlePlaceId,
                            name = planB.name,
                            thumbnail = planB.thumbnail,
                            placeType = planB.category.toPlaceType(),
                        )
                    },
                ),
                regularOpeningHours = item.place.regularOpeningHours,
                userData = TravelPlace.UserData(
                    estimatedDuration = item.estimatedDuration.minutes,
                    memo = item.memo,
                ),
                startTime = parseTimeStringToDuration(item.startTime) ?: 0.hours,
                transportToNext = nextItem?.transportation?.firstOrNull()?.let { transport ->
                    TransportSegment(
                        googlePlaceId = nextItem.place.googlePlaceId,
                        type = transport.mode.toTransportType(),
                        duration = transport.timeMin.minutes,
                        distance = ((nextItem.distanceKm ?: 0.0) * 1000).toInt(),
                    )
                },
            )
        }

        // isStartTimeSet이 false면 클라이언트에서 시간 계산
        val finalPlaces = if (!isStartTimeSet && places.isNotEmpty()) {
            calculatePlaceStartTimes(places, Itinerary.DEFAULT_START_TIME.hours)
        } else {
            places
        }

        return Itinerary(
            isStartTimeSet = isStartTimeSet,
            places = finalPlaces,
        )
    }

    private fun UserTravelTemplateContentInfo.toContentInfo(): ContentInfo = ContentInfo(
        travelId = userTravelId,
        countryCode = countryCode,
        country = countryName,
        city = city,
        budgetPerPerson = Budget(budgetPerPerson ?: 0),
        nights = nights,
        days = days,
        videoInfo = VideoInfo(
            title = program.title,
            creatorName = program.creatorName,
            profileImage = program.profileImage,
            thumbnail = program.thumbnail,
            link = program.link,
            summary = program.summary,
        ),
    )

    @AssistedFactory
    interface Factory {
        fun create(travelId: Long, days: Int): TravelDetailViewModel
    }
}
