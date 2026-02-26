package com.yapp.ndgl.data.travel.api

import com.yapp.ndgl.data.travel.model.ExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApi {
    @GET("v6/{apiKey}/latest/{base}")
    suspend fun getLatestRate(
        @Path("apiKey") apiKey: String,
        @Path("base") base: String,
    ): ExchangeRateResponse
}
