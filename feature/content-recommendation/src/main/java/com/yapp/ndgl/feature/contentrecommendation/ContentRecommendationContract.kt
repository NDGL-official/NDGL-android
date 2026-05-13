package com.yapp.ndgl.feature.contentrecommendation

import androidx.compose.runtime.Stable
import com.yapp.ndgl.core.base.UiIntent
import com.yapp.ndgl.core.base.UiSideEffect
import com.yapp.ndgl.core.base.UiState
import com.yapp.ndgl.feature.contentrecommendation.model.TravelTheme
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf

@Stable
data class ContentRecommendationState(
    val contentUrl: String = "",
    val metadataState: MetadataState = MetadataState.Empty,
    val selectedThemes: PersistentSet<TravelTheme> = persistentSetOf(),
    val reason: String = "",
    val isSubmitEnabled: Boolean = false,
) : UiState {
    sealed interface MetadataState {
        data object Empty : MetadataState
        data object Loading : MetadataState

        data class Success(
            val title: String,
            val channelName: String,
            val thumbnailUrl: String,
        ) : MetadataState

        data object Error : MetadataState
        data object InvalidUrl : MetadataState
    }
}

sealed interface ContentRecommendationIntent : UiIntent {
    data class UpdateUrl(val url: String) : ContentRecommendationIntent
    data class ToggleTheme(val theme: TravelTheme) : ContentRecommendationIntent
    data class UpdateReason(val reason: String) : ContentRecommendationIntent
    data object Submit : ContentRecommendationIntent
    data object NavigateBack : ContentRecommendationIntent
}

sealed interface ContentRecommendationSideEffect : UiSideEffect {
    data object NavigateBack : ContentRecommendationSideEffect
    data object ShowSubmitSuccess : ContentRecommendationSideEffect
    data object ShowSubmitError : ContentRecommendationSideEffect
}
