package com.yapp.ndgl.feature.travelhelper.util

import com.yapp.ndgl.data.travel.model.PlaceCategory
import com.yapp.ndgl.core.ui.R as CoreR

fun PlaceCategory.toDisplayNameRes() = when (this) {
    PlaceCategory.AIRPORT -> CoreR.string.place_type_airport
    PlaceCategory.TRANSPORT -> CoreR.string.place_type_transport
    PlaceCategory.ATTRACTION -> CoreR.string.place_type_attraction
    PlaceCategory.RESTAURANT -> CoreR.string.place_type_restaurant
    PlaceCategory.CAFE -> CoreR.string.place_type_cafe
    PlaceCategory.ACCOMMODATION -> CoreR.string.place_type_accommodation
}

fun PlaceCategory.toDrawableRes() = when (this) {
    PlaceCategory.AIRPORT -> CoreR.drawable.ic_14_airplane
    PlaceCategory.TRANSPORT -> CoreR.drawable.ic_14_car
    PlaceCategory.ATTRACTION -> CoreR.drawable.ic_14_flag
    PlaceCategory.RESTAURANT -> CoreR.drawable.ic_14_restaurant
    PlaceCategory.CAFE -> CoreR.drawable.ic_14_coffee
    PlaceCategory.ACCOMMODATION -> CoreR.drawable.ic_14_home
}
