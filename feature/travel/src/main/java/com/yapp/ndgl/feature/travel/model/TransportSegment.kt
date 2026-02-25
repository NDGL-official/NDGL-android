package com.yapp.ndgl.feature.travel.model

import com.yapp.ndgl.data.travel.model.TransportationItem
import java.util.Locale.getDefault
import kotlin.math.roundToInt
import kotlin.time.Duration

data class TransportSegment(
    val googlePlaceId: String,
    val type: TransportType,
    val duration: Duration, // m 단위
    val distance: Int,
) {
    val distanceKm: Double
        get() = (distance / 100.0).roundToInt() / 10.0

    fun formatDistance(): String {
        return when {
            distance >= 1000 -> {
                val km = distance / 1000.0
                if (km % 1 == 0.0) {
                    "${km.toInt()}km"
                } else {
                    String.format(getDefault(), "%.1fkm", km)
                }
            }

            else -> "${distance}m"
        }
    }

    fun toTransportationItem(): TransportationItem {
        return TransportationItem(
            mode = type.toTransportCategory(),
            timeMin = duration.inWholeMinutes.toInt().coerceAtLeast(1),
        )
    }
}
