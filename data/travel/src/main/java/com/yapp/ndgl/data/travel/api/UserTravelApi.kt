package com.yapp.ndgl.data.travel.api

import com.yapp.ndgl.data.core.model.BaseResponse
import com.yapp.ndgl.data.travel.model.UpcomingTravelList
import com.yapp.ndgl.data.travel.model.UpcomingTravelResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UserTravelApi {
    @GET("/api/v1/travels/upcoming")
    suspend fun getUpcomingTravel(): BaseResponse<UpcomingTravelResponse>

    @GET("/api/v1/travels/upcoming/list")
    suspend fun getUpcomingTravelList(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): BaseResponse<UpcomingTravelList>
}
