package com.yapp.ndgl.feature.home.model

import com.yapp.ndgl.data.travel.model.ProgramType

sealed interface TravelProgramTab {
    data object All : TravelProgramTab

    data class Custom(
        val programId: Long,
        val name: String,
        val type: ProgramType,
    ) : TravelProgramTab
}
