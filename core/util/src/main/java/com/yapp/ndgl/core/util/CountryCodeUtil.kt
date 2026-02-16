package com.yapp.ndgl.core.util

import java.util.Locale

private val countryCodeRegex = Regex("^[A-Za-z]{2}$")

fun String.toCountryName(): String {
    if (!matches(countryCodeRegex)) return ""
    val locale = Locale.Builder().setRegion(this).build()
    return locale.displayCountry
}
