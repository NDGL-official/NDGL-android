package com.yapp.ndgl.feature.travel.additinerary

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.core.ui.util.launchBrowser
import com.yapp.ndgl.feature.travel.additinerary.component.AddItineraryBottomSheet
import com.yapp.ndgl.feature.travel.additinerary.component.AddItineraryTopBar
import com.yapp.ndgl.feature.travel.additinerary.component.NoSearchResultContent
import com.yapp.ndgl.feature.travel.additinerary.component.SearchEmptyContent
import com.yapp.ndgl.feature.travel.additinerary.component.SearchPlaceMap
import com.yapp.ndgl.feature.travel.additinerary.component.SearchResultItem
import com.yapp.ndgl.feature.travel.additinerary.component.SearchedPlaceBottomSheet
import com.yapp.ndgl.feature.travel.model.PlaceType
import kotlin.time.Duration.Companion.hours

@Composable
internal fun AddItineraryRoute(
    viewModel: AddItineraryViewModel,
    navigateBack: () -> Unit,
    navigateToAddPlace: (String) -> Unit,
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is AddItinerarySideEffect.NavigateBack -> navigateBack()
            is AddItinerarySideEffect.NavigateToBrowser -> context.launchBrowser(sideEffect.url)
            is AddItinerarySideEffect.NavigateToAddPlace -> navigateToAddPlace(sideEffect.placeId)
        }
    }

    AddItineraryScreen(
        state = state,
        clickBack = { viewModel.onIntent(AddItineraryIntent.ClickBack) },
        updateKeyword = { viewModel.onIntent(AddItineraryIntent.UpdateKeyword(it)) },
        searchKeyword = { viewModel.onIntent(AddItineraryIntent.SearchKeyword) },
        focusSearch = { viewModel.onIntent(AddItineraryIntent.FocusSearch) },
        selectSearchResult = { viewModel.onIntent(AddItineraryIntent.SelectSearchResult(it)) },
        selectChip = { viewModel.onIntent(AddItineraryIntent.SelectChip(it)) },
        checkSelectablePlace = { viewModel.onIntent(AddItineraryIntent.CheckSelectablePlace(it)) },
        clickSelectablePlace = { viewModel.onIntent(AddItineraryIntent.ClickSelectablePlace(it)) },
        clickAddItinerary = { viewModel.onIntent(AddItineraryIntent.ClickAddItinerary) },
        clickAddress = { viewModel.onIntent(AddItineraryIntent.ClickAddress) },
        clickMenu = { viewModel.onIntent(AddItineraryIntent.ClickMenu) },
        bookmarkPlace = { placeId -> viewModel.onIntent(AddItineraryIntent.BookMarkPlace(placeId)) },
    )
}

@Composable
private fun AddItineraryScreen(
    state: AddItineraryState,
    clickBack: () -> Unit,
    updateKeyword: (String) -> Unit,
    searchKeyword: () -> Unit,
    focusSearch: () -> Unit,
    selectSearchResult: (SearchResult) -> Unit,
    selectChip: (AddItineraryChip) -> Unit,
    checkSelectablePlace: (String) -> Unit,
    clickSelectablePlace: (String) -> Unit,
    clickAddItinerary: () -> Unit,
    clickAddress: () -> Unit,
    clickMenu: () -> Unit,
    bookmarkPlace: (String) -> Unit,
) {
    BackHandler {
        clickBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(if (!state.showMap) NDGLTheme.colors.white else Color.Transparent),
    ) {
        if (state.showMap) {
            SearchPlaceMap(
                modifier = Modifier
                    .fillMaxSize(),
                selectedPlaceDetail = state.selectedPlaceDetail,
                representativeLatLng = state.representativeLatLng,
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            AddItineraryTopBar(
                modifier = Modifier.statusBarsPadding(),
                keyword = state.keyword,
                isSearchFocused = state.isSearchFocused,
                clickBackButton = clickBack,
                focusSearch = focusSearch,
                updateKeyword = updateKeyword,
                searchKeyword = searchKeyword,
            )

            if (!state.showMap && state.showSearchResults) {
                Spacer(Modifier.height(17.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NDGLTheme.colors.white),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(
                        items = state.searchResults,
                        key = { it.googlePlaceId },
                    ) { result ->
                        SearchResultItem(
                            result = result,
                            onClick = { selectSearchResult(result) },
                        )
                    }
                }
            } else if (!state.showMap && state.showSearchEmptyArea) {
                Spacer(Modifier.height(120.dp))
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (state.showSearchEmptyContent) {
                        SearchEmptyContent()
                    } else if (state.showNoSearchResultContent) {
                        NoSearchResultContent()
                    }
                }
            }
        }

        if (state.showAddItineraryBottomSheet) {
            val places = if (state.selectedChip == AddItineraryChip.RECOMMENDED_PLACE) {
                state.recommendedPlaces
            } else {
                state.bookmarkedPlaces
            }

            AddItineraryBottomSheet(
                places = places,
                day = state.day,
                selectChip = selectChip,
                checkSelectablePlace = checkSelectablePlace,
                clickSelectablePlace = clickSelectablePlace,
                selectedChip = state.selectedChip,
                checkedPlaceId = state.checkedPlaceId,
                clickAddItinerary = clickAddItinerary,
            )
        }

        if (state.showSearchedPlaceBottomSheet && state.selectedPlaceDetail != null) {
            SearchedPlaceBottomSheet(
                selectedPlaceDetail = state.selectedPlaceDetail,
                clickBack = clickBack,
                clickAddress = clickAddress,
                clickMenu = clickMenu,
                bookmarkPlace = bookmarkPlace,
                clickAddItinerary = clickAddItinerary,
            )
        }
    }
}

private val previewRecommendedPlaces = listOf(
    SelectablePlace(
        googlePlaceId = "place_1",
        name = "카피톨리니 박물관 (Musei Capitolini)",
        placeType = PlaceType.ATTRACTION,
        thumbnail = "https://picsum.photos/seed/capitolini/200",
    ),
    SelectablePlace(
        googlePlaceId = "place_2",
        name = "콜로세움 (Colosseo)",
        placeType = PlaceType.ATTRACTION,
        thumbnail = "https://picsum.photos/seed/colosseo/200",
    ),
    SelectablePlace(
        googlePlaceId = "place_3",
        name = "젤라테리아 파씨 (Gelateria Fassi)",
        placeType = PlaceType.CAFE,
        thumbnail = "https://picsum.photos/seed/gelato/200",
    ),
)

private val previewSelectedPlaceDetail = SelectedPlaceDetail(
    placeInfo = PlaceInfo(
        id = "1",
        name = "콜로세움",
        placeType = PlaceType.ATTRACTION,
        rating = 4.8,
        userRatingCount = 12450,
        address = "Piazza del Colosseo, 1, 00184 Roma RM, Italy",
        phoneNumber = "+39 06 3996 7700",
        websiteUrl = "https://www.colosseo.it",
        estimatedDuration = 2.hours,
    ),
)

@Preview(showBackground = true)
@Composable
private fun AddItineraryScreenWithAddItineraryBottomSheetPreview() {
    NDGLTheme {
        AddItineraryScreen(
            state = AddItineraryState(day = 1, recommendedPlaces = previewRecommendedPlaces),
            clickBack = {},
            updateKeyword = {},
            searchKeyword = {},
            focusSearch = {},
            selectSearchResult = {},
            selectChip = {},
            checkSelectablePlace = {},
            clickSelectablePlace = {},
            clickAddItinerary = {},
            clickAddress = {},
            clickMenu = {},
            bookmarkPlace = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddItineraryWithSearchedPlaceBottomSheetPreview() {
    NDGLTheme {
        AddItineraryScreen(
            state = AddItineraryState(
                day = 1,
                isSearched = true,
                selectedPlaceDetail = previewSelectedPlaceDetail,
            ),
            clickBack = {},
            updateKeyword = {},
            searchKeyword = {},
            focusSearch = {},
            selectSearchResult = {},
            selectChip = {},
            checkSelectablePlace = {},
            clickSelectablePlace = {},
            clickAddItinerary = {},
            clickAddress = {},
            clickMenu = {},
            bookmarkPlace = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShowSearchResultsPreview() {
    NDGLTheme {
        AddItineraryScreen(
            state = AddItineraryState(
                day = 1,
                isSearchFocused = true,
                isSearched = true,
                keyword = "카피톨",
                searchResults = listOf(
                    SearchResult(googlePlaceId = "search_1", name = "카피톨리니 박물관 (Musei Capitolini)"),
                    SearchResult(googlePlaceId = "search_2", name = "카피톨리노 광장 (Piazza del Campidoglio)"),
                    SearchResult(googlePlaceId = "search_3", name = "카피톨 호텔 로마 (Capitol Hotel Roma)"),
                ),
            ),
            clickBack = {},
            updateKeyword = {},
            searchKeyword = {},
            focusSearch = {},
            selectSearchResult = {},
            selectChip = {},
            checkSelectablePlace = {},
            clickSelectablePlace = {},
            clickAddItinerary = {},
            clickAddress = {},
            clickMenu = {},
            bookmarkPlace = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShowSearchEmptyContentPreview() {
    NDGLTheme {
        AddItineraryScreen(
            state = AddItineraryState(
                day = 1,
                isSearchFocused = true,
                keyword = "",
            ),
            clickBack = {},
            updateKeyword = {},
            searchKeyword = {},
            focusSearch = {},
            selectSearchResult = {},
            selectChip = {},
            checkSelectablePlace = {},
            clickSelectablePlace = {},
            clickAddItinerary = {},
            clickAddress = {},
            clickMenu = {},
            bookmarkPlace = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShowNoSearchResultContentPreview() {
    NDGLTheme {
        AddItineraryScreen(
            state = AddItineraryState(
                day = 1,
                isSearchFocused = true,
                isSearched = true,
                keyword = "없는장소",
                searchResults = emptyList(),
            ),
            clickBack = {},
            updateKeyword = {},
            searchKeyword = {},
            focusSearch = {},
            selectSearchResult = {},
            selectChip = {},
            checkSelectablePlace = {},
            clickSelectablePlace = {},
            clickAddItinerary = {},
            clickAddress = {},
            clickMenu = {},
            bookmarkPlace = {},
        )
    }
}
