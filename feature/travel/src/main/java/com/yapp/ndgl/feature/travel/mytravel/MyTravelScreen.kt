package com.yapp.ndgl.feature.travel.mytravel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationBar
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationBarAttr.TextAlignType
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationIcon
import com.yapp.ndgl.core.ui.theme.NDGLTheme

@Composable
internal fun MyTravelRoute(
    viewModel: MyTravelViewModel = hiltViewModel(),
    navigateToFollowTravel: (Long, Int) -> Unit,
    navigateToTravelDetail: (Long) -> Unit,
    navigateToTravelPlace: (String) -> Unit,
) {
    val state by viewModel.collectAsState()

    MyTravelScreen(
        state = state,
        onTravelClick = { travelId ->
            viewModel.onIntent(MyTravelIntent.ClickTravelDetail(travelId = travelId))
        },
        onPlaceClick = { placeId ->
            viewModel.onIntent(MyTravelIntent.ClickPlaceDetail(placeId = placeId))
        },
        onNewTravelFindClick = {
            viewModel.onIntent(MyTravelIntent.ClickFindNewTravel)
        },
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MyTravelSideEffect.NavigateToFollowTravel -> navigateToFollowTravel(
                sideEffect.travelId,
                sideEffect.days,
            )

            is MyTravelSideEffect.NavigateToTravelDetail -> navigateToTravelDetail(
                sideEffect.travelId,
            )

            is MyTravelSideEffect.NavigateToTravelPlace -> navigateToTravelPlace(
                sideEffect.placeId,
            )

            MyTravelSideEffect.NavigateToPopularTravelList -> {
                // FIXME: navigate to popular travel list
            }
        }
    }
}

@Composable
private fun MyTravelScreen(
    state: MyTravelState,
    onTravelClick: (Long) -> Unit,
    onPlaceClick: (String) -> Unit,
    onNewTravelFindClick: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            NDGLNavigationBar(
                textAlignType = TextAlignType.START,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = NDGLTheme.colors.white)
                    .statusBarsPadding(),
                trailingContents = {
                    NDGLNavigationIcon(
                        icon = R.drawable.ic_28_search,
                        onClick = { /* FIXME: 홈 검색 */ },
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
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                top = 20.dp,
                bottom = 100.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (state.upcomingTravel != null) {
                item {
                    UpcomingTravelCardSection(
                        modifier = Modifier.fillMaxWidth(),
                        upcomingTravel = state.upcomingTravel,
                        onTravelClick = onTravelClick,
                        onPlaceClick = onPlaceClick,
                    )
                }
            }
            item {
                UpcomingTravelListSection(
                    upcomingTravels = state.upcomingTravels,
                    onUserTravelClick = onTravelClick,
                    onNewTravelFindClick = onNewTravelFindClick,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyTravelScreenPreview() {
    NDGLTheme {
        MyTravelScreen(
            state = MyTravelState(),
            onTravelClick = {},
            onPlaceClick = {},
            onNewTravelFindClick = {},
        )
    }
}
