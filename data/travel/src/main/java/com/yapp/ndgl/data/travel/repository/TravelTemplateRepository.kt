package com.yapp.ndgl.data.travel.repository

import com.yapp.ndgl.data.core.model.getData
import com.yapp.ndgl.data.travel.api.TravelTemplateApi
import com.yapp.ndgl.data.travel.model.PopularTravelTemplates
import com.yapp.ndgl.data.travel.model.RecommendTravelTemplates
import com.yapp.ndgl.data.travel.model.TravelTemplateContentInfo
import com.yapp.ndgl.data.travel.model.TravelTemplateItinerary
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TravelTemplateRepository @Inject constructor(
    private val travelTemplateApi: TravelTemplateApi,
) {
    suspend fun getAllPopularTravelTemplates(): PopularTravelTemplates {
        return travelTemplateApi.getPopularTravelTemplates().getData()
    }

    suspend fun getPopularTravelTemplates(travelProgramId: Long): PopularTravelTemplates {
        return travelTemplateApi.getPopularTravelTemplates(travelProgramId = travelProgramId).getData()
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
}
