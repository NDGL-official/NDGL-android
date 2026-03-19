package com.yapp.ndgl.feature.travelhelper.main

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.yapp.ndgl.core.base.UiIntent
import com.yapp.ndgl.core.base.UiSideEffect
import com.yapp.ndgl.core.base.UiState
import com.yapp.ndgl.data.travel.model.PlaceCategory
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate

data class CurrencyOption(
    val currencyCode: String,
    val countryName: String,
)

@Stable
data class TravelHelperState(
    val travelUiState: TravelUiState = TravelUiState.Loading,
    val currencyInput: String = "1",
    val convertedAmount: Double? = null,
    val availableCurrencies: ImmutableList<CurrencyOption> = persistentListOf(),
) : UiState {
    sealed interface TravelUiState {
        data object Loading : TravelUiState

        data object NoTravel : TravelUiState

        data class UpcomingTravel(
            val id: Long,
            val title: String,
            val city: String,
            val startDate: LocalDate,
            val endDate: LocalDate,
            val thumbnail: String?,
            val dDay: Long,
            val days: Int,
            val weatherState: WeatherUiState,
            val exchangeRateInfo: ExchangeRateInfo,
        ) : TravelUiState

        data class OngoingTravel(
            val id: Long,
            val title: String,
            val city: String,
            val startDate: LocalDate,
            val endDate: LocalDate,
            val thumbnail: String?,
            val dayCount: Int,
            val weatherState: WeatherUiState,
            val currentPlace: TravelPlace?,
            val exchangeRateInfo: ExchangeRateInfo,
        ) : TravelUiState
    }

    @Stable
    sealed interface WeatherUiState {
        data object NotAvailable : WeatherUiState
        data class Available(
            val forecasts: ImmutableList<WeatherForecastUiInfo>,
        ) : WeatherUiState
    }

    @Immutable
    data class WeatherForecastUiInfo(
        val date: LocalDate,
        val dayOfWeek: String,
        val iconUrl: String?,
        val highTempCelsius: Int,
        val lowTempCelsius: Int,
    )

    data class TravelPlace(
        val googlePlaceId: String,
        val name: String,
        val category: PlaceCategory,
        val estimatedDuration: Int,
        val thumbnailUrl: String?,
    )

    @Immutable
    data class CurrencyInfo(
        val currencyCode: String,
        val currencyLabel: String,
        val countryName: String,
        val flagEmoji: String,
    )

    @Immutable
    data class ExchangeRateInfo(
        val topCurrency: CurrencyInfo,
        val bottomCurrency: CurrencyInfo,
        val rate: Double,
        val rateDate: LocalDate,
    )
}

sealed interface TravelHelperIntent : UiIntent {
    data object ClickSearch : TravelHelperIntent
    data class UpdateCurrencyInput(val input: String) : TravelHelperIntent
    data object SwapCurrency : TravelHelperIntent
    data class SelectCurrency(val currencyCode: String) : TravelHelperIntent
    data class ClickTravelCard(val travelId: Long, val days: Int) : TravelHelperIntent
    data class ClickPlace(val googlePlaceId: String) : TravelHelperIntent
}

sealed interface TravelHelperSideEffect : UiSideEffect {
    data object NavigateToSearch : TravelHelperSideEffect
    data object ShowExchangeRateError : TravelHelperSideEffect
    data class NavigateToTravelDetail(val travelId: Long, val days: Int) : TravelHelperSideEffect
    data class NavigateToPlaceDetail(val googlePlaceId: String) : TravelHelperSideEffect
}
