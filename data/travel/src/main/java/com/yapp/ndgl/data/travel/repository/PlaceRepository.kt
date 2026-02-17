package com.yapp.ndgl.data.travel.repository

import com.yapp.ndgl.data.core.model.error.HttpResponseException
import com.yapp.ndgl.data.core.model.getData
import com.yapp.ndgl.data.travel.api.PlaceApi
import com.yapp.ndgl.data.travel.model.GetPlacePhotosResponse
import com.yapp.ndgl.data.travel.model.PlaceDetailResponse
import com.yapp.ndgl.data.travel.model.SavePlaceRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceRepository @Inject constructor(
    private val placeApi: PlaceApi,
) {
    suspend fun getPlace(googlePlaceId: String): PlaceDetailResponse {
        return try {
            placeApi.getPlaceDetail(googlePlaceId).getData()
        } catch (e: HttpResponseException) {
            if (e.code == "PLACE-02-001") {
                placeApi.savePlace(SavePlaceRequest(googlePlaceId)).getData()
            } else {
                throw e
            }
        }
    }

    suspend fun getPlacePhotos(googlePlaceId: String): GetPlacePhotosResponse {
        return placeApi.getPlacePhotos(googlePlaceId).getData()
    }
}
