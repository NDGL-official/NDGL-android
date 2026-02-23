package com.yapp.ndgl.feature.travel.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.data.travel.model.PlaceCategory

enum class PlaceType(@get:StringRes val labelRes: Int, @get:DrawableRes val iconRes: Int) {
    ACCOMMODATION(R.string.place_type_accommodation, R.drawable.ic_14_home),
    RESTAURANT(R.string.place_type_restaurant, R.drawable.ic_14_restaurant),
    ATTRACTION(R.string.place_type_attraction, R.drawable.ic_14_flag),
    CAFE(R.string.place_type_cafe, R.drawable.ic_14_coffee),
    AIRPORT(R.string.place_type_airport, R.drawable.ic_14_airplane),
    TRANSPORT(R.string.place_type_transport, R.drawable.ic_14_car),
}

@Composable
fun PlaceType.getColor(): androidx.compose.ui.graphics.Color {
    return when (this) {
        PlaceType.ACCOMMODATION -> NDGLTheme.colors.etcPurple
        PlaceType.RESTAURANT -> NDGLTheme.colors.etcOrange
        PlaceType.ATTRACTION -> NDGLTheme.colors.etcGreen
        PlaceType.CAFE -> NDGLTheme.colors.etcOrange
        PlaceType.AIRPORT -> NDGLTheme.colors.etcGray
        PlaceType.TRANSPORT -> NDGLTheme.colors.etcGray
    }
}

internal fun PlaceCategory.toPlaceType(): PlaceType = when (this) {
    PlaceCategory.AIRPORT -> PlaceType.AIRPORT
    PlaceCategory.TRANSPORT -> PlaceType.TRANSPORT
    PlaceCategory.ATTRACTION -> PlaceType.ATTRACTION
    PlaceCategory.RESTAURANT -> PlaceType.RESTAURANT
    PlaceCategory.CAFE -> PlaceType.CAFE
    PlaceCategory.ACCOMMODATION -> PlaceType.ACCOMMODATION
}

internal fun PlaceType.toPlaceCategory(): PlaceCategory = when (this) {
    PlaceType.ACCOMMODATION -> PlaceCategory.ACCOMMODATION
    PlaceType.RESTAURANT -> PlaceCategory.RESTAURANT
    PlaceType.ATTRACTION -> PlaceCategory.ATTRACTION
    PlaceType.CAFE -> PlaceCategory.CAFE
    PlaceType.AIRPORT -> PlaceCategory.AIRPORT
    PlaceType.TRANSPORT -> PlaceCategory.TRANSPORT
}

fun String.toPlaceType(): PlaceType = PlaceType.entries.find { it.name == this } ?: PlaceType.ATTRACTION
