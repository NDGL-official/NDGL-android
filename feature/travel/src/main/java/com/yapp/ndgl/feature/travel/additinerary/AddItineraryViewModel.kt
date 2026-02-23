package com.yapp.ndgl.feature.travel.additinerary

import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.yapp.ndgl.core.base.BaseViewModel
import com.yapp.ndgl.core.util.suspendRunCatching
import com.yapp.ndgl.data.travel.model.AddPlaceEvent
import com.yapp.ndgl.data.travel.repository.PlaceRepository
import com.yapp.ndgl.data.travel.repository.UserTravelRepository
import com.yapp.ndgl.feature.travel.model.PlacePhoto
import com.yapp.ndgl.feature.travel.model.PlaceType
import com.yapp.ndgl.feature.travel.model.toPlaceCategory
import com.yapp.ndgl.feature.travel.model.toPlaceInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO("테스트용으로 지워야함")
private const val TEST_THUMBNAIL_URL = "https://picsum.photos/200"

@HiltViewModel(assistedFactory = AddItineraryViewModel.Factory::class)
class AddItineraryViewModel @AssistedInject constructor(
    @Assisted("travelId") private val travelId: Long,
    @Assisted("day") private val day: Int,
    @Assisted("countryCode") private val countryCode: String,
    @Assisted("representativeLatLng") private val representativeLatLng: LatLng,
    private val placeRepository: PlaceRepository,
    private val userTravelRepository: UserTravelRepository,
) : BaseViewModel<AddItineraryState, AddItineraryIntent, AddItinerarySideEffect>(
    initialState = AddItineraryState(travelId = travelId, day = day, countryCode = countryCode, representativeLatLng = representativeLatLng),
) {
    private var searchJob: Job? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        // FIXME: Repository에서 현재 일차 장소 + 추천 장소 로드
        val stubRecommendedPlaces = listOf(
            SelectablePlace(
                googlePlaceId = "ChIJKWGrTn8hQTUR7zeTLtzYJL4",
                name = "카피톨리니 박물관 (Musei Capitolini)",
                placeType = PlaceType.ATTRACTION,
                thumbnail = TEST_THUMBNAIL_URL,
            ),
            SelectablePlace(
                googlePlaceId = "1",
                name = "콜로세움 (Colosseo)",
                placeType = PlaceType.ATTRACTION,
                thumbnail = TEST_THUMBNAIL_URL,
            ),
            SelectablePlace(
                googlePlaceId = "2",
                name = "트레비 분수 (Fontana di Trevi)",
                placeType = PlaceType.ATTRACTION,
                thumbnail = TEST_THUMBNAIL_URL,
            ),
            SelectablePlace(
                googlePlaceId = "3",
                name = "젤라테리아 파씨 (Gelateria Fassi)",
                placeType = PlaceType.CAFE,
                thumbnail = TEST_THUMBNAIL_URL,
            ),
            SelectablePlace(
                googlePlaceId = "4",
                name = "리스토란테 일 팔라초 (Ristorante Il Palazzo)",
                placeType = PlaceType.RESTAURANT,
                thumbnail = TEST_THUMBNAIL_URL,
            ),
            SelectablePlace(
                googlePlaceId = "5",
                name = "카피톨리니 박물관 (Musei Capitolini)",
                placeType = PlaceType.ATTRACTION,
                thumbnail = TEST_THUMBNAIL_URL,
            ),
            SelectablePlace(
                googlePlaceId = "6",
                name = "리스토란테 일 팔라초 (Ristorante Il Palazzo)",
                placeType = PlaceType.RESTAURANT,
                thumbnail = TEST_THUMBNAIL_URL,
            ),
            SelectablePlace(
                googlePlaceId = "7",
                name = "카피톨리니 박물관 (Musei Capitolini)",
                placeType = PlaceType.ATTRACTION,
                thumbnail = TEST_THUMBNAIL_URL,
            ),
        )
        val stubBookmarkedPlaces = listOf(
            SelectablePlace(
                googlePlaceId = "8",
                name = "카페 산트에우스타키오 (Caffè Sant'Eustachio)",
                placeType = PlaceType.CAFE,
                thumbnail = TEST_THUMBNAIL_URL,
            ),
        )
        reduce {
            copy(
                recommendedPlaces = stubRecommendedPlaces,
                bookmarkedPlaces = stubBookmarkedPlaces,
            )
        }
    }

    override suspend fun handleIntent(intent: AddItineraryIntent) {
        when (intent) {
            is AddItineraryIntent.ClickBack -> clickBack()
            is AddItineraryIntent.UpdateKeyword -> updateKeyword(intent.keyword)
            is AddItineraryIntent.SearchKeyword -> searchKeyword()
            is AddItineraryIntent.FocusSearch -> focusSearch()
            is AddItineraryIntent.SelectSearchResult -> selectSearchResult(intent.searchResult)
            is AddItineraryIntent.SelectChip -> selectChip(intent.chip)
            is AddItineraryIntent.CheckSelectablePlace -> checkSelectablePlace(intent.googlePlaceId)
            is AddItineraryIntent.ClickAddItinerary -> clickAddItinerary()
            is AddItineraryIntent.ClickAddress -> clickAddress()
            is AddItineraryIntent.ClickMenu -> clickMenu()
            is AddItineraryIntent.BookMarkPlace -> bookmarkPlace(intent.placeId)
            is AddItineraryIntent.ClickSelectablePlace -> clickSelectablePlace(intent.googlePlaceId)
        }
    }

    private fun clickBack() {
        if (state.value.isSearched && state.value.selectedPlaceDetail != null) {
            reduce {
                copy(
                    isSearchFocused = true,
                    selectedPlaceDetail = null,
                )
            }
        } else if (state.value.isSearchFocused || state.value.isSearched) {
            reduce { copy(isSearchFocused = false, isSearched = false) }
        } else {
            postSideEffect(AddItinerarySideEffect.NavigateBack)
        }
    }

    private fun updateKeyword(keyword: String) {
        reduce { copy(keyword = keyword) }
        if (keyword.isBlank()) {
            searchJob?.cancel()
            reduce { copy(isSearched = false, searchResults = emptyList()) }
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (state.value.searchResults.isNotEmpty()) delay(300L)
            suspendRunCatching {
                placeRepository.searchKeyword(
                    keyword = keyword,
                    countryCode = state.value.countryCode,
                )
            }.onSuccess { response ->
                val results = response.results.map { result ->
                    SearchResult(
                        googlePlaceId = result.googlePlaceId,
                        name = result.name,
                    )
                }
                reduce { copy(isSearched = true, searchResults = results) }
            }.onFailure { error ->
                // FIXME: 임시 조치
                reduce { copy(isSearched = true, searchResults = emptyList()) }
            }
        }
    }

    private suspend fun searchKeyword() {
        reduce { copy(selectedPlaceDetail = null, isSearchFocused = true) }
        val keyword = state.value.keyword
        if (keyword.isBlank()) {
            return
        }

        suspendRunCatching {
            placeRepository.searchKeyword(
                keyword = keyword,
                countryCode = state.value.countryCode,
            )
        }.onSuccess { response ->
            val results = response.results.map { result ->
                SearchResult(
                    googlePlaceId = result.googlePlaceId,
                    name = result.name,
                )
            }
            reduce { copy(isSearched = true, searchResults = results) }
        }.onFailure { error ->
            reduce { copy(isSearched = true, searchResults = emptyList()) }
        }
    }

    private fun focusSearch() {
        reduce {
            copy(
                isSearchFocused = true,
                isSearched = state.value.searchResults.isNotEmpty(),
                selectedPlaceDetail = null,
            )
        }
    }

    private fun selectSearchResult(searchResult: SearchResult) {
        placeRepository.resetSessionToken()
        loadPlaceDetail(searchResult.googlePlaceId)
        reduce {
            copy(
                isSearchFocused = false,
                isSearched = true,
                keyword = searchResult.name,
            )
        }
    }

    private fun loadPlaceDetail(placeId: String) = viewModelScope.launch {
        suspendRunCatching {
            placeRepository.getPlace(placeId)
        }.onSuccess { response ->
            loadPlacePhotos(placeId)
            reduce {
                val currentDetail = selectedPlaceDetail ?: SelectedPlaceDetail()
                copy(
                    selectedPlaceDetail = currentDetail.copy(
                        placeInfo = response.toPlaceInfo(),
                    ),
                )
            }
        }.onFailure {
            // FIXME: 에러 뷰
        }
    }

    private fun loadPlacePhotos(placeId: String) = viewModelScope.launch {
        repeat(3) {
            delay(1000)
            val result = suspendRunCatching { placeRepository.getPlacePhotos(placeId) }
            val photos = result.getOrNull()?.photos
            if (!photos.isNullOrEmpty()) {
                reduce {
                    val currentDetail = selectedPlaceDetail ?: SelectedPlaceDetail()

                    copy(
                        selectedPlaceDetail = currentDetail.copy(
                            photos = photos.map { PlacePhoto(url = it.photoUri, width = it.widthPx, height = it.heightPx) },
                        ),
                    )
                }
                return@launch
            }

            result.onFailure {
                // FIXME: 에러 뷰
            }
        }
    }

    private fun selectChip(chip: AddItineraryChip) {
        reduce { copy(selectedChip = chip) }
        // FIXME: 칩에 따라 추천 장소 목록 로드
    }

    private fun checkSelectablePlace(googlePlaceId: String) {
        if (state.value.checkedPlaceId == googlePlaceId) {
            reduce { copy(checkedPlaceId = null) }
        } else {
            reduce { copy(checkedPlaceId = googlePlaceId) }
        }
    }

    private fun clickSelectablePlace(googlePlaceId: String) {
        postSideEffect(AddItinerarySideEffect.NavigateToAddPlace(googlePlaceId))
    }

    private fun clickAddItinerary() = viewModelScope.launch {
        val selectedDetail = state.value.selectedPlaceDetail

        if (selectedDetail == null) {
            postSideEffect(AddItinerarySideEffect.NavigateBack)
            return@launch
        }

        val placeInfo = selectedDetail.placeInfo
        userTravelRepository.emitAddPlaceEvent(
            AddPlaceEvent(
                travelId = travelId,
                day = day,
                googlePlaceId = placeInfo.googlePlaceId,
                name = placeInfo.name,
                latitude = placeInfo.latitude,
                longitude = placeInfo.longitude,
                thumbnail = placeInfo.thumbnail,
                placeType = placeInfo.placeType.toPlaceCategory(),
                address = placeInfo.address,
                phoneNumber = placeInfo.phoneNumber,
                googleMapsUri = placeInfo.googleMapsUri,
                websiteUrl = placeInfo.websiteUrl,
                rating = placeInfo.rating,
                userRatingCount = placeInfo.userRatingCount,
                estimatedDuration = placeInfo.estimatedDuration.inWholeMinutes.toInt(),
            ),
        )

        postSideEffect(AddItinerarySideEffect.NavigateBack)
    }

    private fun clickAddress() {
        val url = state.value.selectedPlaceDetail?.placeInfo?.googleMapsUri ?: return
        if (url.isBlank()) return
        postSideEffect(AddItinerarySideEffect.NavigateToBrowser(url))
    }

    private fun clickMenu() {
        val url = state.value.selectedPlaceDetail?.placeInfo?.websiteUrl ?: return
        if (url.isBlank()) return
        postSideEffect(AddItinerarySideEffect.NavigateToBrowser(url))
    }

    private fun bookmarkPlace(placeId: String) {
        // FIXME: 북마크 저장 기능 연동
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("travelId") travelId: Long,
            @Assisted("day") day: Int,
            @Assisted("countryCode") countryCode: String,
            @Assisted("representativeLatLng") representativeLatLng: LatLng,
        ): AddItineraryViewModel
    }
}
