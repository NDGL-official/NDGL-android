package com.yapp.ndgl.feature.travelhelper.main

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.yapp.ndgl.core.base.UiIntent
import com.yapp.ndgl.core.base.UiSideEffect
import com.yapp.ndgl.core.base.UiState
import com.yapp.ndgl.data.travel.model.PlaceCategory
import kotlinx.collections.immutable.ImmutableList
import java.time.LocalDate

@Stable
data class TravelHelperState(
    val travelUiState: TravelUiState = TravelUiState.Loading,
    val currencyInput: String = "1",
    val convertedAmount: Double? = null,
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
            val dayCount: Long,
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
        val name: String,
        val category: PlaceCategory,
        val estimatedDuration: Int,
        val thumbnailUrl: String?,
    )

    data class ExchangeRateInfo(
        val foreignCurrencyCode: String,
        val foreignCurrencyName: String,
        val rateToKrw: Double,
        val rateDate: String,
    )
}

sealed interface TravelHelperIntent : UiIntent {
    data object ClickSearch : TravelHelperIntent
    data class UpdateCurrencyInput(val input: String) : TravelHelperIntent
    data object SwapCurrency : TravelHelperIntent
}

sealed interface TravelHelperSideEffect : UiSideEffect {
    data object NavigateToSearch : TravelHelperSideEffect
}
