package com.yapp.ndgl.feature.contentrecommendation

import androidx.lifecycle.viewModelScope
import com.yapp.ndgl.core.base.BaseViewModel
import com.yapp.ndgl.core.util.suspendRunCatching
import com.yapp.ndgl.data.travel.exception.DuplicateSuggestedTemplateException
import com.yapp.ndgl.data.travel.repository.ContentMetadataRepository
import com.yapp.ndgl.data.travel.repository.TravelTemplateRepository
import com.yapp.ndgl.feature.contentrecommendation.ContentRecommendationState.MetadataState
import com.yapp.ndgl.feature.contentrecommendation.model.TravelTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContentRecommendationViewModel @Inject constructor(
    private val contentMetadataRepository: ContentMetadataRepository,
    private val travelTemplateRepository: TravelTemplateRepository,
) : BaseViewModel<ContentRecommendationState, ContentRecommendationIntent, ContentRecommendationSideEffect>(
    initialState = ContentRecommendationState(),
) {

    override suspend fun handleIntent(intent: ContentRecommendationIntent) {
        when (intent) {
            is ContentRecommendationIntent.UpdateUrl -> updateUrl(intent.url)
            is ContentRecommendationIntent.ToggleTheme -> toggleTheme(intent.theme)
            is ContentRecommendationIntent.UpdateReason -> updateReason(intent.reason)
            is ContentRecommendationIntent.SubmitForm -> submitForm()
            is ContentRecommendationIntent.SubscribeNotification -> subscribeNotification()
            is ContentRecommendationIntent.NavigateBack -> postSideEffect(ContentRecommendationSideEffect.NavigateBack)
        }
    }

    private fun updateUrl(url: String) {
        when {
            url.isBlank() -> reduce {
                copy(contentUrl = url, metadataState = MetadataState.Empty, isSubmitEnabled = false)
            }

            !isYoutubeUrl(url) -> reduce {
                copy(contentUrl = url, metadataState = MetadataState.InvalidUrl, isSubmitEnabled = false)
            }

            else -> {
                reduce { copy(contentUrl = url, metadataState = MetadataState.Loading) }
                fetchMetadata(url)
            }
        }
    }

    private fun fetchMetadata(url: String) = viewModelScope.launch {
        suspendRunCatching {
            contentMetadataRepository.getMetadata(url)
        }
            .onSuccess { response ->
                reduce {
                    copy(
                        metadataState = MetadataState.Success(
                            title = response.title,
                            channelName = response.authorName,
                            thumbnailUrl = response.thumbnailUrl,
                        ),
                        isSubmitEnabled = selectedThemes.isNotEmpty(),
                    )
                }
            }
            .onFailure {
                reduce {
                    copy(
                        metadataState = MetadataState.Error,
                        isSubmitEnabled = selectedThemes.isNotEmpty(),
                    )
                }
            }
    }

    private fun toggleTheme(theme: TravelTheme) {
        reduce {
            val updated = if (selectedThemes.contains(theme)) {
                selectedThemes.remove(theme)
            } else {
                selectedThemes.add(theme)
            }
            copy(selectedThemes = updated, isSubmitEnabled = isYoutubeUrl(contentUrl) && updated.isNotEmpty())
        }
    }

    private fun isYoutubeUrl(url: String): Boolean =
        url.contains("youtube.com", ignoreCase = true) || url.contains("youtu.be", ignoreCase = true)

    private fun updateReason(reason: String) {
        if (reason.length <= REASON_MAX_LENGTH) {
            reduce { copy(reason = reason) }
        }
    }

    private fun submitForm() {
        val currentState = state.value
        if (!currentState.isSubmitEnabled) return

        viewModelScope.launch {
            suspendRunCatching {
                travelTemplateRepository.suggestTemplate(
                    videoLink = currentState.contentUrl,
                    recommendReason = currentState.reason,
                    category = currentState.selectedThemes.map { it.name },
                )
            }.onSuccess {
                postSideEffect(ContentRecommendationSideEffect.ShowSuccessModal)
            }.onFailure { e ->
                if (e is DuplicateSuggestedTemplateException) {
                    when (e.reason) {
                        DuplicateSuggestedTemplateException.Reason.ALREADY_PUBLISHED ->
                            postSideEffect(ContentRecommendationSideEffect.ShowSnackbar(ContentRecommendationSideEffect.SNACKBAR_ALREADY_PUBLISHED))

                        DuplicateSuggestedTemplateException.Reason.OTHER_USER_PENDING ->
                            postSideEffect(ContentRecommendationSideEffect.ShowDuplicateModal)

                        DuplicateSuggestedTemplateException.Reason.SELF_PENDING ->
                            postSideEffect(ContentRecommendationSideEffect.ShowSnackbar(ContentRecommendationSideEffect.SNACKBAR_SELF_PENDING))

                        DuplicateSuggestedTemplateException.Reason.ALREADY_SUBSCRIBED ->
                            postSideEffect(ContentRecommendationSideEffect.ShowSnackbar(ContentRecommendationSideEffect.SNACKBAR_ALREADY_SUBSCRIBED))
                    }
                } else {
                    postSideEffect(ContentRecommendationSideEffect.ShowSnackbar(ContentRecommendationSideEffect.SNACKBAR_SUBMIT_ERROR))
                }
            }
        }
    }

    private fun subscribeNotification() {
        val currentState = state.value

        viewModelScope.launch {
            suspendRunCatching {
                travelTemplateRepository.subscribeTemplate(videoLink = currentState.contentUrl)
            }.onSuccess {
                postSideEffect(ContentRecommendationSideEffect.NotifySubscribeSuccess)
            }.onFailure { e ->
                val message = if (e is DuplicateSuggestedTemplateException) {
                    ContentRecommendationSideEffect.SNACKBAR_ALREADY_SUBSCRIBED
                } else {
                    ContentRecommendationSideEffect.SNACKBAR_SUBSCRIBE_ERROR
                }
                postSideEffect(ContentRecommendationSideEffect.ShowSnackbar(message))
            }
        }
    }

    companion object {
        private const val REASON_MAX_LENGTH = 200
    }
}
