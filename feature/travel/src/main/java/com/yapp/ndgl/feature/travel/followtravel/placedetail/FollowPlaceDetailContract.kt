package com.yapp.ndgl.feature.travel.followtravel.placedetail

import com.yapp.ndgl.core.base.UiIntent
import com.yapp.ndgl.core.base.UiSideEffect
import com.yapp.ndgl.core.base.UiState
import com.yapp.ndgl.core.util.formatDecimal
import com.yapp.ndgl.feature.travel.model.AlternativePlace
import com.yapp.ndgl.feature.travel.model.PlaceDetailTab
import com.yapp.ndgl.feature.travel.model.PlacePhoto
import com.yapp.ndgl.feature.travel.model.PlaceType
import com.yapp.ndgl.feature.travel.model.PriceRange
import com.yapp.ndgl.feature.travel.model.TipContent
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

data class FollowPlaceDetailState(
    val placeInfo: FollowPlaceInfo = FollowPlaceInfo(),
    val selectedTab: PlaceDetailTab = PlaceDetailTab.INFO,
    val photos: List<PlacePhoto> = emptyList(),
) : UiState

data class FollowPlaceInfo(
    val id: String = "",
    val name: String = "",
    val placeType: PlaceType = PlaceType.ATTRACTION,
    val priceRange: PriceRange? = null,
    val rating: Double? = null,
    val userRatingCount: Int? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
    val openingHours: String? = null,
    val googleMapsUri: String? = null,
    val websiteUrl: String? = null,
    val estimatedDuration: Duration = 1.hours,
    val thumbnail: String = "",
    val tipContent: TipContent? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val alternativePlaces: List<AlternativePlace> = emptyList(),
) {
    val formattedRatingCount: String
        get() = userRatingCount?.formatDecimal() ?: ""
}

sealed interface FollowPlaceDetailIntent : UiIntent {
    data class SelectTab(val tab: PlaceDetailTab) : FollowPlaceDetailIntent
    data object ClickAddress : FollowPlaceDetailIntent
    data object ClickMenu : FollowPlaceDetailIntent
}

sealed interface FollowPlaceDetailSideEffect : UiSideEffect {
    data class NavigateToBrowser(val url: String) : FollowPlaceDetailSideEffect
}
