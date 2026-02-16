package com.yapp.ndgl.feature.travel.model

data class PriceRange(
    val startPrice: Price,
    val endPrice: Price,
) {
    val formattedPriceRange: String
        get() = "${startPrice.symbol}${startPrice.units}~${endPrice.units}"
}

data class Price(
    val currencyCode: String,
    val units: String,
    val symbol: String,
)
