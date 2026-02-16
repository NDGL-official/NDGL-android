package com.yapp.ndgl.feature.home.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.yapp.ndgl.core.ui.designsystem.NDGLChipTab
import com.yapp.ndgl.core.ui.designsystem.NDGLChipTabAttr
import com.yapp.ndgl.core.ui.designsystem.NDGLOutlinedButton
import com.yapp.ndgl.core.ui.designsystem.NDGLOutlinedButtonAttr
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.core.util.FlagEmojiUtil.toFlagEmoji
import com.yapp.ndgl.data.travel.model.ProgramType
import com.yapp.ndgl.feature.home.R
import com.yapp.ndgl.feature.home.util.toIconRes
import kotlinx.collections.immutable.toPersistentList

private const val COLUMN_ITEM_COUNT = 3

@Composable
internal fun PopularTravelSection(
    tabs: List<HomeState.TravelProgramTab>,
    selectedTabIndex: Int,
    travels: List<HomeState.TravelContent>,
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
            travels = travels,
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
    tabs: List<HomeState.TravelProgramTab>,
    selectedTabIndex: Int,
    travels: List<HomeState.TravelContent>,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val columns = travels.chunked(COLUMN_ITEM_COUNT)
    val pagerState = rememberPagerState(
        pageCount = { columns.size.coerceAtLeast(1) },
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        NDGLChipTab(
            tabs = tabs.map { tab ->
                when (tab) {
                    HomeState.TravelProgramTab.All -> NDGLChipTabAttr.Tab(
                        tag = "All",
                        name = "전체",
                    )
                    is HomeState.TravelProgramTab.Custom -> NDGLChipTabAttr.Tab(
                        tag = tab.programId.toString(),
                        name = tab.name,
                        leadingIcon = tab.type.toIconRes(),
                    )
                }
            }.toPersistentList(),
            selectedIndex = selectedTabIndex,
            onTabSelected = onTabSelected,
            modifier = Modifier.padding(start = 24.dp),
        )

        if (columns.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                modifier = modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp),
                pageSize = PageSize.Fixed(310.dp),
                pageSpacing = 8.dp,
            ) { pageIndex ->
                val columnItems = columns[pageIndex]
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    columnItems.forEach { travel ->
                        PopularTravelItem(travel = travel)
                    }
                }
            }
        }
    }

    LaunchedEffect(selectedTabIndex) {
        pagerState.scrollToPage(0)
    }
}

@Composable
private fun PopularTravelItem(
    travel: HomeState.TravelContent,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        AsyncImage(
            model = travel.thumbnail,
            contentDescription = travel.title,
            modifier = Modifier
                .width(140.dp)
                .height(88.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop,
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = travel.country.toFlagEmoji(),
                    style = NDGLTheme.typography.bodyLgMedium,
                )
                Text(
                    text = travel.country,
                    color = NDGLTheme.colors.black400,
                    style = NDGLTheme.typography.bodyMdMedium,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = travel.title,
                    color = NDGLTheme.colors.black900,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = NDGLTheme.typography.bodyLgMedium,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = travel.city,
                        color = NDGLTheme.colors.black400,
                        style = NDGLTheme.typography.bodyMdMedium,
                    )
                    Text(
                        text = stringResource(R.string.home_common_dot_separator),
                        color = NDGLTheme.colors.black400,
                        style = NDGLTheme.typography.bodyMdMedium,
                    )
                    Text(
                        text = stringResource(
                            R.string.home_popular_travel_nights_days,
                            travel.nights,
                            travel.days,
                        ),
                        color = NDGLTheme.colors.black400,
                        style = NDGLTheme.typography.bodyMdRegular,
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
        HomeState.TravelContent(
            travelId = 1,
            title = "곽준빈의 신혼여행",
            country = "FR",
            city = "파리",
            nights = 7,
            days = 9,
            programName = "곽튜브",
            programType = ProgramType.YOUTUBE,
            thumbnail = "",
        ),
        HomeState.TravelContent(
            travelId = 2,
            title = "스위스 여행",
            country = "CH",
            city = "스위스",
            nights = 5,
            days = 6,
            programName = "빠니보틀",
            programType = ProgramType.TV,
            thumbnail = "",
        ),
        HomeState.TravelContent(
            travelId = 3,
            title = "충격적인 북유럽 물가",
            country = "DK",
            city = "덴마크",
            nights = 4,
            days = 6,
            programName = "곽튜브",
            programType = ProgramType.YOUTUBE,
            thumbnail = "",
        ),
    )

    NDGLTheme {
        PopularTravelSection(
            tabs = listOf(
                HomeState.TravelProgramTab.All,
                HomeState.TravelProgramTab.Custom(
                    programId = 1,
                    name = "빠니보틀",
                    type = ProgramType.YOUTUBE,
                ),
                HomeState.TravelProgramTab.Custom(
                    programId = 2,
                    name = "곽튜브",
                    type = ProgramType.TV,
                ),
            ),
            selectedTabIndex = 0,
            travels = sampleTravels,
            onTabSelected = {},
        )
    }
}
