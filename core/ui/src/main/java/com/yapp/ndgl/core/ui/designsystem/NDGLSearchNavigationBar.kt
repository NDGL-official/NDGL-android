package com.yapp.ndgl.core.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.theme.NDGLTheme

@Composable
fun NDGLSearchNavigationBar(
    searchKeyword: String,
    onSearchKeywordChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 24.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_28_chevron_left),
            contentDescription = null,
            modifier = Modifier
                .clickable(
                    onClick = onBackClick,
                    interactionSource = null,
                    indication = ripple(bounded = false),
                )
                .size(28.dp),
            tint = NDGLTheme.colors.black600,
        )

        BasicTextField(
            value = searchKeyword,
            onValueChange = onSearchKeywordChange,
            modifier = Modifier.weight(1f),
            textStyle = NDGLTheme.typography.bodyLgRegular.copy(
                color = NDGLTheme.colors.black700,
            ),
            singleLine = true,
            cursorBrush = SolidColor(NDGLTheme.colors.black400),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch(searchKeyword) }),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(NDGLTheme.colors.black100)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (placeholder != null && searchKeyword.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = NDGLTheme.typography.bodyLgRegular,
                                color = NDGLTheme.colors.black400,
                            )
                        }
                        innerTextField()
                    }
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_28_search),
                        contentDescription = null,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable(
                                onClick = { onSearch(searchKeyword) },
                                interactionSource = null,
                                indication = ripple(bounded = false),
                            ),
                        tint = NDGLTheme.colors.black600,
                    )
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NDGLSearchNavigationBarEmptyPreview() {
    NDGLTheme {
        NDGLSearchNavigationBar(
            searchKeyword = "",
            onSearchKeywordChange = {},
            placeholder = "여행 템플릿을 검색해 보세요",
            onSearch = {},
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NDGLSearchNavigationBarFilledPreview() {
    NDGLTheme {
        NDGLSearchNavigationBar(
            searchKeyword = "제주도 3박 4일",
            onSearchKeywordChange = {},
            placeholder = "여행 템플릿을 검색해 보세요",
            onSearch = {},
            onBackClick = {},
        )
    }
}
