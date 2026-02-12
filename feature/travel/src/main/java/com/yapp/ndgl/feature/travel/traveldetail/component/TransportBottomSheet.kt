package com.yapp.ndgl.feature.travel.traveldetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.designsystem.NDGLBottomSheet
import com.yapp.ndgl.core.ui.designsystem.NDGLCTAButton
import com.yapp.ndgl.core.ui.designsystem.NDGLCTAButtonAttr
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.core.ui.util.noRippleClickable
import com.yapp.ndgl.core.util.formatDistance
import com.yapp.ndgl.core.util.formatString
import com.yapp.ndgl.feature.travel.traveldetail.TransportSegment
import com.yapp.ndgl.feature.travel.traveldetail.TransportType
import kotlin.time.Duration.Companion.minutes

@Composable
internal fun TransportBottomSheet(
    initialTransport: TransportSegment,
    availableTransports: List<TransportSegment>,
    onDismissRequest: () -> Unit,
    onConfirm: (TransportSegment) -> Unit,
) {
    var selectedTransport by remember { mutableStateOf(initialTransport) }
    val itemHeight = 56.dp
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                return available
            }
        }
    }

    NDGLBottomSheet(
        onDismissRequest = onDismissRequest,
        showDragHandle = false,
        title = stringResource(R.string.transport_bottom_sheet_title),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight * 4)
                    .nestedScroll(nestedScrollConnection),
            ) {
                itemsIndexed(availableTransports) { index, transport ->
                    val isSelected = selectedTransport == transport
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(if (isSelected) NDGLTheme.colors.black100 else Color.Transparent)
                            .noRippleClickable {
                                selectedTransport = transport
                            }
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = ImageVector.vectorResource(transport.type.iconRes),
                            contentDescription = stringResource(transport.type.labelRes),
                            tint = if (isSelected) NDGLTheme.colors.black600 else NDGLTheme.colors.black400,
                        )
                        Spacer(Modifier.width(32.dp))
                        Text(
                            stringResource(transport.type.labelRes),
                            color = if (isSelected) NDGLTheme.colors.black700 else NDGLTheme.colors.black400,
                            style = NDGLTheme.typography.subtitleMdSemiBold,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            transport.duration.formatString(),
                            color = if (isSelected) NDGLTheme.colors.black400 else NDGLTheme.colors.black300,
                            style = NDGLTheme.typography.bodyLgMedium,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "•",
                            color = if (isSelected) NDGLTheme.colors.black400 else NDGLTheme.colors.black300,
                            style = NDGLTheme.typography.bodyLgMedium,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            transport.distance.formatDistance(),
                            color = if (isSelected) NDGLTheme.colors.black400 else NDGLTheme.colors.black300,
                            style = NDGLTheme.typography.bodyLgMedium,
                        )
                    }
                }
            }
            NDGLCTAButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                type = NDGLCTAButtonAttr.Type.PRIMARY,
                size = NDGLCTAButtonAttr.Size.LARGE,
                status = NDGLCTAButtonAttr.Status.ACTIVE,
                label = stringResource(R.string.transport_bottom_sheet_button),
                onClick = {
                    onConfirm(selectedTransport)
                    onDismissRequest()
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TransportBottomSheetPreview() {
    // 1. Mock Data 준비
    val mockAvailableTransports = listOf(
        TransportSegment(TransportType.WALK, 15.minutes, 1200),
        TransportSegment(TransportType.CAR, 10.minutes, 5400),
        TransportSegment(TransportType.BUS, 25.minutes, 4800),
        TransportSegment(TransportType.TRAIN, 40.minutes, 12000),
        TransportSegment(TransportType.CAR, 5.minutes, 2000), // 스크롤 확인용 추가
    )

    NDGLTheme {
        Box(Modifier.fillMaxSize()) {
            TransportBottomSheet(
                initialTransport = mockAvailableTransports[0],
                availableTransports = mockAvailableTransports,
                onDismissRequest = {},
                onConfirm = {},
            )
        }
    }
}
