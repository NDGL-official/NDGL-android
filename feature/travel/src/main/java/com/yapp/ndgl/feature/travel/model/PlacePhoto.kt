package com.yapp.ndgl.feature.travel.model

data class PlacePhoto(
    val url: String,
    val width: Int,
    val height: Int,
) {
    val aspectRatio: Float
        get() = width.toFloat() / height.toFloat()
}
