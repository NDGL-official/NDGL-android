package com.yapp.ndgl.feature.home

import com.yapp.ui.base.UiIntent
import com.yapp.ui.base.UiSideEffect
import com.yapp.ui.base.UiState

data class HomeState(
    val isLoading: Boolean = false,
) : UiState

sealed interface HomeIntent : UiIntent

sealed interface HomeSideEffect : UiSideEffect
