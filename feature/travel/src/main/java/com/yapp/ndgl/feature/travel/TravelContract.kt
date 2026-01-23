package com.yapp.ndgl.feature.travel

import com.yapp.ui.base.UiIntent
import com.yapp.ui.base.UiSideEffect
import com.yapp.ui.base.UiState

data class TravelState(
    val displayText: String = "초기 상태"
) : UiState


sealed interface TravelIntent : UiIntent {
    data class ClickTravel(val travelId: Int) : TravelIntent
}

sealed interface TravelSideEffect : UiSideEffect {
    data class NavigateToDetail(val travelId: Int) : TravelSideEffect
}
