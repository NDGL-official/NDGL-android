package com.yapp.ndgl.feature.travel.additinerary.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.core.ui.util.dropShadow

@Composable
internal fun AddItineraryTopBar(
    modifier: Modifier = Modifier,
    keyword: String,
    isSearchFocused: Boolean,
    clickBackButton: () -> Unit,
    focusSearch: () -> Unit,
    updateKeyword: (String) -> Unit,
    searchKeyword: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var textFieldValue by remember { mutableStateOf(TextFieldValue(keyword)) }

    LaunchedEffect(keyword) {
        if (textFieldValue.text != keyword) {
            textFieldValue = textFieldValue.copy(text = keyword)
        }
    }

    LaunchedEffect(isSearchFocused) {
        if (isSearchFocused) {
            textFieldValue = textFieldValue.copy(
                selection = TextRange(textFieldValue.text.length),
            )
            focusRequester.requestFocus()
        } else {
            focusManager.clearFocus()
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_28_chevron_left),
            contentDescription = null,
            tint = NDGLTheme.colors.black600,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .clickable { clickBackButton() },
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .then(
                    if (isSearchFocused) {
                        Modifier
                    } else {
                        Modifier.dropShadow(
                            shape = CircleShape,
                            color = Color.Black.copy(0.1f),
                            blur = 4.dp,
                            offsetY = 1.dp,
                        )
                    },
                )
                .clip(CircleShape)
                .background(if (isSearchFocused) NDGLTheme.colors.black100 else NDGLTheme.colors.white)
                .clickable {
                    focusRequester.requestFocus()
                    focusSearch()
                }
                .padding(horizontal = 16.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    textFieldValue = newValue
                    updateKeyword(newValue.text)
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused && !isSearchFocused) {
                            focusSearch()
                        }
                    },
                textStyle = NDGLTheme.typography.bodyLgSemiBold.copy(
                    color = NDGLTheme.colors.black700,
                ),
                cursorBrush = SolidColor(NDGLTheme.colors.black700),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        searchKeyword()
                        focusManager.clearFocus()
                    },
                ),
                decorationBox = { innerTextField ->
                    if (keyword.isEmpty()) {
                        Text(
                            text = stringResource(R.string.add_itinerary_search_placeholder),
                            style = NDGLTheme.typography.bodyLgRegular,
                            color = NDGLTheme.colors.black400,
                        )
                    }
                    innerTextField()
                },
            )
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_28_search),
                contentDescription = null,
                tint = NDGLTheme.colors.black600,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .clickable {
                        searchKeyword()
                    },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddItineraryTopBarPreview() {
    NDGLTheme {
        AddItineraryTopBar(
            keyword = "",
            isSearchFocused = false,
            clickBackButton = {},
            focusSearch = {},
            updateKeyword = {},
            searchKeyword = {},
        )
    }
}
