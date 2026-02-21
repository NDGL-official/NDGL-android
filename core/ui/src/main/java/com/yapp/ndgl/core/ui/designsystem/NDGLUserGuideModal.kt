package com.yapp.ndgl.core.ui.designsystem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.theme.NDGLTheme

@Composable
fun UserGuideModal(
    onConfirmClick: () -> Unit,
    onTermsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = { /* 확인 버튼을 눌러야만 닫힘 */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
    ) {
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
                UserGuideContent(
                    onTermsClick = onTermsClick,
                )
                NDGLCTAButton(
                    type = NDGLCTAButtonAttr.Type.PRIMARY,
                    size = NDGLCTAButtonAttr.Size.MEDIUM,
                    status = NDGLCTAButtonAttr.Status.ACTIVE,
                    label = stringResource(R.string.user_guide_modal_confirm),
                    onClick = onConfirmClick,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun UserGuideContent(
    onTermsClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.user_guide_modal_title),
            modifier = Modifier.fillMaxWidth(),
            color = NDGLTheme.colors.black900,
            textAlign = TextAlign.Center,
            style = NDGLTheme.typography.subtitleLgSemiBold,
        )
        Text(
            text = stringResource(R.string.user_guide_modal_body),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            color = NDGLTheme.colors.black500,
            textAlign = TextAlign.Center,
            style = NDGLTheme.typography.bodyLgMedium,
        )
        Text(
            text = stringResource(R.string.user_guide_modal_terms),
            modifier = Modifier
                .padding(top = 12.dp)
                .clickable(onClick = onTermsClick),
            color = NDGLTheme.colors.black400,
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.Underline,
            style = NDGLTheme.typography.bodyMdMedium,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UserGuideModalPreview() {
    NDGLTheme {
        UserGuideModal(
            onConfirmClick = {},
            onTermsClick = {},
        )
    }
}
