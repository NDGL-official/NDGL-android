package com.yapp.ndgl.feature.travel.followtravel.placedetail

import androidx.lifecycle.viewModelScope
import com.yapp.ndgl.core.base.BaseViewModel
import com.yapp.ndgl.core.util.suspendRunCatching
import com.yapp.ndgl.data.travel.repository.PlaceRepository
import com.yapp.ndgl.feature.travel.model.AlternativePlace
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

@HiltViewModel(assistedFactory = FollowPlaceDetailViewModel.Factory::class)
class FollowPlaceDetailViewModel @AssistedInject constructor(
    @Assisted private val googlePlaceId: String,
    @Assisted private val tipContent: RouteTipContent?,
    @Assisted private val alternativePlaces: List<RouteAlternativePlace>,
    private val placeRepository: PlaceRepository,
) : BaseViewModel<FollowPlaceDetailState, FollowPlaceDetailIntent, FollowPlaceDetailSideEffect>(
    initialState = FollowPlaceDetailState(),
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
                        alternativePlaces = alternativePlaces.map { routePlace ->
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

    override suspend fun handleIntent(intent: FollowPlaceDetailIntent) {
        when (intent) {
            is FollowPlaceDetailIntent.SelectTab -> reduce { copy(selectedTab = intent.tab) }
            is FollowPlaceDetailIntent.ClickAddress -> {
                state.value.placeInfo.googleMapsUri?.let {
                    postSideEffect(FollowPlaceDetailSideEffect.NavigateToBrowser(it))
                }
            }

            is FollowPlaceDetailIntent.ClickMenu -> {
                state.value.placeInfo.websiteUrl?.let {
                    postSideEffect(FollowPlaceDetailSideEffect.NavigateToBrowser(it))
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(googlePlaceId: String, tipContent: RouteTipContent?, alternativePlaces: List<RouteAlternativePlace>): FollowPlaceDetailViewModel
    }
}
