package com.yapp.ndgl.feature.travel.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.data.travel.model.TransportCategory

enum class TransportType(@get:StringRes val labelRes: Int, @get:DrawableRes val iconRes: Int) {
    WALK(R.string.transport_type_walk, R.drawable.ic_20_walk),
    CAR(R.string.transport_type_car, R.drawable.ic_20_car),
    BUS(R.string.transport_type_bus, R.drawable.ic_20_bus),
    TRAIN(R.string.transport_type_train, R.drawable.ic_20_train),
}

internal fun TransportCategory.toTransportType(): TransportType = when (this) {
    TransportCategory.WALKING -> TransportType.WALK
    else -> TransportType.CAR // FIXME: 교통수단 변경
}
