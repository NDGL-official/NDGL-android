package com.yapp.ndgl.data.travel.model

import com.yapp.ndgl.data.core.serializer.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class UpcomingTravelList(
    val content: List<UpcomingTravel>,
    val hasNext: Boolean,
) {
    @Serializable
    data class UpcomingTravel(
        val id: Long,
        val title: String,
        val country: String,
        val city: String,
        @Serializable(with = LocalDateSerializer::class)
        val startDate: LocalDate,
        @Serializable(with = LocalDateSerializer::class)
        val endDate: LocalDate,
        val nights: Int,
        val days: Int,
        val templateId: Long,
        val thumbnail: String? = null,
        val profileImage: String? = null,
    )
}
