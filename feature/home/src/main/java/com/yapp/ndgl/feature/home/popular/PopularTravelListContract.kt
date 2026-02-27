package com.yapp.ndgl.feature.home.popular

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.yapp.ndgl.core.base.UiIntent
import com.yapp.ndgl.core.base.UiSideEffect
import com.yapp.ndgl.core.base.UiState
import com.yapp.ndgl.feature.home.model.TravelContent
import com.yapp.ndgl.feature.home.model.TravelProgramTab
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf

@Stable
sealed class PopularTravelListState : UiState {
    data object Loading : PopularTravelListState()

    @Immutable
    data class Success(
        val travelProgramTabs: ImmutableList<TravelProgramTab>,
        val allPopularTravels: ImmutableList<PopularTravelListItem>,
        val popularTravelsByProgram: ImmutableMap<Long, ImmutableList<PopularTravelListItem>>,
        val selectedTabIndex: Int = 0,
    ) : PopularTravelListState() {
        val selectedProgramTravels: ImmutableList<PopularTravelListItem> by lazy {
            val selectTab = travelProgramTabs.getOrElse(selectedTabIndex) { TravelProgramTab.All }

            when (selectTab) {
                TravelProgramTab.All -> allPopularTravels
                is TravelProgramTab.Custom -> popularTravelsByProgram.getOrDefault(
                    selectTab.programId,
                    persistentListOf(),
                )
            }
        }

        sealed interface PopularTravelListItem {
            data class Travel(
                val travelContent: TravelContent,
            ) : PopularTravelListItem

            data class Loading(
                val nextPage: Int,
            ) : PopularTravelListItem
        }
    }

    data object Error : PopularTravelListState()
}

sealed interface PopularTravelListIntent : UiIntent {
    data object ClickSearchTravelTemplate : PopularTravelListIntent
    data class SelectPopularTravelTab(val index: Int) : PopularTravelListIntent
    data class ClickTravel(val travelId: Long, val days: Int) : PopularTravelListIntent
    data class LoadMore(val nextPage: Int) : PopularTravelListIntent
    data object ClickRetry : PopularTravelListIntent
}

sealed interface PopularTravelListSideEffect : UiSideEffect {
    data object NavigateToSearchTravelTemplate : PopularTravelListSideEffect
    data class NavigateToFollowTravel(
        val travelId: Long,
        val days: Int,
    ) : PopularTravelListSideEffect

    data class ShowSnackBar(val type: Type) : PopularTravelListSideEffect {
        enum class Type {
            ERR_UNKNOWN,
        }
    }
}
