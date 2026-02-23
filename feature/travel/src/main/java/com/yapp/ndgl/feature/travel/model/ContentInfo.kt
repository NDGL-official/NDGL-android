package com.yapp.ndgl.feature.travel.model

data class ContentInfo(
    val travelId: Long = 0,
    val countryCode: String = "",
    val country: String? = null,
    val city: String = "",
    val budgetPerPerson: Budget = Budget(0),
    val nights: Int = 0,
    val days: Int = 0,
    val videoInfo: VideoInfo = VideoInfo(),
)

data class VideoInfo(
    val title: String = "",
    val creatorName: String = "",
    val profileImage: String? = null,
    val thumbnail: String? = null,
    val link: String? = null,
    val summary: String = "",
)

data class Budget(
    val amount: Int,
) {
    fun formatString(): String {
        return when {
            amount < 10000 -> "만원"
            amount % 10000 == 0 -> "${amount / 10000}만원"
            amount % 1000 == 0 -> "${amount / 10000}만 ${(amount % 10000) / 1000}천원"
            else -> "${amount}원"
        }
    }
}
