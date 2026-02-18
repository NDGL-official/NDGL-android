package com.yapp.ndgl.feature.home.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yapp.ndgl.core.ui.designsystem.NDGLCTAButton
import com.yapp.ndgl.core.ui.designsystem.NDGLCTAButtonAttr
import com.yapp.ndgl.core.ui.designsystem.NDGLSearchNavigationBar
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.data.travel.model.ProgramType
import com.yapp.ndgl.feature.home.R
import com.yapp.ndgl.feature.home.component.TravelTemplate
import com.yapp.ndgl.feature.home.model.TravelContent
import com.yapp.ndgl.core.ui.R as CoreR

@Composable
internal fun TemplateSearchRoute(
    viewModel: TemplateSearchViewModel = hiltViewModel(),
    goBack: () -> Unit,
    navigateToFollowTravel: (Long, Int) -> Unit,
) {
    val state by viewModel.collectAsState()

    TemplateSearchScreen(
        state = state,
        onBackClick = { goBack() },
        onSearchKeywordChange = { keyword ->
            viewModel.onIntent(TemplateSearchIntent.UpdateSearchKeyword(keyword))
        },
        onSearch = { keyword ->
            viewModel.onIntent(TemplateSearchIntent.SearchTemplate(keyword))
        },
        onTravelTemplateClick = { travelId ->
            viewModel.onIntent(TemplateSearchIntent.ClickTravelTemplate(travelId))
        },
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is TemplateSearchSideEffect.NavigateToFollowTravel -> navigateToFollowTravel(
                sideEffect.travelId,
                sideEffect.days,
            )
        }
    }
}

@Composable
private fun TemplateSearchScreen(
    state: TemplateSearchState,
    onBackClick: () -> Unit,
    onSearchKeywordChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onTravelTemplateClick: (Long) -> Unit,
) {
    Scaffold(
        topBar = {
            NDGLSearchNavigationBar(
                searchKeyword = state.searchKeyword,
                onSearchKeywordChange = onSearchKeywordChange,
                onSearch = onSearch,
                onBackClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = NDGLTheme.colors.white)
                    .statusBarsPadding(),
                placeholder = stringResource(R.string.home_template_search_placeholder),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    top = 18.dp,
                    bottom = 80.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                when (state.searchResult) {
                    TemplateSearchState.SearchResult.Idle -> item { InitialEmptyView() }
                    TemplateSearchState.SearchResult.Empty -> item { EmptyResultView() }
                    is TemplateSearchState.SearchResult.Success -> items(
                        items = state.searchResult.travels,
                        key = { travelTemplate -> travelTemplate.travelId },
                    ) { travel ->
                        TravelTemplate(
                            travel = travel,
                            onTravelTemplateClick = onTravelTemplateClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                        )
                    }

                    TemplateSearchState.SearchResult.Error -> item { ErrorView() }
                }
            }

            if (state.searchResult == TemplateSearchState.SearchResult.Error) {
                NDGLCTAButton(
                    type = NDGLCTAButtonAttr.Type.PRIMARY,
                    size = NDGLCTAButtonAttr.Size.LARGE,
                    status = NDGLCTAButtonAttr.Status.ACTIVE,
                    label = stringResource(CoreR.string.common_retry),
                    onClick = { onSearch(state.searchKeyword) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun InitialEmptyView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 165.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(CoreR.drawable.img_empty_suitcase),
            contentDescription = null,
            modifier = Modifier.size(100.dp),
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.home_template_search_idle_title),
                color = NDGLTheme.colors.black500,
                textAlign = TextAlign.Center,
                style = NDGLTheme.typography.subtitleMdSemiBold,
            )
            Text(
                text = stringResource(R.string.home_template_search_idle_description),
                color = NDGLTheme.colors.black400,
                textAlign = TextAlign.Center,
                style = NDGLTheme.typography.bodyLgRegular,
            )
        }
    }
}

@Composable
private fun EmptyResultView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 165.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(CoreR.drawable.img_empty_browser),
            contentDescription = null,
            modifier = Modifier.size(100.dp),
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.home_template_search_empty_title),
                color = NDGLTheme.colors.black500,
                textAlign = TextAlign.Center,
                style = NDGLTheme.typography.subtitleMdSemiBold,
            )
            Text(
                text = stringResource(R.string.home_template_search_empty_description),
                color = NDGLTheme.colors.black400,
                textAlign = TextAlign.Center,
                style = NDGLTheme.typography.bodyLgRegular,
            )
        }
    }
}

@Composable
private fun ErrorView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 165.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(CoreR.drawable.img_empty_browser),
            contentDescription = null,
            modifier = Modifier.size(100.dp),
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.home_template_search_error_title),
                color = NDGLTheme.colors.black500,
                textAlign = TextAlign.Center,
                style = NDGLTheme.typography.subtitleMdSemiBold,
            )
            Text(
                text = stringResource(R.string.home_template_search_error_description),
                color = NDGLTheme.colors.black400,
                textAlign = TextAlign.Center,
                style = NDGLTheme.typography.bodyLgRegular,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InitialEmptyViewPreview() {
    NDGLTheme {
        InitialEmptyView()
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyResultViewPreview() {
    NDGLTheme {
        EmptyResultView()
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorViewPreview() {
    NDGLTheme {
        ErrorView()
    }
}

@Preview(showBackground = true)
@Composable
private fun TemplateSearchScreenFilledPreview() {
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
        TemplateSearchScreen(
            state = TemplateSearchState(
                searchKeyword = "뉴욕",
                searchResult = TemplateSearchState.SearchResult.Success(
                    travels = sampleTravels,
                ),
            ),
            onBackClick = {},
            onSearchKeywordChange = {},
            onSearch = {},
            onTravelTemplateClick = {},
        )
    }
}
