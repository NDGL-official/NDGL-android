package com.yapp.ndgl.feature.travel.traveldetail

import androidx.lifecycle.viewModelScope
import com.yapp.ndgl.core.base.BaseViewModel
import com.yapp.ndgl.core.util.parseDurationToTimeString
import com.yapp.ndgl.core.util.parseTimeStringToDuration
import com.yapp.ndgl.core.util.suspendRunCatching
import com.yapp.ndgl.data.travel.model.AddPlaceEvent
import com.yapp.ndgl.data.travel.model.ChangePlaceEvent
import com.yapp.ndgl.data.travel.model.ItineraryUpdateItem
import com.yapp.ndgl.data.travel.model.StartTimeUpdateItem
import com.yapp.ndgl.data.travel.model.TravelMode
import com.yapp.ndgl.data.travel.model.UserTravelTemplateContentInfo
import com.yapp.ndgl.data.travel.model.UserTravelTemplateItinerary
import com.yapp.ndgl.data.travel.repository.PlaceRepository
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
import com.yapp.ndgl.feature.travel.model.toOpeningHours
import com.yapp.ndgl.feature.travel.model.toPlaceInfo
import com.yapp.ndgl.feature.travel.model.toPlaceType
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
    private val placeRepository: PlaceRepository,
) : BaseViewModel<TravelDetailState, TravelDetailIntent, TravelDetailSideEffect>(
    initialState = TravelDetailState(days = days),
) {
    init {
        loadUserTravelTemplateItinerary()
        loadUserTravelTemplateContentInfo()
        subscribeToAddPlaceEvent()
        subscribeToChangePlaceEvent()
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
                    creatorName = info.program.creatorName,
                    startDate = info.startDate,
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

    private fun subscribeToChangePlaceEvent() = viewModelScope.launch {
        userTravelRepository.changePlaceEvent.collect { event ->
            if (event.travelId == travelId) {
                handleChangePlace(event)
            }
        }
    }

    private suspend fun handleAddPlace(event: AddPlaceEvent) {
        val dayIndex = event.day - 1
        val currentItinerary = state.value.itineraries.getOrNull(dayIndex) ?: return
        val newSequence = currentItinerary.places.size + 1
        val lastPlace = currentItinerary.places.lastOrNull()

        // 추가된 장소로 가는 교통수단 계산
        val newTransportSegment = if (lastPlace != null) {
            computeRoute(
                originLatitude = lastPlace.placeInfo.latitude,
                originLongitude = lastPlace.placeInfo.longitude,
                destinationLatitude = event.latitude,
                destinationLongitude = event.longitude,
                newGooglePlaceId = event.googlePlaceId,
                travelMode = TravelMode.TRANSIT,
            )
        } else {
            null
        }

        val (distanceKm, transportation) = if (currentItinerary.places.isNotEmpty()) {
            newTransportSegment?.distanceKm to listOfNotNull(newTransportSegment?.toTransportationItem())
        } else {
            null to null
        }

        suspendRunCatching {
            userTravelRepository.addItinerary(
                travelId = travelId,
                googlePlaceId = event.googlePlaceId,
                day = event.day,
                sequence = newSequence,
                startTime = if (lastPlace == null) {
                    null
                } else {
                    (
                        lastPlace.startTime + lastPlace.userData.estimatedDuration +
                            (newTransportSegment?.duration ?: 0.hours)
                        )
                        .parseDurationToTimeString()
                },
                estimatedDuration = 60,
                cost = null,
                memo = null,
                distanceKm = distanceKm,
                transportation = transportation,
            )
        }.onSuccess { response ->
            val newPlace = TravelPlace(
                id = response.id,
                placeInfo = PlaceInfo(
                    googlePlaceId = response.place.googlePlaceId,
                    name = response.place.name,
                    placeType = response.place.category.toPlaceType(),
                    day = response.day,
                    sequence = response.sequence,
                    thumbnail = response.place.thumbnail,
                    latitude = response.place.latitude,
                    longitude = response.place.longitude,
                    googleMapsUri = response.place.googleMapsUri,
                    estimatedDuration = response.estimatedDuration.minutes,
                ),
                regularOpeningHours = response.place.regularOpeningHours,
                userData = TravelPlace.UserData(
                    estimatedDuration = response.estimatedDuration.minutes,
                ),
                startTime = (lastPlace?.startTime ?: Itinerary.DEFAULT_START_TIME.hours) +
                    (lastPlace?.placeInfo?.estimatedDuration ?: 1.hours) + (newTransportSegment?.duration ?: 0.hours),
                transportToNext = null,
            )

            val updatedPlaces = if (currentItinerary.places.isNotEmpty()) {
                val lastPlace = currentItinerary.places.last()
                val transportSegment = if (distanceKm != null && transportation != null) {
                    TransportSegment(
                        googlePlaceId = event.googlePlaceId,
                        type = TransportType.TRANSIT,
                        duration = (transportation.first().timeMin * 60).seconds,
                        distance = (distanceKm * 1000).toInt(),
                    )
                } else {
                    null
                }
                val placesWithTransport = currentItinerary.places.dropLast(1) + lastPlace.copy(transportToNext = transportSegment)
                placesWithTransport + newPlace
            } else {
                listOf(newPlace)
            }

            reduce {
                val updatedItineraries = itineraries.mapIndexed { index, itinerary ->
                    if (index == dayIndex) {
                        itinerary.copy(places = updatedPlaces)
                    } else {
                        itinerary
                    }
                }
                copy(itineraries = updatedItineraries)
            }

            postSideEffect(TravelDetailSideEffect.ScrollToPlace(newPlace.id))
            postSideEffect(TravelDetailSideEffect.ShowSnackbar(TravelDetailSideEffect.SNACKBAR_ADDED_TO_MY_TRAVEL))
        }.onFailure {
            // TODO: Handle API failure
        }
    }

    private suspend fun handleChangePlace(event: ChangePlaceEvent) {
        val dayIndex = event.day - 1
        val currentItinerary = state.value.itineraries.getOrNull(dayIndex) ?: return
        val targetPlace = currentItinerary.places.find { it.id == event.itineraryId } ?: return
        val targetIndex = currentItinerary.places.indexOf(targetPlace)

        val newPlaceResponse = suspendRunCatching {
            placeRepository.getPlace(event.newGooglePlaceId)
        }.getOrNull() ?: return

        val newPlaceInfo = newPlaceResponse.toPlaceInfo().copy(
            day = targetPlace.placeInfo.day,
            sequence = targetPlace.placeInfo.sequence,
            tipContent = null, // 장소 변경 시 꿀팁 제거
            alternativePlaces = null, // 장소 변경 시 대체 장소(planB) 제거
        )
        val newRegularOpeningHours = newPlaceResponse.place.regularOpeningHours?.toOpeningHours(
            startDate = state.value.startDate,
            day = event.day,
        )
        val replacedPlace = targetPlace.copy(
            placeInfo = newPlaceInfo,
            regularOpeningHours = newRegularOpeningHours,
            transportToNext = null, // 장소 변경 시 교통수단 재계산 필요
        )
        val updatedPlaces = currentItinerary.places.mapIndexed { index, place ->
            if (index == targetIndex) {
                replacedPlace
            } else {
                place
            }
        }
        val recalculatedPlaces = recalculateTransportSegments(updatedPlaces)
        val firstPlaceStartTime = currentItinerary.places.firstOrNull()?.startTime ?: Itinerary.DEFAULT_START_TIME.hours
        val timedPlaces = calculatePlaceStartTimes(recalculatedPlaces, firstPlaceStartTime)
        val updatedItinerary = currentItinerary.copy(places = timedPlaces)
        val updatedItineraries = state.value.itineraries.mapIndexed { index, itinerary ->
            if (index == dayIndex) updatedItinerary else itinerary
        }

        updateItinerary(updatedItineraries).onSuccess {
            reduce {
                val updatedItineraries = itineraries.mapIndexed { index, itinerary ->
                    if (index == dayIndex) updatedItinerary else itinerary
                }
                copy(itineraries = updatedItineraries)
            }

            postSideEffect(TravelDetailSideEffect.AnimatePlaceChange(event.newGooglePlaceId))
            postSideEffect(TravelDetailSideEffect.ShowSnackbar(TravelDetailSideEffect.SNACKBAR_PLACE_CHANGED))
        }.onFailure {
            // TODO: 에러 처리
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

        postSideEffect(TravelDetailSideEffect.ShowSnackbar(TravelDetailSideEffect.SNACKBAR_PLACE_DELETED))
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

        updateItinerary(updatedItineraries).onSuccess {
            reduce {
                copy(
                    itineraries = updatedItineraries,
                    isEditMode = false,
                    selectedPlaceIds = emptySet(),
                )
            }
        }.onFailure {
            // TODO: Handle failure
        }
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

        updateItinerary(updatedItineraries).onSuccess {
            reduce {
                copy(
                    itineraries = updatedItineraries,
                    selectedPlace = null,
                    showTransportBottomSheet = false,
                    availableTransports = emptyList(),
                )
            }

            postSideEffect(TravelDetailSideEffect.ShowSnackbar(TravelDetailSideEffect.SNACKBAR_TRANSPORT_CHANGED))
        }.onFailure {
            // TODO: Handle failure
        }
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
                    TipContent(creatorName = state.value.creatorName, tips = it.tips)
                },
                alternativePlaces = place.placeInfo.alternativePlaces,
                day = place.placeInfo.day,
                itineraryId = place.id,
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
            itinerary.copy(places = calculatePlaceStartTimes(durationUpdatedPlaces, firstPlaceStartTime))
        }

        updateItinerary(updatedItineraries).onSuccess {
            reduce {
                copy(
                    itineraries = updatedItineraries,
                    selectedPlace = null,
                    showTimeBottomSheet = false,
                )
            }
        }.onFailure {
            // TODO: Handle failure
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

    private fun confirmCost(cost: Int) = viewModelScope.launch {
        val selectedPlace = state.value.selectedPlace ?: return@launch

        suspendRunCatching {
            userTravelRepository.updateTravelPlace(
                travelId = travelId,
                userTravelPlaceId = selectedPlace.id,
                cost = cost,
                memo = selectedPlace.userData.memo,
            )
        }.onSuccess {
            reduce {
                var updatedPlace: TravelPlace? = null
                val updatedItineraries = itineraries.map { itinerary ->
                    itinerary.copy(
                        places = itinerary.places.map { place ->
                            if (place.id == selectedPlace.id) {
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
        }.onFailure {
            // TODO: Handle failure
            reduce { copy(showCostModal = false) }
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

    private fun confirmMemo(memo: String) = viewModelScope.launch {
        val selectedPlace = state.value.selectedPlace ?: return@launch

        suspendRunCatching {
            userTravelRepository.updateTravelPlace(
                travelId = travelId,
                userTravelPlaceId = selectedPlace.id,
                cost = selectedPlace.userData.cost,
                memo = memo.trim(),
            )
        }.onSuccess {
            reduce {
                var updatedPlace: TravelPlace? = null
                val updatedItineraries = itineraries.map { itinerary ->
                    itinerary.copy(
                        places = itinerary.places.map { place ->
                            if (place.id == selectedPlace.id) {
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
        }.onFailure {
            // TODO: Handle failure
            reduce { copy(showMemoModal = false) }
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
                computeRoute(
                    originLatitude = from.placeInfo.latitude,
                    originLongitude = from.placeInfo.longitude,
                    destinationLatitude = to.placeInfo.latitude,
                    destinationLongitude = to.placeInfo.longitude,
                    newGooglePlaceId = to.placeInfo.googlePlaceId,
                    travelMode = mode,
                )

                suspendRunCatching {
                    routeRepository.computeRoute(
                        originLatitude = from.placeInfo.latitude,
                        originLongitude = from.placeInfo.longitude,
                        destinationLatitude = to.placeInfo.latitude,
                        destinationLongitude = to.placeInfo.longitude,
                        travelMode = mode,
                    )
                }.getOrNull()?.let { routeInfo ->
                    val durationSeconds = routeInfo.duration.removeSuffix("s").toLongOrNull()?.seconds
                    if (routeInfo.distanceMeters > 0 && durationSeconds != null) {
                        TransportSegment(
                            googlePlaceId = to.placeInfo.googlePlaceId,
                            type = mode.toTransportTypeFromMode(),
                            duration = durationSeconds,
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

    private suspend fun computeRoute(
        originLatitude: Double,
        originLongitude: Double,
        destinationLatitude: Double,
        destinationLongitude: Double,
        newGooglePlaceId: String,
        travelMode: TravelMode,
    ): TransportSegment? {
        return suspendRunCatching {
            routeRepository.computeRoute(
                originLatitude = originLatitude,
                originLongitude = originLongitude,
                destinationLatitude = destinationLatitude,
                destinationLongitude = destinationLongitude,
                travelMode = travelMode,
            )
        }.getOrNull()?.let { routeInfo ->
            val durationSeconds = routeInfo.duration.removeSuffix("s").toLongOrNull()?.seconds
            if (routeInfo.distanceMeters > 0 && durationSeconds != null) {
                TransportSegment(
                    googlePlaceId = newGooglePlaceId,
                    type = travelMode.toTransportType(),
                    duration = durationSeconds,
                    distance = routeInfo.distanceMeters,
                )
            } else {
                null
            }
        }
    }

    // FIXME: 기획상 변경될 수 있음, 현재는 대중교통, 도보, 자동차, 자전거, 오토바이 순
    private suspend fun recalculateTransportSegments(places: List<TravelPlace>): List<TravelPlace> = coroutineScope {
        val travelModes = listOf(
            TravelMode.TRANSIT,
            TravelMode.WALK,
            TravelMode.DRIVE,
            TravelMode.BICYCLE,
            TravelMode.TWO_WHEELER,
        )

        places.mapIndexed { index, place ->
            async {
                val nextPlace = places.getOrNull(index + 1) ?: return@async place.copy(transportToNext = null)

                // 기존 최적화: 다음 장소 ID가 변하지 않았다면 기존 데이터 유지
                if (place.transportToNext?.googlePlaceId == nextPlace.placeInfo.googlePlaceId) {
                    place
                } else {
                    var newSegment: TransportSegment? = null
                    for (mode in travelModes) {
                        val result = computeRoute(
                            originLatitude = place.placeInfo.latitude,
                            originLongitude = place.placeInfo.longitude,
                            destinationLatitude = nextPlace.placeInfo.latitude,
                            destinationLongitude = nextPlace.placeInfo.longitude,
                            newGooglePlaceId = nextPlace.placeInfo.googlePlaceId,
                            travelMode = mode,
                        )

                        if (result != null) {
                            newSegment = result
                            break
                        }
                    }

                    place.copy(transportToNext = newSegment)
                }
            }
        }.awaitAll()
    }

    private suspend fun updateItinerary(updatedItineraries: List<Itinerary>): Result<Unit> {
        return suspendRunCatching {
            userTravelRepository.updateItinerary(
                travelId = travelId,
                itineraries = updatedItineraries.toUpdateItems(),
            )
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
                    cost = item.cost,
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

    private fun List<Itinerary>.toUpdateItems(): List<ItineraryUpdateItem> {
        return this.flatMapIndexed { dayIndex, itinerary ->
            val day = dayIndex + 1
            itinerary.places.mapIndexed { index, place ->
                val prevPlace = itinerary.places.getOrNull(index - 1)

                ItineraryUpdateItem(
                    googlePlaceId = place.placeInfo.googlePlaceId,
                    day = day,
                    sequence = place.placeInfo.sequence,
                    startTime = if (itinerary.isStartTimeSet) place.startTime.parseDurationToTimeString() else null,
                    estimatedDuration = place.userData.estimatedDuration.inWholeMinutes.toInt(),
                    memo = place.userData.memo,
                    cost = place.userData.cost,
                    distanceKm = prevPlace?.transportToNext?.distanceKm,
                    transportation = prevPlace?.transportToNext?.let {
                        listOf(it.toTransportationItem())
                    },
                )
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
            is TravelDetailIntent.NavigateToTravelPlaceDetail -> navigateToPlaceDetail(intent.googlePlaceId)
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

    @AssistedFactory
    interface Factory {
        fun create(travelId: Long, days: Int): TravelDetailViewModel
    }
}
