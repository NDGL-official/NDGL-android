package com.yapp.ndgl.feature.home.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.core.util.FlagEmojiUtil.toFlagEmoji
import com.yapp.ndgl.data.travel.model.ProgramType
import com.yapp.ndgl.feature.home.R
import com.yapp.ndgl.feature.home.model.TravelContent
import com.yapp.ndgl.feature.home.util.toIconRes

@Composable
internal fun RecommendedContentSection(
    userName: String,
    contents: List<TravelContent>,
    onTravelClick: (Long, Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(
            text = stringResource(R.string.home_recommended_content_section_title, userName),
            style = NDGLTheme.typography.subtitleLgSemiBold,
            color = NDGLTheme.colors.black900,
            modifier = Modifier.padding(horizontal = 24.dp),
        )

        val lazyListState = rememberLazyListState()
        LazyRow(
            state = lazyListState,
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            flingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState),
        ) {
            items(
                items = contents,
                key = { it.travelId },
            ) { travel ->
                RecommendedContentCard(
                    travel = travel,
                    onTravelClick = onTravelClick,
                )
            }
        }
    }
}

@Composable
private fun RecommendedContentCard(
    travel: TravelContent,
    onTravelClick: (Long, Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .width(240.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onTravelClick(travel.travelId, travel.days) },
    ) {
        AsyncImage(
            model = travel.thumbnail,
            contentDescription = travel.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            contentScale = ContentScale.Crop,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(NDGLTheme.colors.white)
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            CountryChip(
                country = travel.country,
                city = travel.city,
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = travel.title,
                    color = NDGLTheme.colors.black700,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = NDGLTheme.typography.bodyLgSemiBold,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(travel.programType.toIconRes()),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = NDGLTheme.colors.black400,
                    )
                    Text(
                        text = travel.programName,
                        style = NDGLTheme.typography.bodyMdMedium,
                        color = NDGLTheme.colors.black400,
                    )
                    Text(
                        text = stringResource(R.string.home_common_dot_separator),
                        style = NDGLTheme.typography.bodyMdMedium,
                        color = NDGLTheme.colors.black400,
                    )
                    Text(
                        text = stringResource(
                            R.string.home_popular_travel_nights_days,
                            travel.nights,
                            travel.days,
                        ),
                        style = NDGLTheme.typography.bodyMdMedium,
                        color = NDGLTheme.colors.black400,
                    )
                }
            }
        }
    }
}

@Composable
private fun CountryChip(
    country: String,
    city: String,
) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = country.toFlagEmoji(),
            style = NDGLTheme.typography.bodySmSemiBold,
        )
        Text(
            text = city,
            style = NDGLTheme.typography.bodyMdMedium,
            color = NDGLTheme.colors.black400,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecommendedContentSectionPreview() {
    NDGLTheme {
        RecommendedContentSection(
            userName = "유저123",
            onTravelClick = { _, _ -> },
            contents = listOf(
                TravelContent(
                    travelId = 1,
                    title = "곽준빈의 신혼여행",
                    country = "FR",
                    countryName = "프랑스",
                    city = "파리",
                    nights = 7,
                    days = 9,
                    programName = "곽튜브",
                    programType = ProgramType.YOUTUBE,
                    thumbnail = "",
                ),
                TravelContent(
                    travelId = 2,
                    title = "스위스 여행",
                    country = "CH",
                    countryName = "스위스",
                    city = "스위스",
                    nights = 5,
                    days = 6,
                    programName = "빠니보틀",
                    programType = ProgramType.YOUTUBE,
                    thumbnail = "",
                ),
            ),
        )
    }
}
