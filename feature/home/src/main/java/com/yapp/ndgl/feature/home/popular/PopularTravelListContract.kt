package com.yapp.ndgl.feature.home.popular

import androidx.compose.runtime.Immutable
import com.yapp.ndgl.core.base.UiIntent
import com.yapp.ndgl.core.base.UiSideEffect
import com.yapp.ndgl.core.base.UiState
import com.yapp.ndgl.feature.home.model.TravelContent
import com.yapp.ndgl.feature.home.model.TravelProgramTab
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

@Immutable
data class PopularTravelListState(
    val travelProgramTabs: ImmutableList<TravelProgramTab> = persistentListOf(),
    val selectedTabIndex: Int = 0,
    val allPopularTravels: ImmutableList<TravelContent> = persistentListOf(),
    val popularTravelsByProgram: ImmutableMap<Long, ImmutableList<TravelContent>> = persistentMapOf(),
) : UiState {
    val selectedProgramTravels: ImmutableList<TravelContent> by lazy {
        val selectTab = travelProgramTabs.getOrElse(selectedTabIndex) { TravelProgramTab.All }

        when (selectTab) {
            TravelProgramTab.All -> allPopularTravels
            is TravelProgramTab.Custom -> popularTravelsByProgram.getOrDefault(
                selectTab.programId,
                persistentListOf(),
            )
        }
    }
}

sealed interface PopularTravelListIntent : UiIntent {
    data object ClickSearchTravelTemplate : PopularTravelListIntent
    data class SelectPopularTravelTab(val index: Int) : PopularTravelListIntent
    data class ClickTravel(val travelId: Long, val days: Int) : PopularTravelListIntent
}

sealed interface PopularTravelListSideEffect : UiSideEffect {
    data object NavigateToSearchTravelTemplate : PopularTravelListSideEffect
    data class NavigateToFollowTravel(
        val travelId: Long,
        val days: Int,
    ) : PopularTravelListSideEffect
}
