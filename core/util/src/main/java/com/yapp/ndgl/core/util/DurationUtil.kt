package com.yapp.ndgl.core.util

import java.util.Locale.getDefault
import kotlin.time.Duration

fun Duration.formatString(): String {
    return toComponents { hours, minutes, _, _ ->
        when {
            hours > 0 && minutes > 0 -> "${hours}시간 ${minutes}분"
            hours > 0 -> "${hours}시간"
            else -> "${inWholeMinutes}분"
        }
    }
}

fun Duration.toTimeString(): String {
    val hours = this.inWholeHours.toInt()
    val minutes = (this.inWholeMinutes % 60).toInt()
    return String.format(getDefault(), "%02d:%02d", hours, minutes)
}

fun Duration.toAmPmTimeString(): String {
    val hours = this.inWholeHours.toInt()
    val minutes = (this.inWholeMinutes % 60).toInt()
    val amPm = if (hours < 12) "오전" else "오후"
    val displayHour = when {
        hours == 0 -> 12
        hours > 12 -> hours - 12
        else -> hours
    }
    return if (minutes == 0) {
        "$amPm $displayHour:00"
    } else {
        "$amPm $displayHour:${String.format(getDefault(), "%02d", minutes)}"
    }
}
