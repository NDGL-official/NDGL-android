package com.yapp.ndgl.feature.travel.addplace

import com.yapp.ndgl.core.base.UiIntent
import com.yapp.ndgl.core.base.UiSideEffect
import com.yapp.ndgl.core.base.UiState
import com.yapp.ndgl.feature.travel.model.PlaceDetailTab
import com.yapp.ndgl.feature.travel.model.PlaceInfo
import com.yapp.ndgl.feature.travel.model.PlacePhoto

data class AddPlaceState(
    val placeInfo: PlaceInfo = PlaceInfo(),
    val selectedTab: PlaceDetailTab = PlaceDetailTab.INFO,
    val photos: List<PlacePhoto> = emptyList(),
) : UiState

sealed interface AddPlaceIntent : UiIntent {
    data class SelectTab(val tab: PlaceDetailTab) : AddPlaceIntent
    data object ClickAddress : AddPlaceIntent
    data object ClickMenu : AddPlaceIntent
    data object ClickBack : AddPlaceIntent
    data object ClickAddItinerary : AddPlaceIntent
}

sealed interface AddPlaceSideEffect : UiSideEffect {
    data object NavigateBack : AddPlaceSideEffect
    data class NavigateToBrowser(val url: String) : AddPlaceSideEffect
}
