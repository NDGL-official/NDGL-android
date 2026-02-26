package com.yapp.ndgl.feature.travelhelper.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.feature.travelhelper.R
import com.yapp.ndgl.feature.travelhelper.main.TravelHelperState.ExchangeRateInfo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.format.DateTimeFormatter
import com.yapp.ndgl.core.ui.R as CoreR

@Composable
internal fun CurrencyCalculatorSection(
    exchangeRateInfo: ExchangeRateInfo,
    currencyInput: String,
    convertedAmount: Double?,
    availableCurrencies: ImmutableList<CurrencyOption>,
    onInputChange: (String) -> Unit,
    onSwap: () -> Unit,
    onCurrencySelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val convertedFormatted = convertedAmount?.let { "%,.2f".format(it) } ?: "-"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = stringResource(R.string.travel_helper_currency_calculator_title),
            style = NDGLTheme.typography.subtitleLgSemiBold,
            color = NDGLTheme.colors.black700,
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                CurrencyCard(
                    modifier = Modifier.fillMaxWidth(),
                    isEditable = true,
                    flagEmoji = exchangeRateInfo.topCurrency.flagEmoji,
                    currencyName = exchangeRateInfo.topCurrency.countryName,
                    currencyCode = exchangeRateInfo.topCurrency.currencyCode,
                    currencyLabel = exchangeRateInfo.topCurrency.currencyLabel,
                    currencyInput = currencyInput,
                    availableCurrencies = availableCurrencies,
                    selectedCurrencyCode = exchangeRateInfo.topCurrency.currencyCode,
                    onInputChange = onInputChange,
                    onCurrencySelect = onCurrencySelect,
                )
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 26.dp),
                        thickness = 1.dp,
                        color = NDGLTheme.colors.black200,
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(NDGLTheme.colors.black50)
                            .clickable(onClick = onSwap),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(CoreR.drawable.ic_24_change),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = NDGLTheme.colors.black900,
                        )
                    }
                }
                CurrencyCard(
                    modifier = Modifier.fillMaxWidth(),
                    isEditable = false,
                    flagEmoji = exchangeRateInfo.bottomCurrency.flagEmoji,
                    currencyName = exchangeRateInfo.bottomCurrency.countryName,
                    currencyCode = exchangeRateInfo.bottomCurrency.currencyCode,
                    currencyLabel = exchangeRateInfo.bottomCurrency.currencyLabel,
                    currencyInput = convertedFormatted,
                    availableCurrencies = persistentListOf(),
                    selectedCurrencyCode = "",
                    onInputChange = {},
                    onCurrencySelect = {},
                )
            }

            Text(
                text = stringResource(
                    R.string.travel_helper_currency_rate_date,
                    exchangeRateInfo.rateDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")),
                ),
                color = NDGLTheme.colors.black400,
                style = NDGLTheme.typography.bodyMdRegular,
            )
        }
    }
}

@Composable
private fun CurrencyCard(
    modifier: Modifier,
    isEditable: Boolean,
    flagEmoji: String,
    currencyName: String,
    currencyCode: String,
    currencyLabel: String,
    currencyInput: String,
    availableCurrencies: ImmutableList<CurrencyOption>,
    selectedCurrencyCode: String,
    onInputChange: (String) -> Unit,
    onCurrencySelect: (String) -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .border(
                width = 1.dp,
                color = NDGLTheme.colors.black200,
                shape = RoundedCornerShape(4.dp),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ForeignCurrencyLeft(
            modifier = Modifier.wrapContentWidth(),
            isEditable = isEditable,
            flagEmoji = flagEmoji,
            currencyName = currencyName,
            currencyCode = currencyCode,
            availableCurrencies = availableCurrencies,
            selectedCurrencyCode = selectedCurrencyCode,
            onCurrencySelect = onCurrencySelect,
        )
        ForeignCurrencyRight(
            modifier = Modifier.weight(1f),
            isEditable = isEditable,
            currencyInput = currencyInput,
            currencyLabel = currencyLabel,
            onInputChange = onInputChange,
        )
    }
}

@Composable
private fun ForeignCurrencyLeft(
    modifier: Modifier,
    isEditable: Boolean,
    flagEmoji: String,
    currencyName: String,
    currencyCode: String,
    availableCurrencies: ImmutableList<CurrencyOption>,
    selectedCurrencyCode: String,
    onCurrencySelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .background(
                color = NDGLTheme.colors.black50,
                shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp),
            )
            .then(
                if (isEditable) {
                    Modifier.clickable(
                        interactionSource = null,
                        indication = ripple(),
                        onClick = { expanded = true },
                    )
                } else {
                    Modifier
                },
            )
            .padding(horizontal = 10.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.width(142.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = flagEmoji,
                style = NDGLTheme.typography.bodyLgSemiBold,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = currencyName,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (isEditable) NDGLTheme.colors.black800 else NDGLTheme.colors.black500,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = NDGLTheme.typography.bodyLgSemiBold,
                )
                Text(
                    text = currencyCode,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (isEditable) NDGLTheme.colors.black400 else NDGLTheme.colors.black300,
                    style = NDGLTheme.typography.bodyMdMedium,
                )
            }
        }
        if (isEditable) {
            Icon(
                imageVector = ImageVector.vectorResource(CoreR.drawable.ic_24_chevron_down),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = NDGLTheme.colors.black600,
            )
        } else {
            Box(modifier = Modifier.size(24.dp))
        }
    }
    if (isEditable) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(8.dp),
            containerColor = NDGLTheme.colors.white,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, NDGLTheme.colors.black200),
        ) {
            availableCurrencies.forEach { option ->
                CurrencyDropdownItem(
                    option = option,
                    isSelected = option.currencyCode == selectedCurrencyCode,
                    onClick = {
                        expanded = false
                        onCurrencySelect(option.currencyCode)
                    },
                )
            }
        }
    }
}

@Composable
private fun CurrencyDropdownItem(
    option: CurrencyOption,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) {
                    NDGLTheme.colors.green100
                } else {
                    NDGLTheme.colors.white
                },
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (isSelected) {
            Icon(
                imageVector = ImageVector.vectorResource(CoreR.drawable.ic_20_check),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = NDGLTheme.colors.black900,
            )
        } else {
            Spacer(modifier = Modifier.size(20.dp))
        }
        Text(
            text = option.countryName,
            style = NDGLTheme.typography.bodyMdMedium,
            color = NDGLTheme.colors.black800,
        )
        Text(
            text = option.currencyCode,
            style = NDGLTheme.typography.bodyMdMedium,
            color = NDGLTheme.colors.black400,
        )
    }
}

@Composable
private fun ForeignCurrencyRight(
    modifier: Modifier,
    isEditable: Boolean,
    currencyInput: String,
    currencyLabel: String,
    onInputChange: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        BasicTextField(
            value = currencyInput,
            onValueChange = onInputChange,
            modifier = Modifier.fillMaxWidth(),
            readOnly = isEditable.not(),
            textStyle = if (isEditable) {
                NDGLTheme.typography.bodyLgSemiBold.copy(
                    color = NDGLTheme.colors.green500,
                    textAlign = TextAlign.End,
                )
            } else {
                NDGLTheme.typography.bodyLgMedium.copy(
                    color = NDGLTheme.colors.black500,
                    textAlign = TextAlign.End,
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            cursorBrush = SolidColor(NDGLTheme.colors.green500),
            visualTransformation = if (isEditable) {
                ThousandSeparatorTransformation()
            } else {
                VisualTransformation.None
            },
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterEnd) {
                    if (currencyInput.isEmpty()) {
                        Text(
                            text = "0",
                            style = NDGLTheme.typography.bodyLgSemiBold,
                            color = NDGLTheme.colors.black300,
                            textAlign = TextAlign.End,
                        )
                    }
                    innerTextField()
                }
            },
        )
        val displayInput = if (isEditable) {
            val parts = currencyInput.split(".")
            val formattedInt = parts[0].ifEmpty { "0" }
                .reversed().chunked(3).joinToString(",").reversed()
            if (parts.size > 1) "$formattedInt.${parts[1]}" else formattedInt
        } else {
            currencyInput.ifEmpty { "0" }
        }
        Text(
            text = "$displayInput $currencyLabel",
            modifier = Modifier.fillMaxWidth(),
            color = if (isEditable) NDGLTheme.colors.black400 else NDGLTheme.colors.black300,
            textAlign = TextAlign.End,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = NDGLTheme.typography.bodyMdRegular,
        )
    }
}

private class ThousandSeparatorTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        val dotIndex = original.indexOf('.')
        val intPart = if (dotIndex >= 0) original.substring(0, dotIndex) else original
        val decimalPart = if (dotIndex >= 0) original.substring(dotIndex) else ""

        val formattedInt = if (intPart.isEmpty()) {
            ""
        } else {
            intPart.reversed().chunked(3).joinToString(",").reversed()
        }
        val formatted = formattedInt + decimalPart

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= intPart.length) {
                    var origCount = 0
                    formattedInt.forEachIndexed { idx, ch ->
                        if (origCount == offset) return idx
                        if (ch != ',') origCount++
                    }
                    return formattedInt.length
                }
                return formattedInt.length + (offset - intPart.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val fmtIntLen = formattedInt.length
                return if (offset <= fmtIntLen) {
                    formattedInt.take(offset).count { it != ',' }
                } else {
                    intPart.length + (offset - fmtIntLen)
                }
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyCalculatorSectionPreview() {
    NDGLTheme {
        CurrencyCalculatorSection(
            exchangeRateInfo = ExchangeRateInfo(
                topCurrency = TravelHelperState.CurrencyInfo(
                    currencyCode = "JPY",
                    currencyLabel = "엔",
                    countryName = "일본",
                    flagEmoji = "\uD83C\uDDEF\uD83C\uDDF5",
                ),
                bottomCurrency = TravelHelperState.CurrencyInfo(
                    currencyCode = "KRW",
                    currencyLabel = "원",
                    countryName = "대한민국",
                    flagEmoji = "\uD83C\uDDF0\uD83C\uDDF7",
                ),
                rate = 9.5,
                rateDate = java.time.LocalDate.of(2025, 1, 1),
            ),
            currencyInput = "1000",
            convertedAmount = 9500.0,
            availableCurrencies = persistentListOf(
                CurrencyOption(currencyCode = "JPY", countryName = "일본"),
                CurrencyOption(currencyCode = "KRW", countryName = "대한민국"),
            ),
            onInputChange = {},
            onSwap = {},
            onCurrencySelect = {},
        )
    }
}
