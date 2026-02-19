package com.yapp.ndgl.feature.travel.followtravel.placedetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import coil3.compose.AsyncImage
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationBar
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationBarAttr
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.core.ui.util.launchBrowser
import com.yapp.ndgl.feature.travel.component.PlaceDetailTabRow
import com.yapp.ndgl.feature.travel.component.PlaceInfoTab
import com.yapp.ndgl.feature.travel.component.PlacePhotoTab
import com.yapp.ndgl.feature.travel.model.AlternativePlace
import com.yapp.ndgl.feature.travel.model.PlaceDetailTab
import com.yapp.ndgl.feature.travel.model.PlaceInfo
import com.yapp.ndgl.feature.travel.model.PlacePhoto
import com.yapp.ndgl.feature.travel.model.PlaceType
import com.yapp.ndgl.feature.travel.model.Price
import com.yapp.ndgl.feature.travel.model.PriceRange
import com.yapp.ndgl.feature.travel.model.TipContent

@Composable
internal fun FollowPlaceDetailRoute(
    viewModel: FollowPlaceDetailViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToAlternativePlaceDetail: (String) -> Unit,
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is FollowPlaceDetailSideEffect.NavigateToBrowser -> context.launchBrowser(sideEffect.url)
            is FollowPlaceDetailSideEffect.NavigateToAlternativePlaceDetail -> navigateToAlternativePlaceDetail(sideEffect.googlePlaceId)
        }
    }

    FollowPlaceDetailScreen(
        state = state,
        clickBackButton = navigateBack,
        selectTab = { viewModel.onIntent(FollowPlaceDetailIntent.SelectTab(it)) },
        clickAddress = { viewModel.onIntent(FollowPlaceDetailIntent.ClickAddress) },
        clickMenu = { viewModel.onIntent(FollowPlaceDetailIntent.ClickMenu) },
        clickAlternativePlace = { viewModel.onIntent(FollowPlaceDetailIntent.ClickAlternativePlace(it)) },
    )
}

@Composable
private fun FollowPlaceDetailScreen(
    state: FollowPlaceDetailState,
    clickBackButton: () -> Unit,
    selectTab: (PlaceDetailTab) -> Unit,
    clickAddress: () -> Unit,
    clickMenu: () -> Unit,
    clickAlternativePlace: (String) -> Unit,
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

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(placeInfo.placeType.iconRes),
                            contentDescription = null,
                            tint = Color.Unspecified,
                        )
                        val placeTypeLabel = stringResource(placeInfo.placeType.labelRes)
                        val reviewLabel = if (placeInfo.rating != null) {
                            stringResource(R.string.place_detail_review_format, placeInfo.rating)
                        } else {
                            null
                        }
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = NDGLTheme.colors.black500)) {
                                    append(placeTypeLabel)
                                    placeInfo.priceRange?.let { priceRange ->
                                        append(" • " + priceRange.formattedPriceRange)
                                    }
                                    if (reviewLabel != null) append(" • $reviewLabel")
                                }
                                if (placeInfo.userRatingCount != null) {
                                    withStyle(style = SpanStyle(color = NDGLTheme.colors.black300)) {
                                        append(" (${placeInfo.formattedRatingCount})")
                                    }
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

                PlaceDetailTabRow(
                    selectedTab = state.selectedTab,
                    onTabSelected = selectTab,
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    state = listState,
                ) {
                    when (state.selectedTab) {
                        PlaceDetailTab.INFO -> {
                            item {
                                Spacer(Modifier.height(24.dp))
                                PlaceInfoTab(
                                    placeInfo = state.placeInfo,
                                    onAddressClick = clickAddress,
                                    onMenuClick = clickMenu,
                                    onAlternativePlaceClick = clickAlternativePlace,
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
    }
}

@Preview(showBackground = true)
@Composable
private fun FollowPlaceDetailScreenPreview() {
    NDGLTheme {
        FollowPlaceDetailScreen(
            state = FollowPlaceDetailState(
                placeInfo = PlaceInfo(
                    name = "젤라테리아 파씨 (Gelateria Fassi)",
                    placeType = PlaceType.RESTAURANT,
                    address = "Via Principe Eugenio, 65, 00185 Roma RM, Italy",
                    phoneNumber = "+39 06 446 4740",
                    websiteUrl = "https://www.gelateriafassi.com",
                    rating = 4.7,
                    userRatingCount = 12450,
                    priceRange = PriceRange(
                        startPrice = Price(currencyCode = "EUR", units = "5", symbol = "€"),
                        endPrice = Price(currencyCode = "EUR", units = "15", symbol = "€"),
                    ),
                    tipContent = TipContent(
                        creatorName = "빠니보틀",
                        tips = listOf(
                            "젤라또는 오후 3시쯤 먹는 게 가장 맛있어요",
                            "피스타치오와 헤이즐넛 맛을 꼭 드셔보세요",
                            "웨이팅이 길 수 있으니 평일 방문을 추천해요",
                        ),
                    ),
                    alternativePlaces = listOf(
                        AlternativePlace(
                            id = "",
                            name = "젤라또 디 산 크리스피노",
                            thumbnail = "",
                            placeType = PlaceType.CAFE,
                        ),
                        AlternativePlace(
                            id = "",
                            name = "지올리티",
                            thumbnail = "",
                            placeType = PlaceType.CAFE,
                        ),
                    ),
                ),
            ),
            clickBackButton = {},
            selectTab = {},
            clickAddress = {},
            clickMenu = {},
            clickAlternativePlace = {},
        )
    }
}
