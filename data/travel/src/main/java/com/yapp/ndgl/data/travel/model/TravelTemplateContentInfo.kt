package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TravelTemplateContentInfo(
    val travelId: Long,
    @SerialName("country")
    val countryCode: String,
    val countryName: String? = null,
    val city: String,
    val budgetPerPerson: Int? = null,
    val nights: Int,
    val days: Int,
    val program: ProgramInfo,
) {
    @Serializable
    data class ProgramInfo(
        val title: String,
        @SerialName("name")
        val creatorName: String,
        val thumbnail: String? = null,
        val profileImage: String? = null,
        val link: String? = null,
        val summary: String,
    )
}
