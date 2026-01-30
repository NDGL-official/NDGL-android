package com.yapp.ndgl.feature.home

import androidx.lifecycle.viewModelScope
import com.yapp.ndgl.core.util.suspendRunCatching
import com.yapp.ndgl.data.auth.repository.AuthRepository
import com.yapp.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : BaseViewModel<HomeState, HomeIntent, HomeSideEffect>(
    initialState = HomeState(),
) {
    init {
        initSession()
    }

    override suspend fun handleIntent(intent: HomeIntent) {
        TODO("Not yet implemented")
    }

    private fun initSession() = viewModelScope.launch {
        suspendRunCatching {
            authRepository.initSession()
        }.onSuccess {
            Timber.d("initSession() 성공")
        }.onFailure {
            Timber.e("initSession() 실패")
        }
    }
}
