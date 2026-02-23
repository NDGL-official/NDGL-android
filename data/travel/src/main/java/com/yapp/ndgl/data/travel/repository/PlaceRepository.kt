package com.yapp.ndgl.data.travel.repository

import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.yapp.ndgl.data.core.model.error.HttpResponseException
import com.yapp.ndgl.data.core.model.getData
import com.yapp.ndgl.data.travel.api.PlaceApi
import com.yapp.ndgl.data.travel.model.GetPlacePhotosResponse
import com.yapp.ndgl.data.travel.model.PlaceDetailResponse
import com.yapp.ndgl.data.travel.model.SavePlaceRequest
import com.yapp.ndgl.data.travel.model.SearchKeywordResponse
import com.yapp.ndgl.data.travel.model.SearchResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceRepository @Inject constructor(
    private val placeApi: PlaceApi,
    private val placesClient: PlacesClient,
) {
    private var sessionToken: AutocompleteSessionToken? = null

    suspend fun searchKeyword(keyword: String, countryCode: String): SearchKeywordResponse {
        if (sessionToken == null) {
            sessionToken = AutocompleteSessionToken.newInstance()
        }

        val requestBuilder = FindAutocompletePredictionsRequest.builder()
            .setQuery(keyword)
            .setSessionToken(sessionToken)
            .setCountries(countryCode)

        val response = placesClient.findAutocompletePredictions(requestBuilder.build()).await()

        val results = response.autocompletePredictions.map { prediction ->
            SearchResult(
                googlePlaceId = prediction.placeId,
                name = prediction.getPrimaryText(null).toString(),
            )
        }

        return SearchKeywordResponse(results = results)
    }

    fun resetSessionToken() {
        sessionToken = null
    }

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
