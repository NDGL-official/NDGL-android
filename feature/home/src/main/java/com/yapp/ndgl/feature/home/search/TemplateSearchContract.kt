package com.yapp.ndgl.feature.home.search

import androidx.compose.runtime.Stable
import com.yapp.ndgl.core.base.UiIntent
import com.yapp.ndgl.core.base.UiSideEffect
import com.yapp.ndgl.core.base.UiState
import com.yapp.ndgl.feature.home.model.TravelContent

@Stable
data class TemplateSearchState(
    val searchKeyword: String,
    val searchResult: SearchResult,
) : UiState {
    sealed interface SearchResult {
        data object Idle : SearchResult
        data object Empty : SearchResult
        data class Success(val travels: List<TravelContent>) : SearchResult
        data object Error : SearchResult
    }
}

sealed interface TemplateSearchIntent : UiIntent {
    data class UpdateSearchKeyword(val keyword: String) : TemplateSearchIntent
    data class SearchTemplate(val keyword: String) : TemplateSearchIntent
    data class ClickTravelTemplate(val travelId: Long, val days: Int) : TemplateSearchIntent
}

sealed interface TemplateSearchSideEffect : UiSideEffect {
    data class NavigateToFollowTravel(val travelId: Long, val days: Int) : TemplateSearchSideEffect
}
