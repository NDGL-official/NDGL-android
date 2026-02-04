package com.yapp.ndgl.feature.home

import com.yapp.ndgl.core.base.UiIntent
import com.yapp.ndgl.core.base.UiSideEffect
import com.yapp.ndgl.core.base.UiState

data class HomeState(
    val isLoading: Boolean = false,
) : UiState

sealed interface HomeIntent : UiIntent

sealed interface HomeSideEffect : UiSideEffect
