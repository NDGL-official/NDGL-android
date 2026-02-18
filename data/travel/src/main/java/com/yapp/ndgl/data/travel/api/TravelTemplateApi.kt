package com.yapp.ndgl.data.travel.api

import com.yapp.ndgl.data.core.model.BaseResponse
import com.yapp.ndgl.data.travel.model.PopularTravelTemplates
import com.yapp.ndgl.data.travel.model.RecommendTravelTemplates
import com.yapp.ndgl.data.travel.model.SearchTravelTemplates
import com.yapp.ndgl.data.travel.model.TravelTemplateContentInfo
import com.yapp.ndgl.data.travel.model.TravelTemplateItinerary
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TravelTemplateApi {
    @GET("/api/v1/travel-templates/popular")
    suspend fun getPopularTravelTemplates(
        @Query("travelProgramId") travelProgramId: Long? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): BaseResponse<PopularTravelTemplates>

    @GET("/api/v1/travel-templates/recommend")
    suspend fun getRecommendTravelTemplates(): BaseResponse<RecommendTravelTemplates>

    @GET("/api/v1/travel-templates/{id}/itinerary")
    suspend fun getTravelTemplateItinerary(
        @Path("id") id: Long,
        @Query("day") day: Int? = null,
    ): BaseResponse<TravelTemplateItinerary>

    @GET("/api/v1/travel-templates/{id}/content-card")
    suspend fun getTravelTemplateContentInfo(
        @Path("id") id: Long,
    ): BaseResponse<TravelTemplateContentInfo>

    @GET("/api/v1/travel-templates/search")
    suspend fun searchTravelTemplates(
        @Query("keyword") keyword: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): BaseResponse<SearchTravelTemplates>
}
