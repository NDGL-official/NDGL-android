package com.yapp.ndgl.data.travel.api

import com.yapp.ndgl.data.core.model.BaseResponse
import com.yapp.ndgl.data.travel.model.GetPlacePhotosResponse
import com.yapp.ndgl.data.travel.model.PlaceDetailResponse
import com.yapp.ndgl.data.travel.model.SavePlaceRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PlaceApi {
    @GET("/api/v1/places/detail")
    suspend fun getPlaceDetail(
        @Query("googlePlaceId") googlePlaceId: String,
    ): BaseResponse<PlaceDetailResponse>

    @POST("/api/v1/places")
    suspend fun savePlace(
        @Body request: SavePlaceRequest,
    ): BaseResponse<PlaceDetailResponse>

    @GET("/api/v1/places/photos")
    suspend fun getPlacePhotos(
        @Query("googlePlaceId") googlePlaceId: String,
    ): BaseResponse<GetPlacePhotosResponse>
}
