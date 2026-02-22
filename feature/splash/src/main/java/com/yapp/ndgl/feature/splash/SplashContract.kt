package com.yapp.ndgl.feature.splash

import com.yapp.ndgl.core.base.UiIntent
import com.yapp.ndgl.core.base.UiSideEffect
import com.yapp.ndgl.core.base.UiState

class SplashState : UiState

sealed interface SplashUiIntent : UiIntent

sealed interface SplashSideEffect : UiSideEffect {
    data class NavigateToHome(val isFirstUser: Boolean) : SplashSideEffect
}
