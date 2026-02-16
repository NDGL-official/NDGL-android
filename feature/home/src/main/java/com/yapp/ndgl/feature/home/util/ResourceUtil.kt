package com.yapp.ndgl.feature.home.util

import com.yapp.ndgl.data.travel.model.PlaceCategory
import com.yapp.ndgl.data.travel.model.ProgramType
import com.yapp.ndgl.core.ui.R as CoreR

fun PlaceCategory.toDisplayRes() = when (this) {
    PlaceCategory.AIRPORT -> CoreR.string.place_type_airport
    PlaceCategory.TRANSPORT -> CoreR.string.place_type_transport
    PlaceCategory.ATTRACTION -> CoreR.string.place_type_attraction
    PlaceCategory.RESTAURANT -> CoreR.string.place_type_restaurant
    PlaceCategory.CAFE -> CoreR.string.place_type_cafe
    PlaceCategory.ACCOMMODATION -> CoreR.string.place_type_accommodation
}

fun ProgramType.toIconRes() = when (this) {
    ProgramType.YOUTUBE -> CoreR.drawable.ic_20_video
    ProgramType.TV -> CoreR.drawable.ic_20_tv
}
