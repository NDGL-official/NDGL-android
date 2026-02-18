package com.yapp.ndgl.feature.travel.addplace

import com.yapp.ndgl.core.base.UiIntent
import com.yapp.ndgl.core.base.UiSideEffect
import com.yapp.ndgl.core.base.UiState
import com.yapp.ndgl.core.util.formatDecimal
import com.yapp.ndgl.feature.travel.model.PlaceDetailTab
import com.yapp.ndgl.feature.travel.model.PlacePhoto
import com.yapp.ndgl.feature.travel.model.PlaceType
import com.yapp.ndgl.feature.travel.model.PriceRange
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

data class AddPlaceState(
    val placeInfo: AddPlaceInfo = AddPlaceInfo(),
    val selectedTab: PlaceDetailTab = PlaceDetailTab.INFO,
    val photos: List<PlacePhoto> = emptyList(),
) : UiState

data class AddPlaceInfo(
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
    val thumbnail: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
) {
    val formattedRatingCount: String
        get() = userRatingCount?.formatDecimal() ?: ""
}

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
