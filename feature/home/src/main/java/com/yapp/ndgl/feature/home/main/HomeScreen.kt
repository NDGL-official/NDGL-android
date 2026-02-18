package com.yapp.ndgl.feature.home.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationBar
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationBarAttr
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationIcon
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.data.travel.model.PlaceCategory
import com.yapp.ndgl.data.travel.model.ProgramType
import com.yapp.ndgl.feature.home.model.TravelContent
import java.time.LocalDate

@Composable
internal fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToTemplateSearch: () -> Unit,
    navigateToFollowTravel: (Long, Int) -> Unit,
) {
    val state by viewModel.collectAsState()

    HomeScreen(
        state = state,
        onSearchClick = {
            viewModel.onIntent(HomeIntent.ClickSearchTravelTemplate)
        },
        onTabSelected = { index ->
            viewModel.onIntent(HomeIntent.SelectPopularTravelTab(index))
        },
        onTravelClick = { travelId ->
            viewModel.onIntent(HomeIntent.ClickTravel(travelId))
        },
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            HomeSideEffect.NavigateToSearchTravelTemplate -> navigateToTemplateSearch()
            is HomeSideEffect.NavigateToFollowTravel -> navigateToFollowTravel(sideEffect.travelId, sideEffect.days)
        }
    }
}

@Composable
private fun HomeScreen(
    state: HomeState,
    onSearchClick: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onTravelClick: (Long) -> Unit,
) {
    Scaffold(
        topBar = {
            NDGLNavigationBar(
                textAlignType = NDGLNavigationBarAttr.TextAlignType.START,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = NDGLTheme.colors.white)
                    .statusBarsPadding(),
                trailingContents = {
                    NDGLNavigationIcon(
                        icon = R.drawable.ic_28_search,
                        onClick = onSearchClick,
                    )
                    NDGLNavigationIcon(
                        icon = R.drawable.ic_28_settings,
                        onClick = { /* FIXME: 설정 */ },
                    )
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 20.dp,
                bottom = innerPadding.calculateBottomPadding() + 100.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(40.dp),
        ) {
            item {
                UpcomingTravelCardSection(
                    modifier = Modifier.fillMaxWidth(),
                    myTravel = state.myTravel,
                )
            }

            item {
                if (state.filteredPopularTravels.isNotEmpty()) {
                    PopularTravelSection(
                        tabs = state.travelProgramTabs,
                        selectedTabIndex = state.popularTravelSelectedTabIndex,
                        travels = state.filteredPopularTravels,
                        onTabSelected = onTabSelected,
                        onTravelClick = onTravelClick,
                    )
                }
            }

            if (state.recommendedContents.isNotEmpty()) {
                item {
                    RecommendedContentSection(
                        userName = state.userName,
                        contents = state.recommendedContents,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
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
            thumbnail = "https://picsum.photos/200/300",
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
            thumbnail = "https://picsum.photos/200/300",
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
            thumbnail = "https://picsum.photos/200/300",
        ),
    )

    NDGLTheme {
        HomeScreen(
            state = HomeState(
                userName = "유저123",
                myTravel = HomeState.MyTravel.InProgress(
                    title = "인도 여행",
                    dayCount = 1,
                    startDate = LocalDate.of(2024, 12, 23),
                    endDate = LocalDate.of(2024, 12, 26),
                    currentPlace = HomeState.TravelPlace(
                        category = PlaceCategory.TRANSPORT,
                        estimatedDuration = 60,
                        name = "인도 국제 공항",
                        thumbnailUrl = "",
                    ),
                ),
                travelProgramTabs = listOf(
                    HomeState.TravelProgramTab.All,
                    HomeState.TravelProgramTab.Custom(
                        programId = 1,
                        name = "빠니보틀",
                        type = ProgramType.YOUTUBE,
                    ),
                    HomeState.TravelProgramTab.Custom(
                        programId = 2,
                        name = "곽튜브",
                        type = ProgramType.YOUTUBE,
                    ),
                    HomeState.TravelProgramTab.Custom(
                        programId = 3,
                        name = "콩콩팡팡",
                        type = ProgramType.TV,
                    ),
                ),
                allPopularTravels = sampleTravels,
            ),
            onSearchClick = {},
            onTabSelected = {},
            onTravelClick = {},
        )
    }
}
