package com.yapp.ndgl.core.util

import java.util.Locale.getDefault
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

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

fun parseTimeStringToDuration(timeString: String?): Duration? {
    if (timeString.isNullOrBlank()) return null

    val regex = """^(\d+):([0-5]?\d):([0-5]?\d)$""".toRegex()
    val matchResult = regex.find(timeString) ?: return null

    val (hours, minutes) = matchResult.destructured

    return hours.toInt().hours + minutes.toInt().minutes
}

fun Duration.parseDurationToTimeString(): String {
    val totalMinutes = this.inWholeMinutes
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return String.format(getDefault(), "%02d:%02d:00", hours, minutes)
}
