package com.yapp.ndgl.data.travel.repository

import com.yapp.ndgl.data.core.model.error.HttpResponseException
import com.yapp.ndgl.data.core.model.getData
import com.yapp.ndgl.data.travel.api.TravelTemplateApi
import com.yapp.ndgl.data.travel.exception.DuplicateTravelPeriodException
import com.yapp.ndgl.data.travel.model.CreateTravelFromTemplateRequest
import com.yapp.ndgl.data.travel.model.CreateTravelFromTemplateResponse
import com.yapp.ndgl.data.travel.model.PopularTravelTemplates
import com.yapp.ndgl.data.travel.model.RecommendTravelTemplates
import com.yapp.ndgl.data.travel.model.SearchTravelTemplates
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
}
