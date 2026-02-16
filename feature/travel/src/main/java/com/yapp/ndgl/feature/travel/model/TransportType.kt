package com.yapp.ndgl.feature.travel.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.yapp.ndgl.core.ui.R

enum class TransportType(@get:StringRes val labelRes: Int, @get:DrawableRes val iconRes: Int) {
    WALK(R.string.transport_type_walk, R.drawable.ic_20_walk),
    CAR(R.string.transport_type_car, R.drawable.ic_20_car),
    BUS(R.string.transport_type_bus, R.drawable.ic_20_bus),
    TRAIN(R.string.transport_type_train, R.drawable.ic_20_train),
}
