package com.yapp.ndgl.data.travel.api

import com.yapp.ndgl.data.core.model.BaseResponse
import com.yapp.ndgl.data.travel.model.BulkUpdateStartTimeRequest
import com.yapp.ndgl.data.travel.model.UpcomingTravelList
import com.yapp.ndgl.data.travel.model.UpcomingTravelResponse
import com.yapp.ndgl.data.travel.model.UpdateItineraryRequest
import com.yapp.ndgl.data.travel.model.UserTravelTemplateContentInfo
import com.yapp.ndgl.data.travel.model.UserTravelTemplateItinerary
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserTravelApi {
    @GET("/api/v1/travels/upcoming")
    suspend fun getUpcomingTravel(): BaseResponse<UpcomingTravelResponse>

    @GET("/api/v1/travels/upcoming/list")
    suspend fun getUpcomingTravelList(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): BaseResponse<UpcomingTravelList>

    @GET("/api/v1/travels/{id}/itinerary")
    suspend fun getUserTravelTemplateItinerary(
        @Path("id") id: Long,
        @Query("day") day: Int,
    ): BaseResponse<UserTravelTemplateItinerary>

    @GET("/api/v1/travels/{id}/content-card")
    suspend fun getUserTravelTemplateContentInfo(
        @Path("id") id: Long,
    ): BaseResponse<UserTravelTemplateContentInfo>

    @PATCH("/api/v1/travels/{id}/start-time/bulk")
    suspend fun bulkUpdateStartTime(
        @Path("id") id: Long,
        @Body request: BulkUpdateStartTimeRequest,
    ): BaseResponse<Unit>

    @PUT("/api/v1/travels/{id}/itinerary")
    suspend fun updateItinerary(
        @Path("id") id: Long,
        @Body request: UpdateItineraryRequest,
    ): BaseResponse<Unit>
}
