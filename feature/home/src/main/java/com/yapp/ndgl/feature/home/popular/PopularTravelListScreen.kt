package com.yapp.ndgl.feature.home.popular

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yapp.ndgl.core.ui.CommonErrorView
import com.yapp.ndgl.core.ui.designsystem.NDGLCTAButton
import com.yapp.ndgl.core.ui.designsystem.NDGLCTAButtonAttr
import com.yapp.ndgl.core.ui.designsystem.NDGLChipTab
import com.yapp.ndgl.core.ui.designsystem.NDGLChipTabAttr
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationBar
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationBarAttr
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationIcon
import com.yapp.ndgl.core.ui.designsystem.NDGLSnackbar
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.data.travel.model.ProgramType
import com.yapp.ndgl.feature.home.R
import com.yapp.ndgl.feature.home.component.TravelTemplate
import com.yapp.ndgl.feature.home.model.TravelContent
import com.yapp.ndgl.feature.home.model.TravelProgramTab
import com.yapp.ndgl.feature.home.popular.PopularTravelListSideEffect.ShowSnackBar.Type
import com.yapp.ndgl.feature.home.popular.PopularTravelListState.Success.PopularTravelListItem
import com.yapp.ndgl.feature.home.util.toIconRes
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import com.yapp.ndgl.core.ui.R as CoreR

@Composable
internal fun PopularTravelListRoute(
    viewModel: PopularTravelListViewModel = hiltViewModel(),
    goBack: () -> Unit,
    navigateToTemplateSearch: () -> Unit,
    navigateToFollowTravel: (Long, Int) -> Unit,
) {
    val state by viewModel.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val errUnknownMessage = stringResource(CoreR.string.common_err_unknown)

    PopularTravelListScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        goBack = { goBack() },
        onSearchClick = {
            viewModel.onIntent(PopularTravelListIntent.ClickSearchTravelTemplate)
        },
        onTabSelected = { index ->
            viewModel.onIntent(PopularTravelListIntent.SelectPopularTravelTab(index))
        },
        onTravelClick = { travelId, days ->
            viewModel.onIntent(PopularTravelListIntent.ClickTravel(travelId, days))
        },
        onLoadMore = { nextPage ->
            viewModel.onIntent(PopularTravelListIntent.LoadMore(nextPage))
        },
        onRetryClick = {
            viewModel.onIntent(PopularTravelListIntent.ClickRetry)
        },
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            PopularTravelListSideEffect.NavigateToSearchTravelTemplate -> navigateToTemplateSearch()
            is PopularTravelListSideEffect.NavigateToFollowTravel -> navigateToFollowTravel(
                sideEffect.travelId,
                sideEffect.days,
            )

            is PopularTravelListSideEffect.ShowSnackBar -> {
                val message = when (sideEffect.type) {
                    Type.ERR_UNKNOWN -> errUnknownMessage
                }
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message)
                }
            }
        }
    }
}

@Composable
private fun PopularTravelListScreen(
    state: PopularTravelListState,
    snackbarHostState: SnackbarHostState,
    goBack: () -> Unit,
    onSearchClick: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onTravelClick: (Long, Int) -> Unit,
    onLoadMore: (Int) -> Unit,
    onRetryClick: () -> Unit,
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
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                NDGLSnackbar(
                    modifier = Modifier.padding(bottom = 24.dp),
                    snackbarData = data,
                )
            }
        },
    ) { innerPadding ->
        when (state) {
            PopularTravelListState.Loading -> Unit
            is PopularTravelListState.Success -> PopularTravelListContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                state = state,
                onTabSelected = onTabSelected,
                onTravelClick = onTravelClick,
                onLoadMore = onLoadMore,
            )

            PopularTravelListState.Error -> ErrorView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                onRetryClick = onRetryClick,
            )
        }
    }
}

@Composable
private fun PopularTravelListContent(
    modifier: Modifier,
    state: PopularTravelListState.Success,
    onTabSelected: (Int) -> Unit,
    onTravelClick: (Long, Int) -> Unit,
    onLoadMore: (Int) -> Unit,
) {
    Column(
        modifier = modifier,
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
                key = { item ->
                    when (item) {
                        is PopularTravelListItem.Travel -> item.travelContent.travelId
                        is PopularTravelListItem.Loading -> "loading_${item.nextPage}"
                    }
                },
            ) { item ->
                when (item) {
                    is PopularTravelListItem.Travel -> TravelTemplate(
                        travel = item.travelContent,
                        onTravelTemplateClick = onTravelClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                    )

                    is PopularTravelListItem.Loading -> LoadingItem(
                        nextPage = item.nextPage,
                        onLoadMore = onLoadMore,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingItem(
    nextPage: Int,
    onLoadMore: (Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = NDGLTheme.colors.green500)
    }

    LaunchedEffect(Unit) {
        onLoadMore(nextPage)
    }
}

@Composable
private fun ErrorView(
    modifier: Modifier,
    onRetryClick: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(modifier = modifier) {
        CommonErrorView(
            modifier = Modifier
                .verticalScroll(scrollState)
                .weight(1f),
        )
        NDGLCTAButton(
            type = NDGLCTAButtonAttr.Type.PRIMARY,
            size = NDGLCTAButtonAttr.Size.LARGE,
            status = NDGLCTAButtonAttr.Status.ACTIVE,
            label = stringResource(CoreR.string.common_retry),
            onClick = onRetryClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
        )
    }
}

@Preview
@Composable
private fun PopularTravelListScreenPreview() {
    val travelContent = TravelContent(
        travelId = 1L,
        title = "Sample Travel",
        country = "US",
        countryName = "미국",
        city = "뉴욕",
        nights = 2,
        days = 3,
        programName = "Sample Program",
        programType = ProgramType.YOUTUBE,
        thumbnail = "",
    )

    val state = PopularTravelListState.Success(
        travelProgramTabs = persistentListOf(
            TravelProgramTab.All,
            TravelProgramTab.Custom(1L, "Youtube", ProgramType.YOUTUBE),
        ),
        allPopularTravels = persistentListOf(
            PopularTravelListItem.Travel(travelContent),
            PopularTravelListItem.Travel(travelContent.copy(travelId = 2L)),
            PopularTravelListItem.Travel(travelContent.copy(travelId = 3L)),
        ),
        popularTravelsByProgram = persistentMapOf(),
    )

    NDGLTheme {
        PopularTravelListScreen(
            state = state,
            snackbarHostState = remember { SnackbarHostState() },
            goBack = {},
            onSearchClick = {},
            onTabSelected = {},
            onTravelClick = { _, _ -> },
            onLoadMore = {},
            onRetryClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingItemPreview() {
    NDGLTheme {
        LoadingItem(
            nextPage = 1,
            onLoadMore = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorViewPreview() {
    NDGLTheme {
        ErrorView(
            modifier = Modifier.fillMaxSize(),
            onRetryClick = {},
        )
    }
}
