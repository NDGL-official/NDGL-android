package com.yapp.ndgl.data.travel.api

import com.yapp.ndgl.data.core.model.BaseResponse
import com.yapp.ndgl.data.travel.model.PopularTravelTemplates
import retrofit2.http.GET
import retrofit2.http.Query

interface TravelTemplateApi {
    @GET("/api/v1/travel-templates/popular")
    suspend fun getPopularTravelTemplates(
        @Query("travelProgramId") travelProgramId: Long? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): BaseResponse<PopularTravelTemplates>
}
