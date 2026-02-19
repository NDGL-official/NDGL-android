package com.yapp.ndgl.feature.travel.component

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.feature.travel.model.TipContent

@Composable
internal fun PlaceTipCard(tipContent: TipContent) {
    val creatorName = tipContent.creatorName
    val tips = tipContent.tips
    require(tips != null)
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
                        .background(NDGLTheme.colors.black400.copy(alpha = if (index == pagerState.currentPage) 1f else 0.6f)),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceTipCardPreview() {
    val tipContent = TipContent(
        creatorName = "빠니보틀",
        tips = listOf(
            "젤라또는 오후 3시쯤 먹는 게 가장 맛있어요",
            "피스타치오와 헤이즐넛 맛을 꼭 드셔보세요",
            "웨이팅이 길 수 있으니 평일 방문을 추천해요",
        ),
    )

    NDGLTheme {
        PlaceTipCard(tipContent)
    }
}
