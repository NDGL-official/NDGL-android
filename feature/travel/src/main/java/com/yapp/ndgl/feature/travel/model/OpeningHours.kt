package com.yapp.ndgl.feature.travel.model

data class OpeningHours(
    val periods: List<Period>,
    val isAlwaysOpen: Boolean = false,
) {
    data class Period(
        val open: String,
        val close: String,
    )
}

fun String?.toOpeningHours(): OpeningHours {
    if (this == null) return OpeningHours(periods = emptyList())
    if (contains("24시간")) return OpeningHours(periods = emptyList(), isAlwaysOpen = true)

    val periods = split(", ").mapNotNull { segment ->
        val times = segment.trim().split("~")
        if (times.size != 2) return@mapNotNull null
        OpeningHours.Period(
            open = times[0].trim(),
            close = times[1].trim(),
        )
    }

    return OpeningHours(periods = periods)
}
