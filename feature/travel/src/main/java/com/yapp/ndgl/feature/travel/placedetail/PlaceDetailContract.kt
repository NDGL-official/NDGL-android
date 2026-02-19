package com.yapp.ndgl.feature.travel.placedetail

import com.yapp.ndgl.core.base.UiIntent
import com.yapp.ndgl.core.base.UiSideEffect
import com.yapp.ndgl.core.base.UiState
import com.yapp.ndgl.feature.travel.model.AlternativePlace
import com.yapp.ndgl.feature.travel.model.PlaceDetailTab
import com.yapp.ndgl.feature.travel.model.PlaceInfo
import com.yapp.ndgl.feature.travel.model.PlacePhoto

data class PlaceDetailState(
    val placeInfo: PlaceInfo = PlaceInfo(),
    val selectedTab: PlaceDetailTab = PlaceDetailTab.INFO,
    val photos: List<PlacePhoto> = emptyList(),
    val selectedAlternativePlace: AlternativePlace? = null,
    val showChangeModal: Boolean = false,
) : UiState

sealed interface PlaceDetailIntent : UiIntent {
    data class SelectTab(val tab: PlaceDetailTab) : PlaceDetailIntent
    data class ClickChangePlace(val alternativePlace: AlternativePlace) : PlaceDetailIntent
    data object ConfirmChangePlace : PlaceDetailIntent
    data object DismissChangeModal : PlaceDetailIntent
    data object ClickAddress : PlaceDetailIntent
    data object ClickMenu : PlaceDetailIntent
    data class ClickAlternativePlace(val googlePlaceId: String) : PlaceDetailIntent
}

sealed interface PlaceDetailSideEffect : UiSideEffect {
    data class NavigateToBrowser(val url: String) : PlaceDetailSideEffect
    data class NavigateToAlternativePlaceDetail(val googlePlaceId: String) : PlaceDetailSideEffect
}
