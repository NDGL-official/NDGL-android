package com.yapp.ndgl.feature.travel.detail

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel(assistedFactory = TravelDetailViewModel.Factory::class)
class TravelDetailViewModel @AssistedInject constructor(
    @Assisted travelId: Int,
) : ViewModel() {

    private val _travelId = MutableStateFlow(travelId)
    val travelId: StateFlow<Int> = _travelId.asStateFlow()

    @AssistedFactory
    interface Factory {
        fun create(travelId: Int): TravelDetailViewModel
    }
}