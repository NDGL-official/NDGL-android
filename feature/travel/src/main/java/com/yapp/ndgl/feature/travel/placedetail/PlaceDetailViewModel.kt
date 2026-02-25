package com.yapp.ndgl.feature.travel.placedetail

import androidx.lifecycle.viewModelScope
import com.yapp.ndgl.core.base.BaseViewModel
import com.yapp.ndgl.core.util.suspendRunCatching
import com.yapp.ndgl.data.travel.repository.PlaceRepository
import com.yapp.ndgl.feature.travel.model.AlternativePlace
import com.yapp.ndgl.feature.travel.model.PlaceDetailTab
import com.yapp.ndgl.feature.travel.model.PlacePhoto
import com.yapp.ndgl.feature.travel.model.TipContent
import com.yapp.ndgl.feature.travel.model.toPlaceInfo
import com.yapp.ndgl.feature.travel.model.toPlaceType
import com.yapp.ndgl.navigation.model.RouteAlternativePlace
import com.yapp.ndgl.navigation.model.RouteTipContent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = PlaceDetailViewModel.Factory::class)
class PlaceDetailViewModel @AssistedInject constructor(
    @Assisted("googlePlaceId") private val googlePlaceId: String,
    @Assisted("tipContent") private val tipContent: RouteTipContent?,
    @Assisted("alternativePlaces") private val alternativePlaces: List<RouteAlternativePlace>?,
    @Assisted("travelId") private val travelId: Long?,
    @Assisted("day") private val day: Int?,
    @Assisted("itineraryId") private val itineraryId: Long?,
    private val placeRepository: PlaceRepository,
    private val userTravelRepository: com.yapp.ndgl.data.travel.repository.UserTravelRepository,
) : BaseViewModel<PlaceDetailState, PlaceDetailIntent, PlaceDetailSideEffect>(
    initialState = PlaceDetailState(),
) {
    init {
        loadPlaceDetail()
    }

    private fun loadPlaceDetail() = viewModelScope.launch {
        suspendRunCatching {
            placeRepository.getPlace(googlePlaceId)
        }.onSuccess { response ->
            loadPlacePhotos()
            reduce {
                copy(
                    placeInfo = response.toPlaceInfo().copy(
                        tipContent = tipContent?.let { TipContent(creatorName = it.creatorName, tips = it.tips) },
                        alternativePlaces = alternativePlaces?.map { routePlace ->
                            AlternativePlace(
                                id = routePlace.id,
                                name = routePlace.name,
                                thumbnail = routePlace.thumbnail,
                                placeType = routePlace.placeType.toPlaceType(),
                            )
                        },
                    ),
                )
            }
        }.onFailure {
            // TODO: 에러 처리
        }
    }

    private fun loadPlacePhotos() = viewModelScope.launch {
        repeat(3) {
            delay(1000)
            val result = suspendRunCatching { placeRepository.getPlacePhotos(googlePlaceId) }
            val photos = result.getOrNull()?.photos

            if (!photos.isNullOrEmpty()) {
                reduce {
                    copy(
                        photos = photos.map { PlacePhoto(url = it.photoUri, width = it.widthPx, height = it.heightPx) },
                    )
                }
                return@launch
            }

            result.onFailure {
                // FIXME: 에러 뷰
            }
        }
    }

    override suspend fun handleIntent(intent: PlaceDetailIntent) {
        when (intent) {
            is PlaceDetailIntent.SelectTab -> selectTab(intent.tab)
            is PlaceDetailIntent.ClickAddress -> clickAddress()
            is PlaceDetailIntent.ClickMenu -> clickMenu()
            is PlaceDetailIntent.ClickAlternativePlace -> clickAlternativePlace(intent.googlePlaceId)
            is PlaceDetailIntent.ClickChangePlace -> clickChangePlace(intent.alternativePlace)
            is PlaceDetailIntent.ConfirmChangePlace -> confirmChangePlace()
            is PlaceDetailIntent.DismissChangeModal -> dismissChangeModal()
        }
    }

    private fun selectTab(tab: PlaceDetailTab) {
        reduce { copy(selectedTab = tab) }
    }

    private fun clickAddress() {
        state.value.placeInfo.googleMapsUri?.let { postSideEffect(PlaceDetailSideEffect.NavigateToBrowser(it)) }
    }

    private fun clickMenu() {
        state.value.placeInfo.websiteUrl?.let { postSideEffect(PlaceDetailSideEffect.NavigateToBrowser(it)) }
    }

    private fun clickAlternativePlace(googlePlaceId: String) {
        postSideEffect(PlaceDetailSideEffect.NavigateToAlternativePlaceDetail(googlePlaceId))
    }

    private fun clickChangePlace(alternativePlace: AlternativePlace) {
        reduce { copy(selectedAlternativePlace = alternativePlace, showChangeModal = true) }
    }

    private fun confirmChangePlace() = viewModelScope.launch {
        val selectedPlace = state.value.selectedAlternativePlace ?: return@launch
        val currentTravelId = travelId ?: return@launch
        val currentDay = day ?: return@launch
        val currentItineraryId = itineraryId ?: return@launch

        reduce { copy(showChangeModal = false) }

        userTravelRepository.emitChangePlaceEvent(
            com.yapp.ndgl.data.travel.model.ChangePlaceEvent(
                travelId = currentTravelId,
                day = currentDay,
                itineraryId = currentItineraryId,
                oldGooglePlaceId = googlePlaceId,
                newGooglePlaceId = selectedPlace.id,
            ),
        )

        postSideEffect(PlaceDetailSideEffect.NavigateBack)
    }

    private fun dismissChangeModal() {
        reduce { copy(showChangeModal = false) }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("googlePlaceId") googlePlaceId: String,
            @Assisted("tipContent") tipContent: RouteTipContent?,
            @Assisted("alternativePlaces") alternativePlaces: List<RouteAlternativePlace>?,
            @Assisted("travelId") travelId: Long?,
            @Assisted("day") day: Int?,
            @Assisted("itineraryId") itineraryId: Long?,
        ): PlaceDetailViewModel
    }
}
