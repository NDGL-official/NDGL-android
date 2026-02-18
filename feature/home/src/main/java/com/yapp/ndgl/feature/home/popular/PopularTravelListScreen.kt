package com.yapp.ndgl.feature.home.popular

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yapp.ndgl.core.ui.designsystem.NDGLChipTab
import com.yapp.ndgl.core.ui.designsystem.NDGLChipTabAttr
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationBar
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationBarAttr
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationIcon
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.feature.home.R
import com.yapp.ndgl.feature.home.component.TravelTemplate
import com.yapp.ndgl.feature.home.model.TravelProgramTab
import com.yapp.ndgl.feature.home.util.toIconRes
import kotlinx.collections.immutable.toPersistentList
import com.yapp.ndgl.core.ui.R as CoreR

@Composable
internal fun PopularTravelListRoute(
    viewModel: PopularTravelListViewModel = hiltViewModel(),
    goBack: () -> Unit,
    navigateToTemplateSearch: () -> Unit,
    navigateToFollowTravel: (Long, Int) -> Unit,
) {
    val state by viewModel.collectAsState()

    PopularTravelListScreen(
        state = state,
        goBack = { goBack() },
        onSearchClick = {
            viewModel.onIntent(PopularTravelListIntent.ClickSearchTravelTemplate)
        },
        onTabSelected = { index ->
            viewModel.onIntent(PopularTravelListIntent.SelectPopularTravelTab(index))
        },
        onTravelClick = { travelId ->
            viewModel.onIntent(PopularTravelListIntent.ClickTravel(travelId))
        },
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            PopularTravelListSideEffect.NavigateToSearchTravelTemplate -> navigateToTemplateSearch()
            is PopularTravelListSideEffect.NavigateToFollowTravel -> navigateToFollowTravel(
                sideEffect.travelId,
                sideEffect.days,
            )
        }
    }
}

@Composable
private fun PopularTravelListScreen(
    state: PopularTravelListState,
    goBack: () -> Unit,
    onSearchClick: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onTravelClick: (Long) -> Unit,
) {
    Scaffold(
        topBar = {
            NDGLNavigationBar(
                textAlignType = NDGLNavigationBarAttr.TextAlignType.CENTER,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = NDGLTheme.colors.white)
                    .statusBarsPadding(),
                headline = stringResource(R.string.home_popular_travel_section_title),
                leadingIcon = CoreR.drawable.ic_28_chevron_left,
                onLeadingIconClick = goBack,
                trailingContents = {
                    NDGLNavigationIcon(
                        icon = CoreR.drawable.ic_28_search,
                        onClick = onSearchClick,
                    )
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            NDGLChipTab(
                tabs = state.travelProgramTabs.map { tab ->
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
                selectedIndex = state.selectedTabIndex,
                onTabSelected = onTabSelected,
                modifier = Modifier.padding(start = 24.dp, top = 20.dp),
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = state.selectedProgramTravels,
                    key = { travelTemplate -> travelTemplate.travelId },
                ) { travel ->
                    TravelTemplate(
                        travel = travel,
                        onTravelTemplateClick = onTravelClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                    )
                }
            }
        }
    }
}
