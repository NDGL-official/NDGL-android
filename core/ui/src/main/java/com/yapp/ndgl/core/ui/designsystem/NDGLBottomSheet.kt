@file:OptIn(ExperimentalMaterial3Api::class)

package com.yapp.ndgl.core.ui.designsystem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import kotlinx.coroutines.launch

@Composable
fun NDGLBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    showDragHandle: Boolean = true,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val hideSheet: () -> Unit = {
        coroutineScope.launch {
            sheetState.hide()
            onDismissRequest()
        }
    }

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = if (showDragHandle) {
            { NDGLBottomSheetDragHandle() }
        } else {
            null
        },
        containerColor = NDGLTheme.colors.white,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        title?.let { title ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp, horizontal = 24.dp),
            ) {
                Text(
                    title,
                    color = NDGLTheme.colors.black400,
                    style = NDGLTheme.typography.bodyLgMedium,
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_24_close),
                    contentDescription = null,
                    tint = NDGLTheme.colors.black600,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(shape = CircleShape)
                        .clickable { hideSheet() },
                )
            }
        }

        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun NDGLBottomSheetWithHandlePreview() {
    NDGLTheme {
        NDGLBottomSheet(
            onDismissRequest = {},
            showDragHandle = true,
            title = "바텀시트 제목",
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            ) {
                Text(
                    text = "바텀시트 내용입니다.",
                    style = NDGLTheme.typography.bodyLgMedium,
                    color = NDGLTheme.colors.black500,
                )
            }
        }
    }
}

@Composable
fun NDGLBottomSheetDragHandle() {
    BottomSheetDefaults.DragHandle(width = 56.dp, height = 5.dp, color = NDGLTheme.colors.black400)
}

@Preview(showBackground = true)
@Composable
private fun NDGLBottomSheetWithoutHandlePreview() {
    NDGLTheme {
        NDGLBottomSheet(
            onDismissRequest = {},
            showDragHandle = false,
            title = "바텀시트 제목",
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            ) {
                Text(
                    text = "바텀시트 내용입니다.",
                    style = NDGLTheme.typography.bodyLgMedium,
                    color = NDGLTheme.colors.black500,
                )
            }
        }
    }
}
