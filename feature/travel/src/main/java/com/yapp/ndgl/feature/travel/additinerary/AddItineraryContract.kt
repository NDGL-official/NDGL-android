package com.yapp.ndgl.feature.travel.additinerary

import androidx.annotation.StringRes
import com.google.android.gms.maps.model.LatLng
import com.yapp.ndgl.core.base.UiIntent
import com.yapp.ndgl.core.base.UiSideEffect
import com.yapp.ndgl.core.base.UiState
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.feature.travel.model.PlaceInfo
import com.yapp.ndgl.feature.travel.model.PlacePhoto
import com.yapp.ndgl.feature.travel.model.PlaceType

data class AddItineraryState(
    val travelId: Long = 0,
    val day: Int = 1,
    val countryCode: String = "",
    val representativeLatLng: LatLng = LatLng(37.5665, 126.9780),
    val keyword: String = "",
    val isSearchFocused: Boolean = false,
    val isSearched: Boolean = false,
    val recommendedPlaces: List<SelectablePlace> = emptyList(),
    val bookmarkedPlaces: List<SelectablePlace> = emptyList(),
    val searchResults: List<SearchResult> = emptyList(),
    val selectedChip: AddItineraryChip = AddItineraryChip.RECOMMENDED_PLACE,
    val checkedPlaceId: String? = null,
    val selectedPlaceDetail: SelectedPlaceDetail? = null,
) : UiState {
    val showAddItineraryBottomSheet: Boolean
        get() = !isSearched && !isSearchFocused && selectedPlaceDetail == null

    val showSearchedPlaceBottomSheet: Boolean
        get() = isSearched && selectedPlaceDetail != null

    val showMap: Boolean
        get() = !isSearchFocused

    val showSearchResults: Boolean
        get() = isSearchFocused && isSearched && searchResults.isNotEmpty() && selectedPlaceDetail == null

    val showSearchEmptyArea: Boolean
        get() = isSearchFocused && searchResults.isEmpty()

    val showSearchEmptyContent: Boolean
        get() = keyword.isEmpty()

    val showNoSearchResultContent: Boolean
        get() = isSearched && keyword.isNotEmpty() && searchResults.isEmpty()
}

enum class AddItineraryChip(
    @get:StringRes val labelRes: Int,
) {
    RECOMMENDED_PLACE(R.string.add_itinerary_chip_recommended),
    RECENT_SAVED_PLACE(R.string.add_itinerary_chip_recent),
}

data class SelectablePlace(
    val googlePlaceId: String,
    val name: String,
    val placeType: PlaceType = PlaceType.ATTRACTION,
    val thumbnail: String?,
)

data class SelectedPlaceDetail(
    val placeInfo: PlaceInfo = PlaceInfo(),
    val photos: List<PlacePhoto> = emptyList(),
)

data class SearchResult(
    val googlePlaceId: String,
    val name: String,
)

sealed interface AddItineraryIntent : UiIntent {
    data object ClickBack : AddItineraryIntent
    data class UpdateKeyword(val keyword: String) : AddItineraryIntent
    data object SearchKeyword : AddItineraryIntent
    data object FocusSearch : AddItineraryIntent
    data class SelectSearchResult(val searchResult: SearchResult) : AddItineraryIntent
    data class SelectChip(val chip: AddItineraryChip) : AddItineraryIntent
    data class CheckSelectablePlace(val googlePlaceId: String) : AddItineraryIntent
    data object ClickAddItinerary : AddItineraryIntent
    data object ClickAddress : AddItineraryIntent
    data object ClickMenu : AddItineraryIntent
    data class BookMarkPlace(val googlePlaceId: String) : AddItineraryIntent
    data class ClickSelectablePlace(val googlePlaceId: String) : AddItineraryIntent
}

sealed interface AddItinerarySideEffect : UiSideEffect {
    data object NavigateBack : AddItinerarySideEffect
    data class NavigateToBrowser(val url: String) : AddItinerarySideEffect
    data class NavigateToAddPlace(val googlePlaceId: String) : AddItinerarySideEffect
}
