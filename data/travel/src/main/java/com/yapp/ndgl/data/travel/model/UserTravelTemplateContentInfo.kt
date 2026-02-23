package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserTravelTemplateContentInfo(
    val userTravelId: Long,
    val templateId: Long,
    val budgetPerPerson: Int? = null,
    @SerialName("country")
    val countryCode: String,
    val countryName: String? = null,
    val city: String,
    val nights: Int,
    val days: Int,
    val startDate: String,
    val endDate: String,
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
