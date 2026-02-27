package com.yapp.ndgl.feature.home.model

import androidx.compose.runtime.Immutable
import com.yapp.ndgl.data.travel.model.ProgramType

@Immutable
data class TravelContent(
    val travelId: Long,
    val title: String,
    val country: String,
    val countryName: String,
    val city: String,
    val nights: Int,
    val days: Int,
    val programName: String,
    val programType: ProgramType,
    val thumbnail: String,
)
