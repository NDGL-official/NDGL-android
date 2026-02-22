@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package com.yapp.ndgl.data.travel.model

import com.yapp.ndgl.data.core.serializer.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class UpcomingTravelResponse(
    val userTravelId: Long,
    val title: String,
    val country: String,
    val city: String,
    @Serializable(with = LocalDateSerializer::class)
    val startDate: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    val endDate: LocalDate,
    val nights: Int,
    val days: Int,
    val thumbnail: String?,
    val upcomingUserTravelPlace: UserTravelPlace? = null,
)
