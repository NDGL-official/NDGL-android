package com.yapp.ndgl.feature.splash

import androidx.lifecycle.viewModelScope
import com.yapp.ndgl.core.base.BaseViewModel
import com.yapp.ndgl.core.base.UiIntent
import com.yapp.ndgl.core.util.suspendRunCatching
import com.yapp.ndgl.data.auth.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : BaseViewModel<SplashState, UiIntent, SplashSideEffect>(
    initialState = SplashState(),
) {
    init {
        initSession()
    }

    private fun initSession() = viewModelScope.launch {
        suspendRunCatching {
            authRepository.initSession()
        }.onSuccess { isFirstUser ->
            postSideEffect(SplashSideEffect.NavigateToHome(isFirstUser = isFirstUser))
        }.onFailure {
            // FIXME: 에러 뷰
        }
    }

    override suspend fun handleIntent(intent: UiIntent) {
        // Splash에 따로 intent 존재하지 않음
    }
}
