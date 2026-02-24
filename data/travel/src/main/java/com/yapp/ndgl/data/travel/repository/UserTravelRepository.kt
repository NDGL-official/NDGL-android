package com.yapp.ndgl.data.travel.repository

import com.yapp.ndgl.data.core.model.error.HttpResponseException
import com.yapp.ndgl.data.core.model.getData
import com.yapp.ndgl.data.travel.api.UserTravelApi
import com.yapp.ndgl.data.travel.model.AddItineraryRequest
import com.yapp.ndgl.data.travel.model.AddPlaceEvent
import com.yapp.ndgl.data.travel.model.BulkUpdateStartTimeRequest
import com.yapp.ndgl.data.travel.model.ItineraryUpdateItem
import com.yapp.ndgl.data.travel.model.StartTimeUpdateItem
import com.yapp.ndgl.data.travel.model.TravelCreatedEvent
import com.yapp.ndgl.data.travel.model.UpcomingTravelList
import com.yapp.ndgl.data.travel.model.UpcomingTravelResponse
import com.yapp.ndgl.data.travel.model.UpdateItineraryRequest
import com.yapp.ndgl.data.travel.model.UpdateTravelPlaceRequest
import com.yapp.ndgl.data.travel.model.UserTravelTemplateContentInfo
import com.yapp.ndgl.data.travel.model.UserTravelTemplateItinerary
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserTravelRepository @Inject constructor(
    private val userTravelApi: UserTravelApi,
) {
    private val _addPlaceEvent = MutableSharedFlow<AddPlaceEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val addPlaceEvent: SharedFlow<AddPlaceEvent> = _addPlaceEvent.asSharedFlow()

    private val _travelCreatedEvent = MutableSharedFlow<TravelCreatedEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val travelCreatedEvent: SharedFlow<TravelCreatedEvent> = _travelCreatedEvent.asSharedFlow()

    suspend fun emitAddPlaceEvent(event: AddPlaceEvent) {
        _addPlaceEvent.emit(event)
    }

    suspend fun emitTravelCreatedEvent(event: TravelCreatedEvent) {
        _travelCreatedEvent.emit(event)
    }

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

    suspend fun getUserTravelTemplateItinerary(
        travelId: Long,
        day: Int,
    ): UserTravelTemplateItinerary {
        return userTravelApi.getUserTravelTemplateItinerary(
            id = travelId,
            day = day,
        ).getData()
    }

    suspend fun getUserTravelTemplateContentInfo(
        travelId: Long,
    ): UserTravelTemplateContentInfo {
        return userTravelApi.getUserTravelTemplateContentInfo(
            id = travelId,
        ).getData()
    }

    suspend fun bulkUpdateStartTime(
        travelId: Long,
        updates: List<StartTimeUpdateItem>,
    ) {
        userTravelApi.bulkUpdateStartTime(
            id = travelId,
            request = BulkUpdateStartTimeRequest(updates = updates),
        ).getData()
    }

    suspend fun updateItinerary(
        travelId: Long,
        itineraries: List<ItineraryUpdateItem>,
    ) {
        userTravelApi.updateItinerary(
            id = travelId,
            request = UpdateItineraryRequest(itineraries = itineraries),
        ).getData()
    }

    suspend fun updateTravelPlace(
        travelId: Long,
        userTravelPlaceId: Long,
        memo: String? = null,
        cost: Int? = null,
    ) {
        userTravelApi.updateTravelPlace(
            id = travelId,
            userTravelPlaceId = userTravelPlaceId,
            request = UpdateTravelPlaceRequest(
                memo = memo,
                cost = cost,
            ),
        ).getData()
    }

    suspend fun addItinerary(
        travelId: Long,
        googlePlaceId: String,
        day: Int,
        sequence: Int,
        startTime: String? = null,
        estimatedDuration: String? = null,
        cost: Int? = null,
        memo: String? = null,
    ) {
        userTravelApi.addItinerary(
            id = travelId,
            request = AddItineraryRequest(
                googlePlaceId = googlePlaceId,
                day = day,
                sequence = sequence,
                startTime = startTime,
                estimatedDuration = estimatedDuration,
                cost = cost,
                memo = memo,
            ),
        ).getData()
    }
}
