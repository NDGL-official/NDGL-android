package com.yapp.ndgl.data.travel.api

import com.yapp.ndgl.data.travel.model.ComputeRoutesRequest
import com.yapp.ndgl.data.travel.model.ComputeRoutesResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface RouteApi {
    @POST("directions/v2:computeRoutes")
    suspend fun computeRoutes(
        @Body request: ComputeRoutesRequest,
    ): ComputeRoutesResponse
}
