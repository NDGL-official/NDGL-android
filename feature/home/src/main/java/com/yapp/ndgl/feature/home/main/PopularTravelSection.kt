package com.yapp.ndgl.feature.home.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.ndgl.core.ui.designsystem.NDGLChipTab
import com.yapp.ndgl.core.ui.designsystem.NDGLChipTabAttr
import com.yapp.ndgl.core.ui.designsystem.NDGLOutlinedButton
import com.yapp.ndgl.core.ui.designsystem.NDGLOutlinedButtonAttr
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.data.travel.model.ProgramType
import com.yapp.ndgl.feature.home.R
import com.yapp.ndgl.feature.home.component.TravelTemplate
import com.yapp.ndgl.feature.home.model.TravelContent
import com.yapp.ndgl.feature.home.model.TravelProgramTab
import com.yapp.ndgl.feature.home.util.toIconRes
import kotlinx.collections.immutable.toPersistentList
import com.yapp.ndgl.core.ui.R as CoreR

private const val COLUMN_ITEM_COUNT = 3

@Composable
internal fun PopularTravelSection(
    tabs: List<TravelProgramTab>,
    selectedTabIndex: Int,
    travels: List<TravelContent>,
    onTabSelected: (Int) -> Unit,
    onTravelClick: (Long, Int) -> Unit,
    onTravelMoreClick: () -> Unit,
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
            onTravelClick = onTravelClick,
        )

        NDGLOutlinedButton(
            status = NDGLOutlinedButtonAttr.Status.ACTIVE,
            label = stringResource(R.string.home_popular_travel_more_button),
            onClick = onTravelMoreClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        )
    }
}

@Composable
private fun HorizontalCardSection(
    tabs: List<TravelProgramTab>,
    selectedTabIndex: Int,
    travels: List<TravelContent>,
    onTabSelected: (Int) -> Unit,
    onTravelClick: (Long, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val columns = travels.chunked(COLUMN_ITEM_COUNT)
    val pagerState = rememberPagerState(
        pageCount = { columns.size.coerceAtLeast(1) },
    )

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        NDGLChipTab(
            tabs = tabs.map { tab ->
                when (tab) {
                    TravelProgramTab.All -> NDGLChipTabAttr.Tab(
                        tag = "All",
                        name = stringResource(CoreR.string.common_all),
                    )

                    is TravelProgramTab.Custom -> NDGLChipTabAttr.Tab(
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
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    columnItems.forEach { travel ->
                        PopularTravelItem(
                            travel = travel,
                            onTravelClick = onTravelClick,
                        )
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
    travel: TravelContent,
    onTravelClick: (Long, Int) -> Unit,
) {
    TravelTemplate(
        travel = travel,
        onTravelTemplateClick = { travelId, _ -> onTravelClick(travelId, travel.days) },
    )
}

@Preview(showBackground = true)
@Composable
private fun PopularTravelSectionPreview() {
    val sampleTravels = listOf(
        TravelContent(
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
        TravelContent(
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
        TravelContent(
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
                TravelProgramTab.All,
                TravelProgramTab.Custom(
                    programId = 1,
                    name = "빠니보틀",
                    type = ProgramType.YOUTUBE,
                ),
                TravelProgramTab.Custom(
                    programId = 2,
                    name = "곽튜브",
                    type = ProgramType.TV,
                ),
            ),
            selectedTabIndex = 0,
            travels = sampleTravels,
            onTabSelected = {},
            onTravelClick = { _, _ -> },
            onTravelMoreClick = {},
        )
    }
}
