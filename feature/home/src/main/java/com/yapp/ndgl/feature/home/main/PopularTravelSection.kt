package com.yapp.ndgl.feature.home.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.yapp.ndgl.core.ui.designsystem.NDGLChipTab
import com.yapp.ndgl.core.ui.designsystem.NDGLChipTabAttr
import com.yapp.ndgl.core.ui.designsystem.NDGLOutlinedButton
import com.yapp.ndgl.core.ui.designsystem.NDGLOutlinedButtonAttr
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.core.util.FlagEmojiUtil.toFlagEmoji
import com.yapp.ndgl.data.travel.model.TravelSummary
import com.yapp.ndgl.feature.home.R
import kotlinx.collections.immutable.toPersistentList
import com.yapp.ndgl.core.ui.R as CoreR

@Composable
internal fun PopularTravelSection(
    tabs: List<HomeState.PopularTravelTab>,
    selectedTabIndex: Int,
    travelsByTab: Map<String, List<TravelSummary>>,
    onTabSelected: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(
            text = stringResource(R.string.home_popular_travel_section_title),
            style = NDGLTheme.typography.subtitleLgSemiBold,
            color = NDGLTheme.colors.black900,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        )

        HorizontalCardSection(
            tabs = tabs,
            selectedTabIndex = selectedTabIndex,
            travelsByTab = travelsByTab,
            onTabSelected = onTabSelected,
        )

        NDGLOutlinedButton(
            status = NDGLOutlinedButtonAttr.Status.ACTIVE,
            label = stringResource(R.string.home_popular_travel_more_button),
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        )
    }
}

@Composable
private fun HorizontalCardSection(
    tabs: List<HomeState.PopularTravelTab>,
    selectedTabIndex: Int,
    travelsByTab: Map<String, List<TravelSummary>>,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(
        initialPage = selectedTabIndex,
        pageCount = { tabs.size },
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        NDGLChipTab(
            tabs = tabs.map { tab ->
                NDGLChipTabAttr.Tab(
                    tag = tab.tag,
                    name = tab.name,
                    leadingIcon = tab.icon,
                )
            }.toPersistentList(),
            selectedIndex = selectedTabIndex,
            onTabSelected = onTabSelected,
            modifier = Modifier.padding(start = 24.dp),
        )

        HorizontalPager(
            state = pagerState,
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 24.dp),
            pageSize = PageSize.Fixed(310.dp),
            pageSpacing = 8.dp,
        ) { page ->
            val tabTag = tabs.getOrNull(page)?.tag ?: return@HorizontalPager
            val travels = travelsByTab[tabTag] ?: emptyList()
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                travels.take(3).forEach { travel ->
                    PopularTravelItem(travel = travel)
                }
            }
        }
    }

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    LaunchedEffect(pagerState.settledPage) {
        if (pagerState.settledPage != selectedTabIndex) {
            onTabSelected(pagerState.settledPage)
        }
    }
}

@Composable
private fun PopularTravelItem(
    travel: TravelSummary,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        AsyncImage(
            model = travel.youtube.thumbnail,
            contentDescription = travel.youtube.title,
            modifier = Modifier
                .width(140.dp)
                .height(88.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop,
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = travel.country.toFlagEmoji(),
                modifier = Modifier.wrapContentSize(),
                style = NDGLTheme.typography.bodyLgMedium,
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = travel.youtube.title,
                    style = NDGLTheme.typography.bodyLgMedium,
                    color = NDGLTheme.colors.black900,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = travel.city,
                        style = NDGLTheme.typography.bodyMdMedium,
                        color = NDGLTheme.colors.black400,
                    )
                    Text(
                        text = stringResource(R.string.home_common_dot_separator),
                        style = NDGLTheme.typography.bodyMdMedium,
                        color = NDGLTheme.colors.black400,
                    )
                    Text(
                        text = stringResource(R.string.home_popular_travel_nights_days, travel.nights, travel.days),
                        style = NDGLTheme.typography.bodyMdRegular,
                        color = NDGLTheme.colors.black400,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PopularTravelSectionPreview() {
    val sampleTravels = listOf(
        TravelSummary(
            travelId = "1",
            country = "FR",
            city = "파리",
            nights = 7,
            days = 9,
            youtube = TravelSummary.YoutubeInfo(
                title = "곽준빈의 신혼여행",
                youtuber = "곽튜브",
                thumbnail = "",
            ),
        ),
        TravelSummary(
            travelId = "2",
            country = "CH",
            city = "스위스",
            nights = 5,
            days = 6,
            youtube = TravelSummary.YoutubeInfo(
                title = "스위스 여행",
                youtuber = "빠니보틀",
                thumbnail = "",
            ),
        ),
        TravelSummary(
            travelId = "3",
            country = "DK",
            city = "덴마크",
            nights = 4,
            days = 6,
            youtube = TravelSummary.YoutubeInfo(
                title = "충격적인 북유럽 물가",
                youtuber = "곽튜브",
                thumbnail = "",
            ),
        ),
    )

    NDGLTheme {
        PopularTravelSection(
            tabs = listOf(
                HomeState.PopularTravelTab(tag = "all", name = "전체"),
                HomeState.PopularTravelTab(tag = "ppanibottle", name = "빠니보틀", icon = CoreR.drawable.ic_20_video),
                HomeState.PopularTravelTab(tag = "gwaktube", name = "곽튜브", icon = CoreR.drawable.ic_20_video),
            ),
            selectedTabIndex = 0,
            travelsByTab = mapOf(
                "all" to sampleTravels,
                "ppanibottle" to sampleTravels.filter { it.youtube.youtuber == "빠니보틀" },
                "gwaktube" to sampleTravels.filter { it.youtube.youtuber == "곽튜브" },
            ),
            onTabSelected = {},
        )
    }
}
