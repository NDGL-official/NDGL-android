package com.yapp.ndgl.data.travel.repository

import com.yapp.ndgl.data.core.model.error.HttpResponseException
import com.yapp.ndgl.data.core.model.getData
import com.yapp.ndgl.data.travel.api.TravelTemplateApi
import com.yapp.ndgl.data.travel.exception.DuplicateSuggestedTemplateException
import com.yapp.ndgl.data.travel.exception.DuplicateTravelPeriodException
import com.yapp.ndgl.data.travel.model.CreateTravelFromTemplateRequest
import com.yapp.ndgl.data.travel.model.CreateTravelFromTemplateResponse
import com.yapp.ndgl.data.travel.model.PopularTravelTemplates
import com.yapp.ndgl.data.travel.model.RecommendTravelTemplates
import com.yapp.ndgl.data.travel.model.SearchTravelTemplates
import com.yapp.ndgl.data.travel.model.SubscribeTemplateRequest
import com.yapp.ndgl.data.travel.model.SuggestTemplateRequest
import com.yapp.ndgl.data.travel.model.TravelTemplateContentInfo
import com.yapp.ndgl.data.travel.model.TravelTemplateItinerary
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TravelTemplateRepository @Inject constructor(
    private val travelTemplateApi: TravelTemplateApi,
) {
    suspend fun getAllPopularTravelTemplates(page: Int = 0): PopularTravelTemplates {
        return travelTemplateApi.getPopularTravelTemplates(page = page).getData()
    }

    suspend fun getPopularTravelTemplates(travelProgramId: Long, page: Int = 0): PopularTravelTemplates {
        return travelTemplateApi.getPopularTravelTemplates(
            travelProgramId = travelProgramId,
            page = page,
        ).getData()
    }

    suspend fun getRecommendTravelTemplates(): RecommendTravelTemplates {
        return travelTemplateApi.getRecommendTravelTemplates().getData()
    }

    suspend fun getTravelTemplateItinerary(travelId: Long, day: Int): TravelTemplateItinerary {
        return travelTemplateApi.getTravelTemplateItinerary(id = travelId, day = day).getData()
    }

    suspend fun getTravelTemplateContentInfo(travelId: Long): TravelTemplateContentInfo {
        return travelTemplateApi.getTravelTemplateContentInfo(id = travelId).getData()
    }

    suspend fun searchTravelTemplates(keyword: String): SearchTravelTemplates {
        return travelTemplateApi.searchTravelTemplates(keyword = keyword).getData()
    }

    suspend fun createTravelFromTemplate(
        templateId: Long,
        startDate: String,
        endDate: String,
    ): CreateTravelFromTemplateResponse {
        return try {
            travelTemplateApi.createTravelFromTemplate(
                request = CreateTravelFromTemplateRequest(
                    templateId = templateId,
                    startDate = startDate,
                    endDate = endDate,
                ),
            ).getData()
        } catch (e: HttpResponseException) {
            if (e.code == "TRAVEL-04-003") {
                throw DuplicateTravelPeriodException(e)
            }

            throw e
        }
    }

    suspend fun suggestTemplate(
        videoLink: String,
        recommendReason: String,
        category: List<String>,
    ) {
        try {
            travelTemplateApi.suggestTemplate(
                request = SuggestTemplateRequest(
                    videoLink = videoLink,
                    recommendReason = recommendReason,
                    category = category,
                ),
            )
        } catch (e: HttpResponseException) {
            throw when (e.code) {
                CODE_ALREADY_PUBLISHED_TEMPLATE, CODE_ALREADY_APPROVED ->
                    DuplicateSuggestedTemplateException(DuplicateSuggestedTemplateException.Reason.ALREADY_PUBLISHED, e)
                CODE_OTHER_USER_PENDING ->
                    DuplicateSuggestedTemplateException(DuplicateSuggestedTemplateException.Reason.OTHER_USER_PENDING, e)
                CODE_SELF_PENDING ->
                    DuplicateSuggestedTemplateException(DuplicateSuggestedTemplateException.Reason.SELF_PENDING, e)
                else -> e
            }
        }
    }

    suspend fun subscribeTemplate(videoLink: String) {
        try {
            travelTemplateApi.subscribeTemplate(
                request = SubscribeTemplateRequest(videoLink = videoLink),
            )
        } catch (e: HttpResponseException) {
            if (e.code == CODE_ALREADY_SUBSCRIBED) {
                throw DuplicateSuggestedTemplateException(
                    DuplicateSuggestedTemplateException.Reason.ALREADY_SUBSCRIBED,
                )
            }
            throw e
        }
    }

    companion object {
        private const val CODE_ALREADY_PUBLISHED_TEMPLATE = "TRAVEL-03-006"
        private const val CODE_ALREADY_APPROVED = "TRAVEL-03-002"
        private const val CODE_OTHER_USER_PENDING = "TRAVEL-03-003"
        private const val CODE_SELF_PENDING = "TRAVEL-03-004"
        private const val CODE_ALREADY_SUBSCRIBED = "TRAVEL-03-005"
    }
}
