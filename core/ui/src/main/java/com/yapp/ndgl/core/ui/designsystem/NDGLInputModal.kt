package com.yapp.ndgl.core.ui.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.yapp.ndgl.core.ui.theme.NDGLTheme

@Composable
fun NDGLInputModal(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions,
    positiveButtonText: String,
    onPositiveButtonClick: () -> Unit,
    negativeButtonText: String,
    onNegativeButtonClick: (() -> Unit) = {},
    minHeight: Dp = 56.dp,
    maxLines: Int = Int.MAX_VALUE,
    textStyle: TextStyle,
    placeholderStyle: TextStyle,
    textAlign: TextAlign = TextAlign.Start,
) {
    val focusRequester = remember { FocusRequester() }
    var textFieldValue by remember(value) {
        mutableStateOf(TextFieldValue(value, selection = TextRange(value.length)))
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // value가 변경되면 textFieldValue 업데이트
    LaunchedEffect(value) {
        if (textFieldValue.text != value) {
            textFieldValue = TextFieldValue(
                text = value,
                selection = TextRange(value.length),
            )
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = modifier.wrapContentHeight(),
            shape = RoundedCornerShape(8.dp),
            color = NDGLTheme.colors.white,
            shadowElevation = 16.dp,
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .padding(top = 28.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    modifier = Modifier.fillMaxWidth(),
                    color = NDGLTheme.colors.black900,
                    textAlign = TextAlign.Center,
                    style = NDGLTheme.typography.subtitleLgSemiBold,
                )

                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        textFieldValue = newValue
                        onValueChange(newValue.text)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = minHeight)
                        .clip(RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .focusRequester(focusRequester),
                    textStyle = textStyle.copy(
                        color = NDGLTheme.colors.black500,
                        textAlign = textAlign,
                    ),
                    keyboardOptions = keyboardOptions,
                    maxLines = maxLines,
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = when (textAlign) {
                                TextAlign.Center -> Alignment.Center
                                else -> Alignment.TopStart
                            },
                        ) {
                            if (textFieldValue.text.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    style = placeholderStyle,
                                    color = NDGLTheme.colors.black300,
                                )
                            }
                            innerTextField()
                        }
                    },
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    NDGLCTAButton(
                        modifier = Modifier.weight(1f),
                        type = NDGLCTAButtonAttr.Type.SECONDARY,
                        size = NDGLCTAButtonAttr.Size.MEDIUM,
                        status = NDGLCTAButtonAttr.Status.ACTIVE,
                        label = negativeButtonText,
                        onClick = {
                            onNegativeButtonClick()
                            onDismissRequest()
                        },
                    )
                    NDGLCTAButton(
                        modifier = Modifier.weight(1f),
                        type = NDGLCTAButtonAttr.Type.PRIMARY,
                        size = NDGLCTAButtonAttr.Size.MEDIUM,
                        status = if (value.isNotEmpty()) {
                            NDGLCTAButtonAttr.Status.ACTIVE
                        } else {
                            NDGLCTAButtonAttr.Status.DISABLED
                        },
                        label = positiveButtonText,
                        onClick = {
                            if (value.isNotEmpty()) {
                                onPositiveButtonClick()
                                onDismissRequest()
                            }
                        },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NDGLInputModalCostPreview() {
    var value by remember { mutableStateOf("10000") }

    NDGLTheme {
        Box(Modifier.fillMaxSize()) {
            NDGLInputModal(
                onDismissRequest = {},
                title = "비용 추가",
                value = value,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        value = newValue
                    }
                },
                placeholder = "비용을 추가해 보세요",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                positiveButtonText = "확인",
                onPositiveButtonClick = {},
                negativeButtonText = "취소",
                textAlign = TextAlign.Center,
                placeholderStyle = NDGLTheme.typography.subtitleLgSemiBold,
                textStyle = NDGLTheme.typography.subtitleLgSemiBold,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NDGLInputModalMemoPreview() {
    var value by remember { mutableStateOf("") }

    NDGLTheme {
        Box(Modifier.fillMaxSize()) {
            NDGLInputModal(
                onDismissRequest = {},
                title = "메모 추가",
                value = value,
                onValueChange = { value = it },
                placeholder = "정보들을 메모해 보세요",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Default,
                ),
                positiveButtonText = "확인",
                onPositiveButtonClick = {},
                negativeButtonText = "취소",
                minHeight = 120.dp,
                placeholderStyle = NDGLTheme.typography.bodyLgMedium,
                textStyle = NDGLTheme.typography.bodyLgRegular,
            )
        }
    }
}
