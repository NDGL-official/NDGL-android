package com.yapp.ndgl.feature.travelhelper.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.feature.travelhelper.R
import com.yapp.ndgl.feature.travelhelper.main.TravelHelperState.ExchangeRateInfo

@Composable
internal fun CurrencyCalculatorSection(
    exchangeRateInfo: ExchangeRateInfo,
    currencyInput: String,
    convertedAmount: Double?,
    onInputChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.travel_helper_currency_calculator_title),
            style = NDGLTheme.typography.subtitleMdSemiBold,
            color = NDGLTheme.colors.black900,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(NDGLTheme.colors.black50)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${exchangeRateInfo.foreignCurrencyCode} (${exchangeRateInfo.foreignCurrencyName})",
                    style = NDGLTheme.typography.bodyMdMedium,
                    color = NDGLTheme.colors.black600,
                )
                BasicTextField(
                    value = currencyInput,
                    onValueChange = onInputChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    textStyle = NDGLTheme.typography.subtitleMdSemiBold.copy(
                        color = NDGLTheme.colors.black900,
                        textAlign = TextAlign.End,
                    ),
                    cursorBrush = SolidColor(NDGLTheme.colors.green500),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterEnd) {
                            if (currencyInput.isEmpty()) {
                                Text(
                                    text = "0",
                                    style = NDGLTheme.typography.subtitleMdSemiBold,
                                    color = NDGLTheme.colors.black300,
                                    textAlign = TextAlign.End,
                                )
                            }
                            innerTextField()
                        }
                    },
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(NDGLTheme.colors.black200),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.travel_helper_currency_krw),
                    style = NDGLTheme.typography.bodyMdMedium,
                    color = NDGLTheme.colors.black600,
                )
                Text(
                    text = convertedAmount?.let {
                        String.format("%,.0f", it)
                    } ?: "-",
                    style = NDGLTheme.typography.subtitleMdSemiBold,
                    color = NDGLTheme.colors.black900,
                )
            }

            Text(
                text = stringResource(
                    R.string.travel_helper_currency_rate_info,
                    exchangeRateInfo.foreignCurrencyCode,
                    String.format("%,.2f", exchangeRateInfo.rateToKrw),
                ),
                style = NDGLTheme.typography.bodySmRegular,
                color = NDGLTheme.colors.black400,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )
        }
    }
}
