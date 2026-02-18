package com.yapp.ndgl.feature.travel.addplace.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.core.util.formatString
import com.yapp.ndgl.feature.travel.addplace.AddPlaceInfo
import com.yapp.ndgl.feature.travel.model.PlaceType
import com.yapp.ndgl.feature.travel.placedetail.component.PlaceInfoRow
import kotlin.time.Duration.Companion.hours

@Composable
internal fun AddPlaceInfoTab(
    placeInfo: AddPlaceInfo,
    clickAddress: () -> Unit,
    clickMenu: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (placeInfo.address != null) {
            PlaceInfoRow(
                iconRes = R.drawable.ic_24_pin,
                onClick = clickAddress,
            ) {
                Text(
                    placeInfo.address,
                    color = NDGLTheme.colors.black700,
                    style = NDGLTheme.typography.bodyMdMedium,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        if (placeInfo.websiteUrl != null) {
            PlaceInfoRow(
                iconRes = R.drawable.ic_24_book,
                onClick = clickMenu,
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(R.string.place_detail_menu), color = NDGLTheme.colors.black700, style = NDGLTheme.typography.bodyMdMedium)
                    Text(placeInfo.websiteUrl, color = NDGLTheme.colors.black500, style = NDGLTheme.typography.bodyMdMedium)
                }
            }
        }
        if (placeInfo.phoneNumber != null) {
            PlaceInfoRow(iconRes = R.drawable.ic_24_phone) {
                Text(
                    placeInfo.phoneNumber,
                    color = NDGLTheme.colors.black700,
                    style = NDGLTheme.typography.bodyMdMedium,
                )
            }
        }
        PlaceInfoRow(iconRes = R.drawable.ic_24_clock) {
            Text(
                stringResource(R.string.estimated_duration_format, placeInfo.estimatedDuration.formatString()),
                color = NDGLTheme.colors.black700,
                style = NDGLTheme.typography.bodyMdMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddPlaceInfoTabPreview() {
    NDGLTheme {
        AddPlaceInfoTab(
            placeInfo = AddPlaceInfo(
                id = "",
                name = "젤라테리아 파씨",
                placeType = PlaceType.RESTAURANT,
                address = "로마 비아 프린시페",
                phoneNumber = "+39 06 446 4740",
                websiteUrl = "https://example.com",
                estimatedDuration = 2.hours,
            ),
            clickAddress = {},
            clickMenu = {},
        )
    }
}
