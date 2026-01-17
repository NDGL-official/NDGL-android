package com.yapp.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

abstract class BaseViewModel<S : UiState, I : UiIntent, SE : UiSideEffect>(
    initialState: S,
) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _sideEffect: Channel<SE> = Channel(BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    fun onIntent(intent: I) =
        viewModelScope.launch {
            handleIntent(intent)
        }

    protected abstract suspend fun handleIntent(intent: I)

    protected fun reduce(block: S.() -> S) =
        viewModelScope.launch {
            _state.update { it.block() }
        }

    protected fun postSideEffect(effect: SE) =
        viewModelScope.launch { _sideEffect.send(effect) }
}

@Composable
fun <S : UiState, I : UiIntent, SE : UiSideEffect> BaseViewModel<S, I, SE>.collectSideEffect(
    sideEffect: (suspend (sideEffect: SE) -> Unit),
) {
    val sideEffectFlow = this.sideEffect
    val lifecycleOwner = LocalLifecycleOwner.current

    val callback by rememberUpdatedState(newValue = sideEffect)

    LaunchedEffect(sideEffectFlow, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                sideEffectFlow.collect { callback(it) }
            }
        }
    }
}
