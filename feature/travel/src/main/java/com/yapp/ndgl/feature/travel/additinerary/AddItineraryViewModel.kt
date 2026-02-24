package com.yapp.ndgl.feature.travel.additinerary

import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.yapp.ndgl.core.base.BaseViewModel
import com.yapp.ndgl.core.util.suspendRunCatching
import com.yapp.ndgl.data.travel.model.AddPlaceEvent
import com.yapp.ndgl.data.travel.repository.PlaceRepository
import com.yapp.ndgl.data.travel.repository.UserTravelRepository
import com.yapp.ndgl.feature.travel.model.PlacePhoto
import com.yapp.ndgl.feature.travel.model.toPlaceCategory
import com.yapp.ndgl.feature.travel.model.toPlaceInfo
import com.yapp.ndgl.feature.travel.model.toPlaceType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

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
        loadRecommendedPlaces()
        loadBookmarkedPlaces()
    }

    private fun loadRecommendedPlaces() = viewModelScope.launch {
        val travelInfo = suspendRunCatching {
            userTravelRepository.getUserTravelTemplateContentInfo(travelId)
        }.getOrNull()

        // FIXME: 임시 추천 장소
        val recommendedPlaces = if (travelInfo != null) {
            val firstChar = travelInfo.city.firstOrNull()?.toString() ?: travelInfo.countryName?.firstOrNull()?.toString() ?: ""
            if (firstChar.isNotBlank()) {
                // 1. 검색으로 5개 장소 찾기
                val searchResults = suspendRunCatching {
                    placeRepository.searchKeyword(
                        keyword = firstChar,
                        countryCode = countryCode,
                    )
                }.getOrNull()?.results?.take(5) ?: emptyList()

                // 2. 5개 장소를 동시에 조회 (병렬 처리)
                val placeDetails = searchResults.map { result ->
                    async {
                        suspendRunCatching {
                            placeRepository.getPlace(result.googlePlaceId)
                        }.getOrNull()
                    }
                }.awaitAll()

                // 3. SelectablePlace로 변환
                placeDetails.mapNotNull { response ->
                    response?.let {
                        SelectablePlace(
                            googlePlaceId = it.place.id,
                            name = it.place.name,
                            placeType = it.place.category.toPlaceType(),
                            thumbnail = it.place.thumbnail,
                        )
                    }
                }
            } else {
                emptyList()
            }
        } else {
            emptyList()
        }

        reduce { copy(recommendedPlaces = recommendedPlaces) }
    }

    private fun loadBookmarkedPlaces() = viewModelScope.launch {
        suspendRunCatching {
            placeRepository.getBookmarkedPlaces()
        }.onSuccess { response ->
            val bookmarkedPlaces = response.places.map { place ->
                SelectablePlace(
                    googlePlaceId = place.googlePlaceId,
                    name = place.name,
                    placeType = place.category.toPlaceType(),
                    thumbnail = place.thumbnail,
                )
            }

            reduce { copy(bookmarkedPlaces = bookmarkedPlaces) }
        }.onFailure {
            // FIXME: Handle Error
            Timber.d("${it.message} $it")
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
            }.onFailure {
                // FIXME: Handle Error
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
        }.onFailure {
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
        val checkedPlaceId = state.value.checkedPlaceId

        // 검색해서 선택한 장소가 있는 경우
        if (selectedDetail != null) {
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
            return@launch
        }

        // AddItineraryBottomSheet에서 체크박스로 선택한 장소가 있는 경우
        if (checkedPlaceId != null) {
            // 장소 상세 정보 가져오기
            val placeDetail = suspendRunCatching {
                placeRepository.getPlace(checkedPlaceId)
            }.getOrNull()

            if (placeDetail != null) {
                userTravelRepository.emitAddPlaceEvent(
                    AddPlaceEvent(
                        travelId = travelId,
                        day = day,
                        googlePlaceId = placeDetail.place.id,
                        name = placeDetail.place.name,
                        latitude = placeDetail.place.location.latitude,
                        longitude = placeDetail.place.location.longitude,
                        thumbnail = placeDetail.place.thumbnail,
                        placeType = placeDetail.place.category,
                        address = placeDetail.place.formattedAddress,
                        phoneNumber = placeDetail.place.nationalPhoneNumber
                            ?: placeDetail.place.internationalPhoneNumber,
                        googleMapsUri = placeDetail.place.googleMapsUri,
                        websiteUrl = placeDetail.place.websiteUri,
                        rating = placeDetail.place.rating,
                        userRatingCount = placeDetail.place.userRatingCount,
                        estimatedDuration = 60, // 기본값 60분
                    ),
                )
                postSideEffect(AddItinerarySideEffect.NavigateBack)
                return@launch
            }
        }

        // 선택한 장소가 없으면 그냥 뒤로가기
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

    private fun bookmarkPlace(placeId: String) = viewModelScope.launch {
        val currentPlaceDetail = state.value.selectedPlaceDetail
        val isBookmarked = currentPlaceDetail?.placeInfo?.isBookMarked ?: false

        suspendRunCatching {
            if (isBookmarked) {
                placeRepository.unBookmarkPlace(placeId)
            } else {
                placeRepository.bookmarkPlace(placeId)
            }
        }.onSuccess {
            currentPlaceDetail?.let { detail ->
                reduce {
                    copy(
                        selectedPlaceDetail = detail.copy(
                            placeInfo = detail.placeInfo.copy(isBookMarked = !isBookmarked),
                        ),
                    )
                }
            }

            // 북마크 리스트 새로고침
            loadBookmarkedPlaces()
        }.onFailure {
            // TODO: 에러 처리
        }
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
