package com.yapp.ndgl.data.travel.repository

import com.yapp.ndgl.data.core.model.getData
import com.yapp.ndgl.data.travel.api.TravelProgramApi
import com.yapp.ndgl.data.travel.model.TravelProgram
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TravelProgramRepository @Inject constructor(
    private val travelProgramApi: TravelProgramApi,
) {
    suspend fun getAllPrograms(): List<TravelProgram> {
        return travelProgramApi.getAllPrograms().getData()
    }
}
