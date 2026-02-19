package com.yapp.ndgl.feature.travel.followtravel.placedetail

import com.yapp.ndgl.core.base.UiIntent
import com.yapp.ndgl.core.base.UiSideEffect
import com.yapp.ndgl.core.base.UiState
import com.yapp.ndgl.feature.travel.model.PlaceDetailTab
import com.yapp.ndgl.feature.travel.model.PlaceInfo
import com.yapp.ndgl.feature.travel.model.PlacePhoto

data class FollowPlaceDetailState(
    val placeInfo: PlaceInfo = PlaceInfo(),
    val selectedTab: PlaceDetailTab = PlaceDetailTab.INFO,
    val photos: List<PlacePhoto> = emptyList(),
) : UiState

sealed interface FollowPlaceDetailIntent : UiIntent {
    data class SelectTab(val tab: PlaceDetailTab) : FollowPlaceDetailIntent
    data object ClickAddress : FollowPlaceDetailIntent
    data object ClickMenu : FollowPlaceDetailIntent
    data class ClickAlternativePlace(val googlePlaceId: String) : FollowPlaceDetailIntent
}

sealed interface FollowPlaceDetailSideEffect : UiSideEffect {
    data class NavigateToBrowser(val url: String) : FollowPlaceDetailSideEffect
    data class NavigateToAlternativePlaceDetail(val googlePlaceId: String) : FollowPlaceDetailSideEffect
}
