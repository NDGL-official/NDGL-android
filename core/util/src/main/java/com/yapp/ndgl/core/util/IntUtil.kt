package com.yapp.ndgl.core.util

import java.text.NumberFormat
import java.util.Locale
import java.util.Locale.getDefault

fun Int.formatDecimal(): String = NumberFormat.getInstance(Locale.US).format(this)

fun Int.formatDistance(): String {
    return when {
        this >= 1000 -> {
            val km = this / 1000.0
            if (km % 1 == 0.0) {
                "${km.toInt()}km"
            } else {
                String.format(getDefault(), "%.1fkm", km)
            }
        }

        else -> "${this}m"
    }
}
