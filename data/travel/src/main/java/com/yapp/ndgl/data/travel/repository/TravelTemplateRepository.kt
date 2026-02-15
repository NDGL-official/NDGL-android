package com.yapp.ndgl.data.travel.repository

import com.yapp.ndgl.data.core.model.getData
import com.yapp.ndgl.data.travel.api.TravelTemplateApi
import com.yapp.ndgl.data.travel.model.PopularTravelTemplates
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
}
