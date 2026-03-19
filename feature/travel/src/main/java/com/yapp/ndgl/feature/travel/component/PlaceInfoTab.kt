package com.yapp.ndgl.feature.travel.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.core.ui.util.noRippleClickable
import com.yapp.ndgl.core.util.formatString
import com.yapp.ndgl.feature.travel.model.AlternativePlace
import com.yapp.ndgl.feature.travel.model.PlaceInfo
import com.yapp.ndgl.feature.travel.model.PlaceType
import com.yapp.ndgl.feature.travel.model.TipContent
import kotlin.time.Duration.Companion.hours

@Composable
internal fun PlaceInfoTab(
    placeInfo: PlaceInfo,
    onAddressClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onAlternativePlaceClick: (String) -> Unit = {},
    onChangePlaceClick: ((AlternativePlace) -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (placeInfo.address != null) {
                PlaceInfoRow(
                    iconRes = R.drawable.ic_24_pin,
                    onClick = onAddressClick,
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
                    onClick = onMenuClick,
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

        if (placeInfo.tipContent != null && !placeInfo.tipContent.tips.isNullOrEmpty() || !placeInfo.alternativePlaces.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                thickness = 1.dp,
                color = NDGLTheme.colors.black200,
            )
        }

        if (placeInfo.tipContent != null && !placeInfo.tipContent.tips.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            PlaceTipCard(tipContent = placeInfo.tipContent)
        }

        if (!placeInfo.alternativePlaces.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(stringResource(R.string.place_detail_plan_b_message), color = NDGLTheme.colors.black700, style = NDGLTheme.typography.bodyLgSemiBold)
            Spacer(modifier = Modifier.height(16.dp))
            AlternativePlaceContent(
                alternativePlaces = placeInfo.alternativePlaces,
                onClick = onAlternativePlaceClick,
                onChangePlaceClick = onChangePlaceClick,
            )
        }
    }
}

@Composable
private fun PlaceInfoRow(
    iconRes: Int,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.noRippleClickable { onClick() } else Modifier),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(iconRes),
            contentDescription = null,
            tint = NDGLTheme.colors.green500,
        )
        content()
        if (onClick != null) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                imageVector = ImageVector.vectorResource(R.drawable.ic_24_chevron_right),
                tint = NDGLTheme.colors.black600,
                contentDescription = null,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceInfoTabPreview() {
    NDGLTheme {
        PlaceInfoTab(
            placeInfo = PlaceInfo(
                name = "젤라테리아 파씨",
                address = "로마 비아 프린시페",
                phoneNumber = "+39 06 446 4740",
                websiteUrl = "https://example.com",
                estimatedDuration = 2.hours,
                tipContent = TipContent(
                    creatorName = "빠니보틀",
                    tips = listOf(
                        "젤라또는 오후 3시쯤 먹는 게 가장 맛있어요",
                        "피스타치오와 헤이즐넛 맛을 꼭 드셔보세요",
                        "웨이팅이 길 수 있으니 평일 방문을 추천해요",
                    ),
                ),
                alternativePlaces = listOf(
                    AlternativePlace(
                        id = "",
                        name = "젤라또 디 산 크리스피노",
                        thumbnail = "",
                        placeType = PlaceType.CAFE,
                    ),
                    AlternativePlace(
                        id = "",
                        name = "지올리티",
                        thumbnail = "",
                        placeType = PlaceType.CAFE,
                    ),
                ),
            ),
        )
    }
}
