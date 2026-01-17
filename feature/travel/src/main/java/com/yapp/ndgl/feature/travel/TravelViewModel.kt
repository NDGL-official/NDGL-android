package com.yapp.ndgl.feature.travel

import com.yapp.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TravelViewModel @Inject constructor() : BaseViewModel<TravelState, TravelIntent, TravelSideEffect>(
    initialState = TravelState()
) {
    override suspend fun handleIntent(intent: TravelIntent) {
        when (intent) {
            is TravelIntent.OnTravelClick -> {
                reduce { copy(displayText = "클릭된 id: ${intent.travelId}") }
                postSideEffect(TravelSideEffect.NavigateToDetail(intent.travelId))
            }
        }
    }
}