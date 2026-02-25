package com.yapp.ndgl.feature.home.search

import androidx.lifecycle.viewModelScope
import com.yapp.ndgl.core.base.BaseViewModel
import com.yapp.ndgl.core.util.suspendRunCatching
import com.yapp.ndgl.data.travel.repository.TravelTemplateRepository
import com.yapp.ndgl.feature.home.model.TravelContent
import com.yapp.ndgl.feature.home.search.TemplateSearchState.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TemplateSearchViewModel @Inject constructor(
    private val travelTemplateRepository: TravelTemplateRepository,
) : BaseViewModel<TemplateSearchState, TemplateSearchIntent, TemplateSearchSideEffect>(
    initialState = TemplateSearchState(
        searchKeyword = "",
        searchResult = SearchResult.Idle,
    ),
) {
    override suspend fun handleIntent(intent: TemplateSearchIntent) {
        when (intent) {
            is TemplateSearchIntent.UpdateSearchKeyword -> updateKeyword(intent.keyword)
            is TemplateSearchIntent.SearchTemplate -> searchTravelTemplates(intent.keyword)
            is TemplateSearchIntent.ClickTravelTemplate -> postNavigateToTravelTemplate(intent.travelId, intent.days)
        }
    }

    private fun updateKeyword(keyword: String) {
        if (state.value.searchKeyword != keyword) {
            reduce { copy(searchKeyword = keyword) }
        }
    }

    private fun searchTravelTemplates(keyword: String) {
        viewModelScope.launch {
            suspendRunCatching { travelTemplateRepository.searchTravelTemplates(keyword) }
                .onSuccess { result ->
                    val travels = result.content.map { travel ->
                        TravelContent(
                            travelId = travel.id,
                            title = travel.title,
                            country = travel.country,
                            city = travel.city,
                            nights = travel.nights,
                            days = travel.days,
                            programName = travel.programName,
                            programType = travel.programType,
                            thumbnail = travel.thumbnail ?: "",
                        )
                    }
                    reduce {
                        copy(
                            searchResult = if (travels.isNotEmpty()) {
                                SearchResult.Success(travels = travels)
                            } else {
                                SearchResult.Empty
                            },
                        )
                    }
                }.onFailure {
                    reduce { copy(searchResult = SearchResult.Error) }
                }
        }
    }

    private fun postNavigateToTravelTemplate(travelId: Long, days: Int) {
        postSideEffect(
            TemplateSearchSideEffect.NavigateToFollowTravel(
                travelId = travelId,
                days = days,
            ),
        )
    }
}
