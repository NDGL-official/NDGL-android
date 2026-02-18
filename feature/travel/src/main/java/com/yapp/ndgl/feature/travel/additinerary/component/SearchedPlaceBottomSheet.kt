package com.yapp.ndgl.feature.travel.additinerary.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.designsystem.NDGLBottomSheetDragHandle
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationBar
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationBarAttr
import com.yapp.ndgl.core.ui.designsystem.NDGLNonModalBottomSheet
import com.yapp.ndgl.core.ui.designsystem.rememberNDGLNonModalBottomSheetState
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.core.ui.util.dropShadow
import com.yapp.ndgl.core.ui.util.noRippleClickable
import com.yapp.ndgl.core.util.formatString
import com.yapp.ndgl.feature.travel.additinerary.PlaceInfo
import com.yapp.ndgl.feature.travel.additinerary.SelectedPlaceDetail
import com.yapp.ndgl.feature.travel.model.PlaceDetailTab
import com.yapp.ndgl.feature.travel.model.PlacePhoto
import com.yapp.ndgl.feature.travel.model.PlaceType
import com.yapp.ndgl.feature.travel.model.Price
import com.yapp.ndgl.feature.travel.model.PriceRange
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.hours

@Immutable
enum class SearchedPlaceBottomSheetValue {
    Expanded,
    PartiallyExpanded,
    Hidden,
}

@Composable
internal fun SearchedPlaceBottomSheet(
    initialValue: SearchedPlaceBottomSheetValue = SearchedPlaceBottomSheetValue.PartiallyExpanded,
    selectedPlaceDetail: SelectedPlaceDetail,
    clickBack: () -> Unit,
    clickAddress: () -> Unit,
    clickMenu: () -> Unit,
    bookmarkPlace: (String) -> Unit,
    clickAddItinerary: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val placeInfo = selectedPlaceDetail.placeInfo
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val screenHeightPx = windowInfo.containerSize.height.toFloat()
    val anchors = DraggableAnchors {
        SearchedPlaceBottomSheetValue.Hidden at screenHeightPx * 1f
        SearchedPlaceBottomSheetValue.PartiallyExpanded at screenHeightPx * 0.45f
        SearchedPlaceBottomSheetValue.Expanded at screenHeightPx * 0f
    }
    val sheetState = rememberNDGLNonModalBottomSheetState(
        initialValue = initialValue,
        anchors,
    )
    val isExpanded = sheetState.anchoredDraggableState.currentValue == SearchedPlaceBottomSheetValue.Expanded

    BackHandler {
        val currentSheetValue = sheetState.anchoredDraggableState.currentValue
        scope.launch {
            if (currentSheetValue == SearchedPlaceBottomSheetValue.Expanded) {
                sheetState.anchoredDraggableState.animateTo(SearchedPlaceBottomSheetValue.PartiallyExpanded)
            } else {
                clickBack()
            }
        }
    }

    NDGLNonModalBottomSheet(sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isExpanded) {
                        Modifier
                    } else {
                        Modifier
                            .dropShadow(
                                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                                color = Color.Black.copy(alpha = 0.06f),
                                blur = 20.dp,
                                offsetY = (-10).dp,
                            )
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    },
                )
                .background(NDGLTheme.colors.white),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                if (isExpanded) {
                    NDGLNavigationBar(
                        modifier = Modifier.statusBarsPadding(),
                        textAlignType = NDGLNavigationBarAttr.TextAlignType.CENTER,
                        leadingIcon = R.drawable.ic_28_chevron_down,
                        onLeadingIconClick = {
                            scope.launch {
                                sheetState.anchoredDraggableState.animateTo(SearchedPlaceBottomSheetValue.PartiallyExpanded)
                            }
                        },
                    )
                } else {
                    NDGLBottomSheetDragHandle()
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                val density = LocalDensity.current
                val thumbnailHeight = 230.dp
                val navBarSectionHeight = 48.dp
                val maxCollapseHeightPx = with(density) { (navBarSectionHeight + thumbnailHeight).toPx() }

                var collapseOffset by remember { mutableFloatStateOf(0f) }
                var selectedTab by remember { mutableStateOf(PlaceDetailTab.INFO) }

                val nestedScrollConnection = remember {
                    object : NestedScrollConnection {
                        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                            if (available.y < 0f) {
                                val oldOffset = collapseOffset
                                collapseOffset = (collapseOffset - available.y).coerceIn(0f, maxCollapseHeightPx)
                                return Offset(0f, -(collapseOffset - oldOffset))
                            }
                            return Offset.Zero
                        }

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

                val thumbnailProgress = (
                    1f - (collapseOffset - with(density) { navBarSectionHeight.toPx() })
                        .coerceAtLeast(0f) / with(density) { thumbnailHeight.toPx() }
                    ).coerceIn(0f, 1f)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(nestedScrollConnection),
                ) {
                    SearchedPlaceInfoHeader(isExpanded = isExpanded, placeInfo = placeInfo, bookmarkPlace = bookmarkPlace)
                    Spacer(
                        Modifier
                            .height(20.dp)
                            .background(NDGLTheme.colors.white),
                    )

                    if (thumbnailProgress > 0f) {
                        AsyncImage(
                            model = placeInfo.thumbnail,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(thumbnailHeight * thumbnailProgress)
                                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                                .background(Color.LightGray),
                            contentScale = ContentScale.Crop,
                        )
                    }

                    SearchedPlaceDetailTabRow(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
                    HorizontalDivider(thickness = 1.dp, color = NDGLTheme.colors.black200)

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        when (selectedTab) {
                            PlaceDetailTab.INFO -> item {
                                Spacer(Modifier.height(24.dp))
                                SearchedPlaceInfoTab(placeInfo, clickAddress, clickMenu)
                            }

                            PlaceDetailTab.PHOTO -> {
                                val (leftPhotos, rightPhotos) = selectedPlaceDetail.photos.foldIndexed(
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
                                    SearchedPlacePhotoTab(leftPhotos = leftPhotos, rightPhotos = rightPhotos)
                                }
                            }
                        }
                        item { Spacer(Modifier.height(60.dp)) }
                    }
                }
            }
            AddItineraryButton(clickAddItinerary = clickAddItinerary)
            Spacer(modifier = Modifier.height(with(density) { sheetState.anchoredDraggableState.offset.toDp() }))
        }
    }
}

@Composable
private fun SearchedPlaceInfoHeader(isExpanded: Boolean, placeInfo: PlaceInfo, bookmarkPlace: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = placeInfo.name,
                style = NDGLTheme.typography.titleMdSemiBold,
                color = NDGLTheme.colors.black800,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (!isExpanded) {
                Icon(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable {
                            bookmarkPlace(placeInfo.id)
                        },
                    imageVector = ImageVector.vectorResource(if (placeInfo.isBookMarked) R.drawable.ic_28_star_fill else R.drawable.ic_28_star),
                    contentDescription = null,
                    tint = Color.Unspecified,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchedPlaceDetailTabRow(
    selectedTab: PlaceDetailTab,
    onTabSelected: (PlaceDetailTab) -> Unit,
) {
    val tabs = PlaceDetailTab.entries
    val selectedIndex = tabs.indexOf(selectedTab)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
    ) {
        SecondaryTabRow(
            selectedTabIndex = selectedIndex,
            modifier = Modifier.fillMaxWidth(),
            containerColor = NDGLTheme.colors.white,
            contentColor = NDGLTheme.colors.black900,
            indicator = {},
            divider = {},
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = index == selectedIndex
                Tab(
                    selected = isSelected,
                    onClick = { onTabSelected(tab) },
                    modifier = Modifier.background(
                        if (isSelected) NDGLTheme.colors.black100 else NDGLTheme.colors.white,
                    ),
                    text = {
                        Text(
                            stringResource(tab.titleRes),
                            color = NDGLTheme.colors.black600,
                            style = NDGLTheme.typography.bodyMdSemiBold,
                            textAlign = TextAlign.Center,
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun SearchedPlaceInfoTab(
    placeInfo: PlaceInfo,
    clickAddress: () -> Unit,
    clickMenu: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (placeInfo.address != null) {
            SearchedPlaceInfoRow(
                iconRes = R.drawable.ic_24_pin,
                onClick = clickAddress,
            ) {
                Text(
                    text = placeInfo.address,
                    style = NDGLTheme.typography.bodyMdMedium,
                    color = NDGLTheme.colors.black700,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        if (placeInfo.websiteUrl != null) {
            SearchedPlaceInfoRow(
                iconRes = R.drawable.ic_24_book,
                onClick = clickMenu,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = stringResource(R.string.place_detail_menu),
                        style = NDGLTheme.typography.bodyMdMedium,
                        color = NDGLTheme.colors.black700,
                    )
                    Text(
                        text = placeInfo.websiteUrl,
                        style = NDGLTheme.typography.bodyMdMedium,
                        color = NDGLTheme.colors.black500,
                        maxLines = 1,
                    )
                }
            }
        }

        if (placeInfo.phoneNumber != null) {
            SearchedPlaceInfoRow(iconRes = R.drawable.ic_24_phone) {
                Text(
                    text = placeInfo.phoneNumber,
                    style = NDGLTheme.typography.bodyMdMedium,
                    color = NDGLTheme.colors.black700,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        SearchedPlaceInfoRow(iconRes = R.drawable.ic_24_clock) {
            Text(
                text = stringResource(
                    R.string.estimated_duration_format,
                    placeInfo.estimatedDuration.formatString(),
                ),
                style = NDGLTheme.typography.bodyMdMedium,
                color = NDGLTheme.colors.black700,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SearchedPlaceInfoRow(
    iconRes: Int,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.noRippleClickable { onClick() } else Modifier),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(iconRes),
            contentDescription = null,
            tint = NDGLTheme.colors.green500,
            modifier = Modifier.size(24.dp),
        )
        content()
        if (onClick != null) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .clickable { onClick() },
                imageVector = ImageVector.vectorResource(R.drawable.ic_24_chevron_right),
                tint = NDGLTheme.colors.black600,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun SearchedPlacePhotoTab(leftPhotos: List<PlacePhoto>, rightPhotos: List<PlacePhoto>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            leftPhotos.forEach { photo ->
                AsyncImage(
                    model = photo.url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(photo.aspectRatio)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop,
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            rightPhotos.forEach { photo ->
                AsyncImage(
                    model = photo.url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(photo.aspectRatio)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}

private val previewPlaceDetail = SelectedPlaceDetail(
    PlaceInfo(
        id = "1",
        name = "콜로세움",
        placeType = PlaceType.ATTRACTION,
        rating = 4.8,
        userRatingCount = 12450,
        address = "Piazza del Colosseo, 1, 00184 Roma RM, Italy",
        phoneNumber = "+39 06 3996 7700",
        openingHours = "매일 09:00~19:00",
        websiteUrl = "https://www.colosseo.it",
        estimatedDuration = 2.hours,
        priceRange = PriceRange(
            startPrice = Price(currencyCode = "EUR", units = "5", symbol = "€"),
            endPrice = Price(currencyCode = "EUR", units = "15", symbol = "€"),
        ),
    ),
)

@Preview(name = "SearchedPlace - Expanded", showBackground = true)
@Composable
private fun SearchedPlaceBottomSheet_Expanded_Preview() {
    NDGLTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            SearchedPlaceBottomSheet(
                selectedPlaceDetail = previewPlaceDetail,
                clickBack = {},
                clickAddress = {},
                clickMenu = {},
                bookmarkPlace = {},
                clickAddItinerary = {},
                initialValue = SearchedPlaceBottomSheetValue.Expanded,
            )
        }
    }
}

@Preview(name = "SearchedPlace - PartiallyExpanded", showBackground = true)
@Composable
private fun SearchedPlaceBottomSheet_PartiallyExpanded_Preview() {
    NDGLTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            SearchedPlaceBottomSheet(
                selectedPlaceDetail = previewPlaceDetail,
                clickBack = {},
                clickAddress = {},
                clickMenu = {},
                bookmarkPlace = {},
                clickAddItinerary = {},
                initialValue = SearchedPlaceBottomSheetValue.PartiallyExpanded,
            )
        }
    }
}
