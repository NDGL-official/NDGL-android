package com.yapp.ndgl.feature.travelhelper.main

import androidx.lifecycle.viewModelScope
import com.yapp.ndgl.core.base.BaseViewModel
import com.yapp.ndgl.core.util.FlagEmojiUtil.toFlagEmoji
import com.yapp.ndgl.core.util.suspendRunCatching
import com.yapp.ndgl.data.travel.model.WeatherForecastResponse
import com.yapp.ndgl.data.travel.repository.ExchangeRateRepository
import com.yapp.ndgl.data.travel.repository.UserTravelRepository
import com.yapp.ndgl.data.travel.repository.WeatherRepository
import com.yapp.ndgl.data.travel.util.CurrencyInfoResolver
import com.yapp.ndgl.feature.travelhelper.main.TravelHelperSideEffect.NavigateToTravelDetail
import com.yapp.ndgl.feature.travelhelper.main.TravelHelperState.CurrencyInfo
import com.yapp.ndgl.feature.travelhelper.main.TravelHelperState.ExchangeRateInfo
import com.yapp.ndgl.feature.travelhelper.main.TravelHelperState.TravelPlace
import com.yapp.ndgl.feature.travelhelper.main.TravelHelperState.TravelUiState
import com.yapp.ndgl.feature.travelhelper.main.TravelHelperState.WeatherForecastUiInfo
import com.yapp.ndgl.feature.travelhelper.main.TravelHelperState.WeatherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
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
    private val exchangeRateRepository: ExchangeRateRepository,
) : BaseViewModel<TravelHelperState, TravelHelperIntent, TravelHelperSideEffect>(
    initialState = TravelHelperState(),
) {
    init {
        viewModelScope.launch {
            val supportedCodes = runCatching {
                exchangeRateRepository.getSupportedCurrencyCodes()
            }.getOrDefault(emptyList())

            val currencies = supportedCodes
                .filter { CurrencyInfoResolver.getCountryCode(it) != null }
                .map { code ->
                    CurrencyOption(
                        currencyCode = code,
                        countryName = CurrencyInfoResolver.getCountryName(code),
                    )
                }
            val allCurrencies =
                (listOf(CurrencyOption(currencyCode = "KRW", countryName = "대한민국")) + currencies)
                    .toImmutableList()

            reduce { copy(availableCurrencies = allCurrencies) }
        }
        loadUpcomingTravel()
        subscribeToTravelCreatedEvent()
    }

    private fun subscribeToTravelCreatedEvent() = viewModelScope.launch {
        userTravelRepository.travelCreatedEvent.collect { _ ->
            loadUpcomingTravel()
        }
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
                    val currencyCode = CurrencyInfoResolver.getCurrencyCode(travel.country)
                    val exchangeRateInfo =
                        buildExchangeRateInfo(topCode = currencyCode, bottomCode = "KRW")

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
                                days = travel.days,
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
                                dayCount = dayNumber.toInt(),
                                weatherState = WeatherUiState.NotAvailable,
                                currentPlace = place?.place?.let {
                                    TravelPlace(
                                        googlePlaceId = it.googlePlaceId,
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
                            convertedAmount = calculateConvertedAmount(
                                currencyInput,
                                exchangeRateInfo.rate,
                            ),
                        )
                    }

                    loadWeather(travel.city, travel.country, travel.startDate, travel.endDate)
                }
                .onFailure {
                    reduce { copy(travelUiState = TravelUiState.NoTravel) }
                }
        }
    }

    private fun buildCurrencyInfo(code: String): CurrencyInfo {
        return if (code == "KRW") {
            CurrencyInfo(
                currencyCode = "KRW",
                currencyLabel = "원",
                countryName = "대한민국",
                flagEmoji = "KR".toFlagEmoji(),
            )
        } else {
            CurrencyInfo(
                currencyCode = code,
                currencyLabel = CurrencyInfoResolver.getKoreanName(code),
                countryName = CurrencyInfoResolver.getCountryName(code),
                flagEmoji = (CurrencyInfoResolver.getCountryCode(code) ?: "").toFlagEmoji(),
            )
        }
    }

    private suspend fun buildExchangeRateInfo(
        topCode: String,
        bottomCode: String,
    ): ExchangeRateInfo {
        val rateResult = runCatching { getCrossRate(topCode, bottomCode) }
        rateResult.onFailure {
            postSideEffect(TravelHelperSideEffect.ShowExchangeRateError)
        }
        return ExchangeRateInfo(
            topCurrency = buildCurrencyInfo(topCode),
            bottomCurrency = buildCurrencyInfo(bottomCode),
            rate = rateResult.getOrDefault(1.0),
            rateDate = LocalDate.now(),
        )
    }

    private suspend fun getCrossRate(from: String, to: String): Double {
        if (from == to) return 1.0
        return when {
            to == "KRW" -> {
                exchangeRateRepository.getKrwRate(from)
                    ?: throw IllegalStateException("Exchange rate not available for $from")
            }

            from == "KRW" -> {
                val toKrw = exchangeRateRepository.getKrwRate(to)
                    ?: throw IllegalStateException("Exchange rate not available for $to")
                if (toKrw == 0.0) throw IllegalStateException("KRW rate for $to is zero")
                1.0 / toKrw
            }

            else -> {
                val fromKrw = exchangeRateRepository.getKrwRate(from)
                    ?: throw IllegalStateException("Exchange rate not available for $from")
                val toKrw = exchangeRateRepository.getKrwRate(to)
                    ?: throw IllegalStateException("Exchange rate not available for $to")
                if (toKrw == 0.0) throw IllegalStateException("KRW rate for $to is zero")
                fromKrw / toKrw
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
            }.onSuccess { response ->
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
        }
    }

    override suspend fun handleIntent(intent: TravelHelperIntent) {
        when (intent) {
            TravelHelperIntent.ClickSearch -> postSideEffect(TravelHelperSideEffect.NavigateToSearch)
            is TravelHelperIntent.UpdateCurrencyInput -> {
                val rate = getExchangeRateInfo()?.rate ?: 1.0
                val converted = calculateConvertedAmount(intent.input, rate)
                reduce { copy(currencyInput = intent.input, convertedAmount = converted) }
            }

            TravelHelperIntent.SwapCurrency -> {
                val info = getExchangeRateInfo() ?: return
                val newInfo = buildExchangeRateInfo(
                    topCode = info.bottomCurrency.currencyCode,
                    bottomCode = info.topCurrency.currencyCode,
                )
                val newInput = state.value.convertedAmount?.let { "%.2f".format(it) }
                    ?: state.value.currencyInput
                val newConverted = calculateConvertedAmount(newInput, newInfo.rate)
                reduce {
                    copy(
                        travelUiState = travelUiState.withExchangeRateInfo(newInfo),
                        currencyInput = newInput,
                        convertedAmount = newConverted,
                    )
                }
            }

            is TravelHelperIntent.ClickTravelCard -> {
                postSideEffect(NavigateToTravelDetail(intent.travelId, intent.days))
            }

            is TravelHelperIntent.SelectCurrency -> {
                val currentInfo = getExchangeRateInfo() ?: return
                if (intent.currencyCode == currentInfo.topCurrency.currencyCode) return
                val newInfo = buildExchangeRateInfo(
                    topCode = intent.currencyCode,
                    bottomCode = currentInfo.bottomCurrency.currencyCode,
                )
                val newConverted = calculateConvertedAmount("1", newInfo.rate)
                reduce {
                    copy(
                        travelUiState = travelUiState.withExchangeRateInfo(newInfo),
                        currencyInput = "1",
                        convertedAmount = newConverted,
                    )
                }
            }

            is TravelHelperIntent.ClickPlace -> {
                postSideEffect(TravelHelperSideEffect.NavigateToPlaceDetail(intent.placeId))
            }
        }
    }

    private fun getExchangeRateInfo(): ExchangeRateInfo? =
        when (val ts = state.value.travelUiState) {
            is TravelUiState.UpcomingTravel -> ts.exchangeRateInfo
            is TravelUiState.OngoingTravel -> ts.exchangeRateInfo
            else -> null
        }

    private fun TravelUiState.withExchangeRateInfo(info: ExchangeRateInfo): TravelUiState =
        when (this) {
            is TravelUiState.UpcomingTravel -> copy(exchangeRateInfo = info)
            is TravelUiState.OngoingTravel -> copy(exchangeRateInfo = info)
            else -> this
        }

    private fun calculateConvertedAmount(input: String, rate: Double): Double? {
        val amount = input.toDoubleOrNull() ?: return null
        return amount * rate
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
}
