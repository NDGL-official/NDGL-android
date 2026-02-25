package com.yapp.ndgl.data.travel.util

import java.util.Currency
import java.util.Locale

object CurrencyInfoResolver {
    private val countryCodeOverrides = mapOf("EUR" to "EU")

    fun getCountryCode(currencyCode: String): String? {
        countryCodeOverrides[currencyCode]?.let { return it }
        return Locale.getAvailableLocales()
            .firstOrNull { locale ->
                locale.country.isNotEmpty() &&
                    runCatching { Currency.getInstance(locale)?.currencyCode == currencyCode }.getOrDefault(false)
            }?.country
    }

    fun getCountryName(currencyCode: String): String {
        val code = getCountryCode(currencyCode) ?: return currencyCode
        return Locale("", code).getDisplayCountry(Locale.KOREAN)
    }

    fun getCurrencyCode(countryCode: String): String {
        return runCatching {
            Currency.getInstance(Locale("", countryCode)).currencyCode
        }.getOrDefault("USD")
    }

    fun getKoreanName(currencyCode: String): String {
        return runCatching {
            Currency.getInstance(currencyCode).getDisplayName(Locale.KOREAN)
        }.getOrDefault(currencyCode)
    }
}
