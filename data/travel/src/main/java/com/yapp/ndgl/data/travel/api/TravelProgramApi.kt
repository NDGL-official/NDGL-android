package com.yapp.ndgl.data.travel.api

import com.yapp.ndgl.data.core.model.BaseResponse
import com.yapp.ndgl.data.travel.model.TravelProgram
import retrofit2.http.GET

interface TravelProgramApi {
    @GET("/api/v1/travel-programs")
    suspend fun getAllPrograms(): BaseResponse<List<TravelProgram>>
}
