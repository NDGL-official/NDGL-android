package com.yapp.ndgl.feature.travelhelper.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.feature.travelhelper.R
import com.yapp.ndgl.feature.travelhelper.main.TravelHelperState.WeatherForecastUiInfo
import com.yapp.ndgl.feature.travelhelper.main.TravelHelperState.WeatherUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.yapp.ndgl.core.ui.R as CoreR

@Composable
internal fun WeatherSection(
    weatherState: WeatherUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = stringResource(R.string.travel_helper_weather_section_title),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            style = NDGLTheme.typography.subtitleLgSemiBold,
            color = NDGLTheme.colors.black700,
        )
        when (weatherState) {
            WeatherUiState.NotAvailable -> WeatherEmptyState()
            is WeatherUiState.Available -> WeatherCardRow(forecasts = weatherState.forecasts)
        }
    }
}

@Composable
private fun WeatherEmptyState(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(CoreR.drawable.img_empty_suitcase),
            contentDescription = null,
            modifier = Modifier.size(100.dp),
        )
        Text(
            text = stringResource(R.string.travel_helper_weather_not_available),
            modifier = Modifier.fillMaxWidth(),
            color = NDGLTheme.colors.black400,
            textAlign = TextAlign.Center,
            style = NDGLTheme.typography.bodyMdRegular,
        )
    }
}

@Composable
private fun WeatherCardRow(
    forecasts: ImmutableList<WeatherForecastUiInfo>,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(
            items = forecasts,
            key = { it.date },
        ) { forecast ->
            WeatherCard(forecast = forecast)
        }
    }
}

@Composable
private fun WeatherCard(
    forecast: WeatherForecastUiInfo,
    modifier: Modifier = Modifier,
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MM.dd") }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(NDGLTheme.colors.white)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = forecast.date.format(dateFormatter),
                style = NDGLTheme.typography.bodyLgSemiBold,
                color = NDGLTheme.colors.black700,
            )
            Text(
                text = forecast.dayOfWeek,
                style = NDGLTheme.typography.bodySmMedium,
                color = NDGLTheme.colors.black400,
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AsyncImage(
                model = forecast.iconUrl,
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .size(56.dp),
                contentScale = ContentScale.Fit,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${forecast.highTempCelsius}° /",
                    style = NDGLTheme.typography.bodyMdMedium,
                    color = NDGLTheme.colors.black500,
                )
                Text(
                    text = "${forecast.lowTempCelsius}°",
                    style = NDGLTheme.typography.bodyMdMedium,
                    color = NDGLTheme.colors.black400,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherSectionNotAvailablePreview() {
    NDGLTheme {
        WeatherSection(
            weatherState = WeatherUiState.NotAvailable,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherSectionAvailablePreview() {
    NDGLTheme {
        WeatherSection(
            weatherState = WeatherUiState.Available(
                forecasts = persistentListOf(
                    WeatherForecastUiInfo(
                        date = LocalDate.of(2025, 3, 1),
                        dayOfWeek = "토요일",
                        iconUrl = null,
                        highTempCelsius = 18,
                        lowTempCelsius = 10,
                    ),
                    WeatherForecastUiInfo(
                        date = LocalDate.of(2025, 3, 2),
                        dayOfWeek = "일요일",
                        iconUrl = null,
                        highTempCelsius = 20,
                        lowTempCelsius = 12,
                    ),
                    WeatherForecastUiInfo(
                        date = LocalDate.of(2025, 3, 3),
                        dayOfWeek = "월요일",
                        iconUrl = null,
                        highTempCelsius = 15,
                        lowTempCelsius = 8,
                    ),
                    WeatherForecastUiInfo(
                        date = LocalDate.of(2025, 3, 4),
                        dayOfWeek = "화요일",
                        iconUrl = null,
                        highTempCelsius = 13,
                        lowTempCelsius = 7,
                    ),
                    WeatherForecastUiInfo(
                        date = LocalDate.of(2025, 3, 5),
                        dayOfWeek = "수요일",
                        iconUrl = null,
                        highTempCelsius = 16,
                        lowTempCelsius = 9,
                    ),
                ),
            ),
        )
    }
}
