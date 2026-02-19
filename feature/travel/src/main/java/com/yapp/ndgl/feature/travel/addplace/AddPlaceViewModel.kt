package com.yapp.ndgl.feature.travel.addplace

import androidx.lifecycle.viewModelScope
import com.yapp.ndgl.core.base.BaseViewModel
import com.yapp.ndgl.core.util.suspendRunCatching
import com.yapp.ndgl.data.travel.repository.PlaceRepository
import com.yapp.ndgl.feature.travel.model.PlaceDetailTab
import com.yapp.ndgl.feature.travel.model.PlacePhoto
import com.yapp.ndgl.feature.travel.model.toPlaceInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = AddPlaceViewModel.Factory::class)
class AddPlaceViewModel @AssistedInject constructor(
    @Assisted private val placeId: String,
    private val placeRepository: PlaceRepository,
) : BaseViewModel<AddPlaceState, AddPlaceIntent, AddPlaceSideEffect>(
    initialState = AddPlaceState(),
) {
    init {
        loadPlaceDetail()
    }

    private fun loadPlaceDetail() = viewModelScope.launch {
        suspendRunCatching {
            placeRepository.getPlace(placeId)
        }.onSuccess { response ->
            loadPlacePhotos()
            reduce {
                copy(placeInfo = response.toPlaceInfo())
            }
        }.onFailure {
            // TODO: 에러 뷰
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

    override suspend fun handleIntent(intent: AddPlaceIntent) {
        when (intent) {
            is AddPlaceIntent.SelectTab -> selectTab(intent.tab)
            is AddPlaceIntent.ClickAddress -> clickAddress()
            is AddPlaceIntent.ClickMenu -> clickMenu()
            is AddPlaceIntent.ClickBack -> clickBack()
            is AddPlaceIntent.ClickAddItinerary -> clickAddItinerary()
        }
    }

    private fun selectTab(tab: PlaceDetailTab) {
        reduce { copy(selectedTab = tab) }
    }

    private fun clickAddress() {
        state.value.placeInfo.googleMapsUri?.let { postSideEffect(AddPlaceSideEffect.NavigateToBrowser(it)) }
    }

    private fun clickMenu() {
        state.value.placeInfo.websiteUrl?.let { postSideEffect(AddPlaceSideEffect.NavigateToBrowser(it)) }
    }

    private fun clickBack() {
        postSideEffect(AddPlaceSideEffect.NavigateBack)
    }

    private fun clickAddItinerary() {
        // TODO: 선택된 장소를 일정에 추가 API 연동
    }

    @AssistedFactory
    interface Factory {
        fun create(placeId: String): AddPlaceViewModel
    }
}
