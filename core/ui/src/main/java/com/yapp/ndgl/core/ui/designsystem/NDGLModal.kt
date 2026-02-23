package com.yapp.ndgl.core.ui.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yapp.ndgl.core.ui.theme.NDGLTheme

@Composable
fun NDGLModal(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    title: String,
    body: String,
    description: String? = null,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    positiveButtonText: String,
    onPositiveButtonClick: () -> Unit,
    negativeButtonText: String? = null,
    onNegativeButtonClick: (() -> Unit) = {},
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside,
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
                ModalContent(
                    title = title,
                    body = body,
                    description = description,
                )
                ModalButtons(
                    onDismissRequest = onDismissRequest,
                    positiveButtonText = positiveButtonText,
                    onPositiveButtonClick = onPositiveButtonClick,
                    negativeButtonText = negativeButtonText,
                    onNegativeButtonClick = onNegativeButtonClick,
                )
            }
        }
    }
}

@Composable
private fun ModalContent(
    title: String,
    body: String,
    description: String?,
) {
    Column {
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
            color = NDGLTheme.colors.black900,
            textAlign = TextAlign.Center,
            style = NDGLTheme.typography.subtitleLgSemiBold,
        )
        Text(
            text = body,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            color = NDGLTheme.colors.black500,
            textAlign = TextAlign.Center,
            style = NDGLTheme.typography.bodyLgMedium,
        )
        description?.let {
            Text(
                text = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                color = NDGLTheme.colors.black400,
                textAlign = TextAlign.Center,
                style = NDGLTheme.typography.bodyMdMedium,
            )
        }
    }
}

@Composable
private fun ModalButtons(
    onDismissRequest: () -> Unit,
    positiveButtonText: String,
    onPositiveButtonClick: () -> Unit,
    negativeButtonText: String?,
    onNegativeButtonClick: (() -> Unit),
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (negativeButtonText != null) {
            NDGLCTAButton(
                type = NDGLCTAButtonAttr.Type.SECONDARY,
                size = NDGLCTAButtonAttr.Size.MEDIUM,
                status = NDGLCTAButtonAttr.Status.ACTIVE,
                label = negativeButtonText,
                onClick = {
                    onNegativeButtonClick()
                    onDismissRequest()
                },
                modifier = Modifier.weight(1f),
            )
        }
        NDGLCTAButton(
            type = NDGLCTAButtonAttr.Type.PRIMARY,
            size = NDGLCTAButtonAttr.Size.MEDIUM,
            status = NDGLCTAButtonAttr.Status.ACTIVE,
            label = positiveButtonText,
            onClick = {
                onPositiveButtonClick()
                onDismissRequest()
            },
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NDGLModalWithDescriptionPreview() {
    NDGLTheme {
        NDGLModal(
            onDismissRequest = {},
            title = "장소 변경",
            body = "장소를 다음과 같이 변경할까요?",
            description = "젤라테리아 파씨 (Gelateria Fassi) → 콜로세움 (colosseo)",
            negativeButtonText = "취소",
            onNegativeButtonClick = {},
            positiveButtonText = "변경하기",
            onPositiveButtonClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NDGLModalWithoutDescriptionPreview() {
    NDGLTheme {
        NDGLModal(
            onDismissRequest = {},
            title = "장소 변경",
            body = "장소를 다음과 같이 변경할까요?",
            positiveButtonText = "변경하기",
            onPositiveButtonClick = {},
        )
    }
}
