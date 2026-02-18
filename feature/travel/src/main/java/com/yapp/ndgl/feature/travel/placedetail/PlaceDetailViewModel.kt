package com.yapp.ndgl.feature.travel.placedetail

import androidx.lifecycle.viewModelScope
import com.yapp.ndgl.core.base.BaseViewModel
import com.yapp.ndgl.core.util.suspendRunCatching
import com.yapp.ndgl.data.travel.repository.PlaceRepository
import com.yapp.ndgl.feature.travel.model.AlternativePlace
import com.yapp.ndgl.feature.travel.model.PlaceDetailTab
import com.yapp.ndgl.feature.travel.model.PlacePhoto
import com.yapp.ndgl.feature.travel.model.Price
import com.yapp.ndgl.feature.travel.model.PriceRange
import com.yapp.ndgl.feature.travel.model.TipContent
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
    @Assisted private val placeId: String,
    @Assisted private val tipContent: RouteTipContent?,
    @Assisted private val alternativePlaces: List<RouteAlternativePlace>,
    private val placeRepository: PlaceRepository,
) : BaseViewModel<PlaceDetailState, PlaceDetailIntent, PlaceDetailSideEffect>(
    initialState = PlaceDetailState(),
) {
    init {
        loadPlaceDetail()
    }

    private fun loadPlaceDetail() = viewModelScope.launch {
        suspendRunCatching {
            placeRepository.getPlace(placeId)
        }.onSuccess { response ->
            loadPlacePhotos()

            val place = response.place
            reduce {
                copy(
                    placeInfo = PlaceInfo(
                        id = place.id,
                        name = place.name,
                        placeType = place.category.toPlaceType(),
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
                        openingHours = place.regularOpeningHours?.joinToString("\n"),
                        googleMapsUri = place.googleMapsUri,
                        websiteUrl = place.websiteUri,
                        thumbnail = place.thumbnail.orEmpty(),
                        tipContent = tipContent?.let { TipContent(creatorName = it.creatorName, tips = it.tips) },
                        alternativePlaces = alternativePlaces.map { routePlace ->
                            AlternativePlace(
                                id = routePlace.id,
                                name = routePlace.name,
                                thumbnail = routePlace.thumbnail,
                                placeType = routePlace.placeType.toPlaceType(),
                            )
                        },
                        latitude = place.location.latitude,
                        longitude = place.location.longitude,
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
            val result = suspendRunCatching { placeRepository.getPlacePhotos(placeId) }
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
            is PlaceDetailIntent.ClickChangePlace -> clickChangePlace(intent.alternativePlace)
            is PlaceDetailIntent.ConfirmChangePlace -> confirmChangePlace()
            is PlaceDetailIntent.DismissChangeModal -> dismissChangeModal()
            is PlaceDetailIntent.ClickAddress -> clickAddress()
            is PlaceDetailIntent.ClickMenu -> clickMenu()
        }
    }

    private fun selectTab(tab: PlaceDetailTab) {
        reduce { copy(selectedTab = tab) }
    }

    private fun clickChangePlace(alternativePlace: AlternativePlace) {
        reduce { copy(selectedAlternativePlace = alternativePlace, showChangeModal = true) }
    }

    // TODO("Plan B 장소 변경 로직")
    private fun confirmChangePlace() {
        reduce {
            copy(
                showChangeModal = false,
            )
        }
    }

    private fun dismissChangeModal() {
        reduce { copy(showChangeModal = false) }
    }

    private fun clickAddress() {
        state.value.placeInfo.googleMapsUri?.let { postSideEffect(PlaceDetailSideEffect.NavigateToBrowser(it)) }
    }

    private fun clickMenu() {
        state.value.placeInfo.websiteUrl?.let { postSideEffect(PlaceDetailSideEffect.NavigateToBrowser(it)) }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            placeId: String,
            tipContent: RouteTipContent?,
            alternativePlaces: List<RouteAlternativePlace>?,
        ): PlaceDetailViewModel
    }
}
