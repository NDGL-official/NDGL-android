package com.yapp.ndgl.feature.travel.placedetail

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

data class PlaceDetailState(
    val placeInfo: PlaceInfo = PlaceInfo(),
    val selectedTab: PlaceDetailTab = PlaceDetailTab.INFO,
    val photos: List<PlacePhoto> = emptyList(),
    val selectedAlternativePlace: AlternativePlace? = null,
    val showChangeModal: Boolean = false,
) : UiState

data class PlaceInfo(
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
    val alternativePlaces: List<AlternativePlace> = emptyList(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
) {
    val formattedRatingCount: String
        get() = userRatingCount?.formatDecimal() ?: ""
}

sealed interface PlaceDetailIntent : UiIntent {
    data class SelectTab(val tab: PlaceDetailTab) : PlaceDetailIntent
    data class ClickChangePlace(val alternativePlace: AlternativePlace) : PlaceDetailIntent
    data object ConfirmChangePlace : PlaceDetailIntent
    data object DismissChangeModal : PlaceDetailIntent
    data object ClickAddress : PlaceDetailIntent
    data object ClickMenu : PlaceDetailIntent
}

sealed interface PlaceDetailSideEffect : UiSideEffect {
    data class NavigateToBrowser(val url: String) : PlaceDetailSideEffect
}
