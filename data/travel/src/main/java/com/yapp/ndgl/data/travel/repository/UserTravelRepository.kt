package com.yapp.ndgl.data.travel.repository

import com.yapp.ndgl.data.core.model.error.HttpResponseException
import com.yapp.ndgl.data.core.model.getData
import com.yapp.ndgl.data.travel.api.UserTravelApi
import com.yapp.ndgl.data.travel.model.UpcomingTravelList
import com.yapp.ndgl.data.travel.model.UpcomingTravelResponse
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserTravelRepository @Inject constructor(
    private val userTravelApi: UserTravelApi,
) {
    suspend fun getUpcomingTravel(): UpcomingTravelResponse? {
        return try {
            userTravelApi.getUpcomingTravel().getData()
        } catch (e: HttpResponseException) {
            if (e.code == HttpURLConnection.HTTP_NO_CONTENT.toString()) {
                null
            } else {
                throw e
            }
        }
    }

    suspend fun getUpcomingTravelList(): UpcomingTravelList {
        return userTravelApi.getUpcomingTravelList().getData()
    }
}
