package com.yapp.ndgl.feature.travel.travel

import com.yapp.ndgl.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TravelViewModel @Inject constructor() : BaseViewModel<TravelState, TravelIntent, TravelSideEffect>(
    initialState = TravelState(),
) {
    override suspend fun handleIntent(intent: TravelIntent) {
        when (intent) {
            is TravelIntent.ClickTravel -> {
                clickTravel(intent.travelId)
            }
            is TravelIntent.ClickTravelDetail -> {
                clickTravelDetail(intent.travelId)
            }
        }
    }

    // FIXME: Home 화면에서 처리
    private fun clickTravel(travelId: Int) {
        reduce { copy(displayText = "클릭된 id: $travelId") }
        postSideEffect(TravelSideEffect.NavigateToFollowTravel(2, 1))
    }

    // FIXME: Home 화면에서 처리
    private fun clickTravelDetail(travelId: Int) {
        reduce { copy(displayText = "Travel Detail 클릭된 id: $travelId") }
        postSideEffect(TravelSideEffect.NavigateToTravelDetail(travelId))
    }
}
