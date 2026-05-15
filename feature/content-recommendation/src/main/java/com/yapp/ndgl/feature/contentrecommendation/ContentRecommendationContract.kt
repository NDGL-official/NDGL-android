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
    data object SubmitForm : ContentRecommendationIntent
    data object SubscribeNotification : ContentRecommendationIntent
    data object NavigateBack : ContentRecommendationIntent
}

sealed interface ContentRecommendationSideEffect : UiSideEffect {
    data object NavigateBack : ContentRecommendationSideEffect
    data object ShowSuccessModal : ContentRecommendationSideEffect
    data object ShowDuplicateModal : ContentRecommendationSideEffect
    data class ShowSnackbar(val message: String) : ContentRecommendationSideEffect
    data object NotifySubscribeSuccess : ContentRecommendationSideEffect

    companion object {
        const val SNACKBAR_SUBMIT_ERROR = "추천 전송에 실패했어요. 다시 시도해주세요"
        const val SNACKBAR_SUBSCRIBE_ERROR = "알림 신청에 실패했어요. 다시 시도해주세요"
        const val SNACKBAR_ALREADY_PUBLISHED = "이미 게시된 영상이에요."
        const val SNACKBAR_SELF_PENDING = "이미 추천 신청한 영상이에요."
        const val SNACKBAR_ALREADY_SUBSCRIBED = "이미 알림 신청한 영상이에요."
    }
}
