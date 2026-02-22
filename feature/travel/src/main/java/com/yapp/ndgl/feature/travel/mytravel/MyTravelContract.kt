package com.yapp.ndgl.feature.travel.mytravel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.yapp.ndgl.core.base.UiIntent
import com.yapp.ndgl.core.base.UiSideEffect
import com.yapp.ndgl.core.base.UiState
import com.yapp.ndgl.data.travel.model.PlaceCategory
import com.yapp.ndgl.data.travel.model.ProgramType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate

@Immutable
data class MyTravelState(
    val upcomingTravel: UpcomingTravel? = null,
    val upcomingTravels: ImmutableList<UpcomingTravelItem> = persistentListOf(),
    val recommendedTravels: ImmutableList<RecommendedTravel> = persistentListOf(),
) : UiState {
    @Stable
    sealed class UpcomingTravel {
        abstract val travelId: Long
        abstract val title: String
        abstract val startDate: LocalDate
        abstract val endDate: LocalDate

        @Immutable
        data class Upcoming(
            override val travelId: Long,
            override val title: String,
            override val startDate: LocalDate,
            override val endDate: LocalDate,
            val imageUrl: String,
            val dDay: Int,
        ) : UpcomingTravel()

        @Immutable
        data class InProgress(
            override val travelId: Long,
            override val title: String,
            override val startDate: LocalDate,
            override val endDate: LocalDate,
            val dayCount: Int,
            val currentPlace: TravelPlace? = null,
        ) : UpcomingTravel()
    }

    @Immutable
    data class TravelPlace(
        val placeId: String,
        val category: PlaceCategory,
        val estimatedDuration: Int,
        val name: String,
        val thumbnailUrl: String,
    )

    @Immutable
    data class UpcomingTravelItem(
        val travelId: Long,
        val title: String,
        val startDate: LocalDate,
        val endDate: LocalDate,
        val imageUrl: String,
        val dDay: Int,
    )

    @Immutable
    data class RecommendedTravel(
        val travelId: Long,
        val title: String,
        val country: String,
        val city: String,
        val nights: Int,
        val days: Int,
        val programName: String,
        val programType: ProgramType,
        val thumbnailUrl: String,
    )
}

sealed interface MyTravelIntent : UiIntent {
    data object ClickSearchTravelTemplate : MyTravelIntent
    data object ClickSettings : MyTravelIntent
    data class ClickTravel(val travelId: Long) : MyTravelIntent
    data class ClickTravelDetail(val travelId: Long) : MyTravelIntent
    data class ClickPlaceDetail(val placeId: String) : MyTravelIntent
    data object ClickFindNewTravel : MyTravelIntent
}

sealed interface MyTravelSideEffect : UiSideEffect {
    data object NavigateToSearchTravelTemplate : MyTravelSideEffect
    data object NavigateToSettings : MyTravelSideEffect
    data class NavigateToFollowTravel(val travelId: Long, val days: Int) : MyTravelSideEffect
    data class NavigateToTravelDetail(val travelId: Long) : MyTravelSideEffect
    data class NavigateToTravelPlace(val placeId: String) : MyTravelSideEffect
    data object NavigateToPopularTravelList : MyTravelSideEffect
}
