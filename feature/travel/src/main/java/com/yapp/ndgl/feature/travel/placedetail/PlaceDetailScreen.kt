package com.yapp.ndgl.feature.travel.placedetail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.designsystem.NDGLModal
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationBar
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationBarAttr
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.core.ui.util.launchBrowser
import com.yapp.ndgl.feature.travel.placedetail.component.PlaceDetailTabRow
import com.yapp.ndgl.feature.travel.placedetail.component.PlaceInfoTab
import com.yapp.ndgl.feature.travel.placedetail.component.PlacePhotoTab

@Composable
internal fun PlaceDetailRoute(
    viewModel: PlaceDetailViewModel = hiltViewModel(),
    innerPadding: PaddingValues,
    navigateBack: () -> Unit,
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is PlaceDetailSideEffect.NavigateToBrowser -> context.launchBrowser(sideEffect.url)
        }
    }

    PlaceDetailScreen(
        state = state,
        clickBackButton = navigateBack,
        innerPadding = innerPadding,
        selectTab = { viewModel.onIntent(PlaceDetailIntent.SelectTab(it)) },
        clickChangePlace = { viewModel.onIntent(PlaceDetailIntent.ClickChangePlace(it)) },
        confirmChangePlace = { viewModel.onIntent(PlaceDetailIntent.ConfirmChangePlace) },
        dismissChangeModal = { viewModel.onIntent(PlaceDetailIntent.DismissChangeModal) },
        clickAddress = { viewModel.onIntent(PlaceDetailIntent.ClickAddress) },
        clickMenu = { viewModel.onIntent(PlaceDetailIntent.ClickMenu) },
        clickAddScheduleButton = { viewModel.onIntent(PlaceDetailIntent.ClickAddScheduleButton) },
    )
}

@Composable
private fun PlaceDetailScreen(
    state: PlaceDetailState,
    clickBackButton: () -> Unit,
    innerPadding: PaddingValues,
    selectTab: (PlaceDetailTab) -> Unit,
    clickChangePlace: (AlternativePlace) -> Unit,
    confirmChangePlace: () -> Unit,
    dismissChangeModal: () -> Unit,
    clickAddress: () -> Unit,
    clickMenu: () -> Unit,
    clickAddScheduleButton: () -> Unit,
) {
    val placeInfo = state.placeInfo
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val thumbnailHeight = 230.dp
    val navBarSectionHeight = 48.dp
    val thumbnailHeightPx = with(density) { thumbnailHeight.toPx() }
    val navBarSectionHeightPx = with(density) { navBarSectionHeight.toPx() }
    val maxCollapseHeightPx = navBarSectionHeightPx + thumbnailHeightPx

    var collapseOffset by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            // 아래로 스크롤 시 LazyColumn 보다 먼저 헤더를 축소
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < 0f) {
                    val oldOffset = collapseOffset
                    collapseOffset = (collapseOffset - available.y).coerceIn(0f, maxCollapseHeightPx)
                    return Offset(0f, -(collapseOffset - oldOffset))
                }
                return Offset.Zero
            }

            // 위로 스크롤 시 LazyColumn이 끝까지 올라간 후 헤더를 다시 펼침
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                if (available.y > 0f) {
                    val oldOffset = collapseOffset
                    collapseOffset = (collapseOffset - available.y).coerceIn(0f, maxCollapseHeightPx)
                    return Offset(0f, oldOffset - collapseOffset)
                }
                return Offset.Zero
            }
        }
    }

    val navBarProgress = (1f - collapseOffset / navBarSectionHeightPx).coerceIn(0f, 1f)
    val thumbnailProgress = (1f - (collapseOffset - navBarSectionHeightPx).coerceAtLeast(0f) / thumbnailHeightPx).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NDGLTheme.colors.white)
            .padding(innerPadding),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection),
        ) {
            if (navBarProgress > 0f) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((navBarSectionHeight * navBarProgress).coerceAtLeast(0.dp))
                        .clipToBounds(),
                ) {
                    NDGLNavigationBar(
                        textAlignType = NDGLNavigationBarAttr.TextAlignType.CENTER,
                        leadingIcon = R.drawable.ic_28_chevron_left,
                        onLeadingIconClick = clickBackButton,
                    )
                    Spacer(Modifier.height(20.dp))
                }
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .background(NDGLTheme.colors.white)
                    .padding(horizontal = 24.dp)
                    .padding(top = if (navBarProgress == 0f) 8.dp else 0.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(placeInfo.name, color = NDGLTheme.colors.black800, style = NDGLTheme.typography.titleMdSemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Icon(imageVector = ImageVector.vectorResource(placeInfo.placeType.iconRes), contentDescription = null, tint = Color.Unspecified)
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = NDGLTheme.colors.black500)) {
                                // TODO: API 응답이 정해지면 하드코딩 제거
                                append("식당 • $20~40 • 리뷰 ${placeInfo.rating}")
                            }
                            withStyle(style = SpanStyle(color = NDGLTheme.colors.black200)) {
                                append("(${placeInfo.formattedRatingCount})")
                            }
                        },
                        style = NDGLTheme.typography.bodyMdMedium,
                    )
                }
            }
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .background(NDGLTheme.colors.white),
            )

            if (thumbnailProgress > 0f) {
                AsyncImage(
                    model = state.placeInfo.thumbnail,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(thumbnailHeight * thumbnailProgress)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop,
                )
            }

            Column(Modifier.background(NDGLTheme.colors.white)) {
                PlaceDetailTabRow(
                    selectedTab = state.selectedTab,
                    onTabSelected = selectTab,
                )
                HorizontalDivider(thickness = 1.dp, color = NDGLTheme.colors.black200)
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f),
                state = listState,
            ) {
                when (state.selectedTab) {
                    PlaceDetailTab.INFO -> {
                        item {
                            Spacer(Modifier.height(24.dp))
                            PlaceInfoTab(
                                placeInfo = state.placeInfo,
                                clickAddress = clickAddress,
                                clickMenu = clickMenu,
                                onChangePlaceClick = clickChangePlace,
                            )
                        }
                    }

                    PlaceDetailTab.PHOTO -> {
                        val (leftPhotos, rightPhotos) = state.photos.foldIndexed(
                            initial = mutableListOf<PlacePhoto>() to mutableListOf<PlacePhoto>(),
                        ) { index, lists, photo ->
                            if (index % 2 == 0) {
                                lists.first.add(photo)
                            } else {
                                lists.second.add(photo)
                            }
                            lists
                        }

                        item {
                            Spacer(Modifier.height(20.dp))
                            PlacePhotoTab(leftPhotos = leftPhotos, rightPhotos = rightPhotos)
                        }
                    }
                }

                item { Spacer(Modifier.height(60.dp)) }
            }
        }
    }

    if (state.showChangeModal && state.selectedAlternativePlace != null) {
        NDGLModal(
            onDismissRequest = dismissChangeModal,
            title = stringResource(R.string.place_detail_modal_change_title),
            body = stringResource(R.string.place_detail_modal_change_body, state.selectedAlternativePlace.name),
            description = stringResource(
                R.string.place_detail_modal_change_description,
                state.placeInfo.name,
                state.selectedAlternativePlace.name,
            ),
            positiveButtonText = stringResource(R.string.place_detail_modal_change_confirm),
            onPositiveButtonClick = confirmChangePlace,
            negativeButtonText = stringResource(R.string.place_detail_modal_change_cancel),
            onNegativeButtonClick = dismissChangeModal,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceDetailScreenPreview() {
    NDGLTheme {
        PlaceDetailScreen(
            state = PlaceDetailState(
                placeInfo = PlaceInfo(
                    id = "",
                    name = "젤라테리아 파씨",
                    placeType = PlaceType.RESTAURANT,
                    address = "로마 비아 프린시페",
                    phoneNumber = "+39 06 446 4740",
                    openingHours = "매일 01:00~23:00",
                    websiteUrl = "https://example.com",
                    creatorName = "빠니보틀",
                    rating = 4.7,
                    userRatingCount = 3971,
                ),
            ),
            clickBackButton = {},
            innerPadding = PaddingValues(),
            selectTab = {},
            clickChangePlace = {},
            confirmChangePlace = {},
            dismissChangeModal = {},
            clickAddress = {},
            clickMenu = {},
            clickAddScheduleButton = {},
        )
    }
}
