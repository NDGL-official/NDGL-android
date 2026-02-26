package com.yapp.ndgl.data.travel.repository

import com.yapp.ndgl.data.core.di.ExchangeRateApiKey
import com.yapp.ndgl.data.travel.api.ExchangeRateApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRateRepository @Inject constructor(
    private val exchangeRateApi: ExchangeRateApi,
    @ExchangeRateApiKey private val apiKey: String,
) {
    suspend fun getKrwRate(currencyCode: String): Double? {
        return exchangeRateApi.getLatestRate(apiKey, currencyCode)
            .conversionRates["KRW"]
    }

    suspend fun getSupportedCurrencyCodes(): List<String> {
        return exchangeRateApi.getLatestRate(apiKey, "USD").conversionRates.keys.toList()
    }
}
