package com.yapp.ndgl.feature.travel.model

import com.yapp.ndgl.data.travel.model.TransportCategory
import com.yapp.ndgl.data.travel.model.TravelMode

internal fun TravelMode.toTransportType(): TransportType = when (this) {
    TravelMode.WALK -> TransportType.WALKING
    TravelMode.DRIVE -> TransportType.DRIVING
    TravelMode.TRANSIT -> TransportType.TRANSIT
    TravelMode.BICYCLE -> TransportType.BICYCLING
    TravelMode.TWO_WHEELER -> TransportType.TWO_WHEELER
}

internal fun TransportType.toTravelMode(): TravelMode? = when (this) {
    TransportType.WALKING -> TravelMode.WALK
    TransportType.DRIVING -> TravelMode.DRIVE
    TransportType.TRANSIT -> TravelMode.TRANSIT
    TransportType.BICYCLING -> TravelMode.BICYCLE
    TransportType.TWO_WHEELER -> TravelMode.TWO_WHEELER
    TransportType.TAXI -> null // TAXI는 TravelMode에 없음
    TransportType.FERRY -> null // FERRY는 TravelMode에 없음
    TransportType.FLIGHT -> null // FLIGHT는 TravelMode에 없음
}

internal fun TransportType.toTransportCategory(): TransportCategory = when (this) {
    TransportType.WALKING -> TransportCategory.WALKING
    TransportType.DRIVING -> TransportCategory.DRIVING
    TransportType.TRANSIT -> TransportCategory.TRANSIT
    TransportType.BICYCLING -> TransportCategory.BICYCLING
    TransportType.TAXI -> TransportCategory.TAXI
    TransportType.TWO_WHEELER -> TransportCategory.TWO_WHEELER
    TransportType.FERRY -> TransportCategory.FERRY
    TransportType.FLIGHT -> TransportCategory.FLIGHT
}
