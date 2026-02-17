package com.yapp.ndgl.feature.travel.followtravel.placedetail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.core.util.formatString
import com.yapp.ndgl.feature.travel.followtravel.placedetail.FollowPlaceInfo
import com.yapp.ndgl.feature.travel.model.AlternativePlace
import com.yapp.ndgl.feature.travel.model.PlaceType
import com.yapp.ndgl.feature.travel.model.TipContent
import kotlin.collections.forEach
import kotlin.time.Duration.Companion.hours

@Composable
internal fun FollowPlaceInfoTab(
    placeInfo: FollowPlaceInfo,
    clickAddress: () -> Unit,
    clickMenu: () -> Unit,
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
                FollowPlaceInfoRow(
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
                FollowPlaceInfoRow(
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
                FollowPlaceInfoRow(iconRes = R.drawable.ic_24_phone) {
                    Text(
                        placeInfo.phoneNumber,
                        color = NDGLTheme.colors.black700,
                        style = NDGLTheme.typography.bodyMdMedium,
                    )
                }
            }
            FollowPlaceInfoRow(iconRes = R.drawable.ic_24_clock) {
                Text(
                    stringResource(R.string.estimated_duration_format, placeInfo.estimatedDuration.formatString()),
                    color = NDGLTheme.colors.black700,
                    style = NDGLTheme.typography.bodyMdMedium,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 24.dp),
            thickness = 1.dp,
            color = NDGLTheme.colors.black200,
        )

        if (placeInfo.tipContent != null) {
            val tipContent = placeInfo.tipContent
            Spacer(modifier = Modifier.height(32.dp))
            FollowPlaceTipsPager(tips = tipContent.tips, creatorName = tipContent.creatorName)
        }

        if (placeInfo.alternativePlaces.isNotEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(stringResource(R.string.place_detail_plan_b_message), color = NDGLTheme.colors.black700, style = NDGLTheme.typography.bodyLgSemiBold)
            Spacer(modifier = Modifier.height(16.dp))
            FollowAlternativePlaceContent(alternativePlaces = placeInfo.alternativePlaces)
        }
    }
}

@Composable
private fun FollowPlaceTipsPager(
    tips: List<String>,
    creatorName: String,
) {
    val pagerState = rememberPagerState(pageCount = { tips.size })

    Box(
        modifier = Modifier.wrapContentSize(),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 35.dp),
            pageSpacing = 10.dp,
        ) { page ->
            val tip = tips[page]
            val tipCardBrush = Brush.linearGradient(
                0.6f to NDGLTheme.colors.white.copy(alpha = 0.6f),
                0.8f to Color(0xFFFFFFF9).copy(alpha = 0.8f),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(218.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, NDGLTheme.colors.white, RoundedCornerShape(12.dp)),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_place_tip_card),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop,
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 20.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(34.dp))
                            .background(tipCardBrush)
                            .border(0.5.dp, NDGLTheme.colors.white, RoundedCornerShape(34.dp))
                            .padding(vertical = 4.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.place_detail_content_tip),
                            color = NDGLTheme.colors.black500,
                            style = NDGLTheme.typography.bodySmMedium,
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.place_detail_creator_tip_format, creatorName),
                        color = NDGLTheme.colors.black900,
                        style = NDGLTheme.typography.bodyLgSemiBold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("\"$tip\"", color = NDGLTheme.colors.black700, style = NDGLTheme.typography.bodyLgRegular)
                }
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(tips.size) { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            NDGLTheme.colors.black400.copy(alpha = if (index == pagerState.currentPage) 1f else 0.6f),
                        ),
                )
            }
        }
    }
}

@Composable
private fun FollowAlternativePlaceContent(
    alternativePlaces: List<AlternativePlace>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        alternativePlaces.forEach { alternativePlace ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, NDGLTheme.colors.black50, RoundedCornerShape(16.dp))
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = alternativePlace.thumbnail,
                    contentDescription = null,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop,
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            imageVector = ImageVector.vectorResource(alternativePlace.placeType.iconRes),
                            contentDescription = null,
                            tint = Color.Unspecified,
                        )
                        Text(
                            stringResource(alternativePlace.placeType.labelRes),
                            color = NDGLTheme.colors.black400,
                            style = NDGLTheme.typography.bodySmMedium,
                        )
                    }
                    Text(alternativePlace.name, color = NDGLTheme.colors.black700, style = NDGLTheme.typography.bodyMdSemiBold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FollowPlaceInfoTabPreview() {
    NDGLTheme {
        FollowPlaceInfoTab(
            placeInfo = FollowPlaceInfo(
                id = "",
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
            clickAddress = {},
            clickMenu = {},
        )
    }
}
