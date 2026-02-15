package com.yapp.ndgl.feature.home.main

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.yapp.ndgl.core.base.UiIntent
import com.yapp.ndgl.core.base.UiSideEffect
import com.yapp.ndgl.core.base.UiState
import com.yapp.ndgl.data.travel.model.PlaceCategory
import com.yapp.ndgl.data.travel.model.ProgramType
import com.yapp.ndgl.data.travel.model.TravelSummary
import java.time.LocalDate

@Stable
data class HomeState(
    val userName: String = "",
    val myTravel: MyTravel = MyTravel.None,
    val popularTravelSelectedTabIndex: Int = 0,
    val travelProgramTabs: List<TravelProgramTab> = emptyList(),
    val allPopularTravels: List<TravelContent> = emptyList(),
    val popularTravelsByProgram: Map<Long, List<TravelContent>> = emptyMap(),
    val recommendedContents: List<TravelSummary> = emptyList(),
) : UiState {
    @Stable
    sealed interface MyTravel {
        @Immutable
        data object None : MyTravel

        @Immutable
        data class Upcoming(
            val title: String,
            val imageUrl: String,
            val dDay: Int,
            val startDate: LocalDate,
            val endDate: LocalDate,
        ) : MyTravel

        @Immutable
        data class InProgress(
            val title: String,
            val dayCount: Int,
            val startDate: LocalDate,
            val endDate: LocalDate,
            val currentPlace: TravelPlace? = null,
        ) : MyTravel
    }

    data class TravelPlace(
        val category: PlaceCategory,
        val estimatedDuration: Int,
        val name: String,
        val thumbnailUrl: String,
    )

    val filteredPopularTravels: List<TravelContent>
        get() {
            val selectedTab = travelProgramTabs.getOrNull(popularTravelSelectedTabIndex)
            return when (selectedTab) {
                is TravelProgramTab.All, null -> allPopularTravels
                is TravelProgramTab.Custom ->
                    popularTravelsByProgram[selectedTab.programId] ?: emptyList()
            }
        }

    sealed interface TravelProgramTab {
        data object All : TravelProgramTab

        data class Custom(
            val programId: Long,
            val name: String,
            val type: ProgramType,
        ) : TravelProgramTab
    }

    data class TravelContent(
        val travelId: Long,
        val title: String,
        val country: String,
        val city: String,
        val nights: Int,
        val days: Int,
        val programName: String,
        val thumbnail: String,
    )
}

sealed interface HomeIntent : UiIntent {
    data class SelectPopularTravelTab(val index: Int) : HomeIntent
}

sealed interface HomeSideEffect : UiSideEffect
