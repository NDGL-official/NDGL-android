package com.yapp.ndgl.data.travel.api

import com.yapp.ndgl.data.core.model.BaseResponse
import com.yapp.ndgl.data.travel.model.UpcomingTravelResponse
import retrofit2.http.GET

interface UserTravelApi {
    @GET("/api/v1/travels/upcoming")
    suspend fun getUpcomingTravel(): BaseResponse<UpcomingTravelResponse>
}
