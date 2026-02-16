package com.yapp.ndgl.feature.travel.model

import java.util.Locale.getDefault
import kotlin.time.Duration

data class TransportSegment(
    val type: TransportType,
    val duration: Duration,
    val distance: Int,
) {
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
}
