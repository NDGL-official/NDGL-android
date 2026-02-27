package com.yapp.ndgl.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.ndgl.core.ui.theme.NDGLTheme

@Composable
fun CommonErrorView(
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 165.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.img_empty_browser),
            contentDescription = null,
            modifier = Modifier.size(100.dp),
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title ?: stringResource(R.string.common_error_title),
                color = NDGLTheme.colors.black500,
                textAlign = TextAlign.Center,
                style = NDGLTheme.typography.subtitleMdSemiBold,
            )
            Text(
                text = description ?: stringResource(R.string.common_error_description),
                color = NDGLTheme.colors.black400,
                textAlign = TextAlign.Center,
                style = NDGLTheme.typography.bodyLgRegular,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommonErrorViewPreview() {
    NDGLTheme {
        CommonErrorView(
            modifier = Modifier
        )
    }
}
