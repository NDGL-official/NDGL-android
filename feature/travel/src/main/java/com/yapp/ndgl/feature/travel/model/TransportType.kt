package com.yapp.ndgl.feature.travel.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.data.travel.model.TransportCategory

enum class TransportType(@get:StringRes val labelRes: Int, @get:DrawableRes val iconRes: Int) {
    WALKING(R.string.transport_type_walking, R.drawable.ic_24_walk),
    DRIVING(R.string.transport_type_driving, R.drawable.ic_24_car),
    TRANSIT(R.string.transport_type_transit, R.drawable.ic_24_bus),
    BICYCLING(R.string.transport_type_bicycling, R.drawable.ic_24_bicycle),
    TAXI(R.string.transport_type_taxi, R.drawable.ic_24_taxi),
    TWO_WHEELER(R.string.transport_type_two_wheeler, R.drawable.ic_24_two_wheeler),
    FERRY(R.string.transport_type_ferry, R.drawable.ic_24_ferry),
    FLIGHT(R.string.transport_type_flight, R.drawable.ic_24_airplane),
}

internal fun TransportCategory.toTransportType(): TransportType = when (this) {
    TransportCategory.WALKING -> TransportType.WALKING
    TransportCategory.DRIVING -> TransportType.DRIVING
    TransportCategory.TRANSIT -> TransportType.TRANSIT
    TransportCategory.BICYCLING -> TransportType.BICYCLING
    TransportCategory.TAXI -> TransportType.TAXI
    TransportCategory.TWO_WHEELER -> TransportType.TWO_WHEELER
    TransportCategory.FERRY -> TransportType.FERRY
    TransportCategory.FLIGHT -> TransportType.FLIGHT
}
