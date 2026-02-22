package com.yapp.ndgl.feature.travelhelper.main

import androidx.lifecycle.viewModelScope
import com.yapp.ndgl.core.base.BaseViewModel
import com.yapp.ndgl.core.util.suspendRunCatching
import com.yapp.ndgl.data.travel.model.WeatherForecastResponse
import com.yapp.ndgl.data.travel.repository.UserTravelRepository
import com.yapp.ndgl.data.travel.repository.WeatherRepository
import com.yapp.ndgl.feature.travelhelper.main.TravelHelperState.ExchangeRateInfo
import com.yapp.ndgl.feature.travelhelper.main.TravelHelperState.TravelPlace
import com.yapp.ndgl.feature.travelhelper.main.TravelHelperState.TravelUiState
import com.yapp.ndgl.feature.travelhelper.main.TravelHelperState.WeatherForecastUiInfo
import com.yapp.ndgl.feature.travelhelper.main.TravelHelperState.WeatherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class TravelHelperViewModel @Inject constructor(
    private val userTravelRepository: UserTravelRepository,
    private val weatherRepository: WeatherRepository,
) : BaseViewModel<TravelHelperState, TravelHelperIntent, TravelHelperSideEffect>(
    initialState = TravelHelperState(),
) {
    init {
        loadUpcomingTravel()
    }

    private fun loadUpcomingTravel() {
        viewModelScope.launch {
            suspendRunCatching { userTravelRepository.getUpcomingTravel() }
                .onSuccess { travel ->
                    if (travel == null) {
                        reduce { copy(travelUiState = TravelUiState.NoTravel) }
                        return@onSuccess
                    }

                    val today = LocalDate.now()
                    val (currencyCode, currencyDisplayName) = countryToCurrency[travel.country]
                        ?: ("USD" to "달러")
                    val rate = krwRates[currencyCode] ?: 1340.0
                    val exchangeRateInfo = ExchangeRateInfo(
                        foreignCurrencyCode = currencyCode,
                        foreignCurrencyName = currencyDisplayName,
                        rateToKrw = rate,
                        rateDate = "2025-01-01",
                    )

                    val travelUiState = when {
                        today < travel.startDate -> {
                            val dDay = ChronoUnit.DAYS.between(today, travel.startDate)
                            TravelUiState.UpcomingTravel(
                                id = travel.userTravelId,
                                title = travel.title,
                                city = travel.city,
                                startDate = travel.startDate,
                                endDate = travel.endDate,
                                thumbnail = travel.thumbnail,
                                dDay = dDay,
                                weatherState = WeatherUiState.NotAvailable,
                                exchangeRateInfo = exchangeRateInfo,
                            )
                        }

                        today <= travel.endDate -> {
                            val dayNumber = ChronoUnit.DAYS.between(travel.startDate, today) + 1
                            val place = travel.upcomingUserTravelPlace
                            TravelUiState.OngoingTravel(
                                id = travel.userTravelId,
                                title = travel.title,
                                city = travel.city,
                                startDate = travel.startDate,
                                endDate = travel.endDate,
                                thumbnail = travel.thumbnail,
                                dayCount = dayNumber,
                                weatherState = WeatherUiState.NotAvailable,
                                currentPlace = place?.place?.let {
                                    TravelPlace(
                                        name = it.name,
                                        category = it.category,
                                        estimatedDuration = place.estimatedDuration,
                                        thumbnailUrl = it.thumbnail,
                                    )
                                },
                                exchangeRateInfo = exchangeRateInfo,
                            )
                        }

                        else -> TravelUiState.NoTravel
                    }

                    reduce {
                        copy(
                            travelUiState = travelUiState,
                            convertedAmount = calculateConvertedAmount(currencyInput, rate),
                        )
                    }

                    loadWeather(travel.city, travel.country, travel.startDate, travel.endDate)
                }
                .onFailure {
                    Timber.e("Failed to load upcoming travel: $it")
                    reduce { copy(travelUiState = TravelUiState.NoTravel) }
                }
        }
    }

    private fun loadWeather(
        city: String,
        country: String,
        startDate: LocalDate,
        endDate: LocalDate,
    ) {
        viewModelScope.launch {
            suspendRunCatching {
                weatherRepository.getWeatherForecast(
                    city,
                    country,
                    startDate,
                    endDate,
                )
            }
                .onSuccess { response ->
                    if (response == null) return@onSuccess
                    val forecasts = response.forecastDays
                        .map { it.toWeatherForecastUiInfo() }
                        .toImmutableList()
                    val weatherState = WeatherUiState.Available(forecasts)
                    reduce {
                        copy(
                            travelUiState = when (val ts = travelUiState) {
                                is TravelUiState.UpcomingTravel -> ts.copy(weatherState = weatherState)
                                is TravelUiState.OngoingTravel -> ts.copy(weatherState = weatherState)
                                else -> ts
                            },
                        )
                    }
                }
                .onFailure {
                    Timber.e("Failed to load weather: $it")
                }
        }
    }

    override suspend fun handleIntent(intent: TravelHelperIntent) {
        when (intent) {
            TravelHelperIntent.ClickSearch -> postSideEffect(TravelHelperSideEffect.NavigateToSearch)
            is TravelHelperIntent.UpdateCurrencyInput -> {
                val rate = when (val ts = state.value.travelUiState) {
                    is TravelUiState.UpcomingTravel -> ts.exchangeRateInfo.rateToKrw
                    is TravelUiState.OngoingTravel -> ts.exchangeRateInfo.rateToKrw
                    else -> 1.0
                }
                val converted = calculateConvertedAmount(intent.input, rate)
                reduce { copy(currencyInput = intent.input, convertedAmount = converted) }
            }

            TravelHelperIntent.SwapCurrency -> Unit
        }
    }

    private fun calculateConvertedAmount(input: String, rateToKrw: Double): Double? {
        val amount = input.toDoubleOrNull() ?: return null
        return amount * rateToKrw
    }

    private fun WeatherForecastResponse.ForecastDay.toWeatherForecastUiInfo(): WeatherForecastUiInfo {
        val date = LocalDate.of(displayDate.year, displayDate.month, displayDate.day)
        return WeatherForecastUiInfo(
            date = date,
            dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN),
            iconUrl = daytimeForecast?.weatherCondition?.iconBaseUri?.let { url -> "$url.png" },
            highTempCelsius = maxTemperature?.degrees?.roundToInt() ?: 0,
            lowTempCelsius = minTemperature?.degrees?.roundToInt() ?: 0,
        )
    }

    companion object {
        private val countryToCurrency = mapOf(
            "IT" to ("EUR" to "유로"),
            "FR" to ("EUR" to "유로"),
            "DE" to ("EUR" to "유로"),
            "ES" to ("EUR" to "유로"),
            "JP" to ("JPY" to "엔"),
            "US" to ("USD" to "달러"),
            "GB" to ("GBP" to "파운드"),
            "TH" to ("THB" to "밧"),
            "VN" to ("VND" to "동"),
            "SG" to ("SGD" to "달러"),
            "AU" to ("AUD" to "달러"),
            "CN" to ("CNY" to "위안"),
        )
        private val krwRates = mapOf(
            "EUR" to 1460.0,
            "JPY" to 9.5,
            "USD" to 1340.0,
            "GBP" to 1700.0,
            "THB" to 38.0,
            "VND" to 0.054,
            "SGD" to 1000.0,
            "AUD" to 870.0,
            "CNY" to 185.0,
        )
    }
}
