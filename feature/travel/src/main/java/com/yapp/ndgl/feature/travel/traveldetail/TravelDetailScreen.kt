@file:OptIn(ExperimentalMaterial3Api::class)

package com.yapp.ndgl.feature.travel.traveldetail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.designsystem.NDGLBottomSheet
import com.yapp.ndgl.core.ui.designsystem.NDGLCTAButton
import com.yapp.ndgl.core.ui.designsystem.NDGLCTAButtonAttr
import com.yapp.ndgl.core.ui.designsystem.NDGLChipTab
import com.yapp.ndgl.core.ui.designsystem.NDGLChipTabAttr
import com.yapp.ndgl.core.ui.designsystem.NDGLInputModal
import com.yapp.ndgl.core.ui.designsystem.NDGLModal
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationBar
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationBarAttr
import com.yapp.ndgl.core.ui.designsystem.NDGLNavigationIcon
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.core.ui.util.dropShadow
import com.yapp.ndgl.core.ui.util.launchBrowser
import com.yapp.ndgl.core.ui.util.rememberReorderableState
import com.yapp.ndgl.core.ui.util.reorderable
import com.yapp.ndgl.feature.travel.model.AlternativePlace
import com.yapp.ndgl.feature.travel.model.Budget
import com.yapp.ndgl.feature.travel.model.ContentInfo
import com.yapp.ndgl.feature.travel.model.PlaceInfo
import com.yapp.ndgl.feature.travel.model.PlaceType
import com.yapp.ndgl.feature.travel.model.TipContent
import com.yapp.ndgl.feature.travel.model.TransportSegment
import com.yapp.ndgl.feature.travel.model.TransportType
import com.yapp.ndgl.feature.travel.model.VideoInfo
import com.yapp.ndgl.feature.travel.traveldetail.component.ContentCard
import com.yapp.ndgl.feature.travel.traveldetail.component.DurationPickerContent
import com.yapp.ndgl.feature.travel.traveldetail.component.EditControlBar
import com.yapp.ndgl.feature.travel.traveldetail.component.EditablePlaceItem
import com.yapp.ndgl.feature.travel.traveldetail.component.PlaceBottomSheet
import com.yapp.ndgl.feature.travel.traveldetail.component.PlaceItem
import com.yapp.ndgl.feature.travel.traveldetail.component.TimelineContent
import com.yapp.ndgl.feature.travel.traveldetail.component.TransportBottomSheet
import com.yapp.ndgl.feature.travel.traveldetail.component.TransportSegment
import com.yapp.ndgl.feature.travel.traveldetail.component.TravelDetailToolBar
import com.yapp.ndgl.feature.travel.traveldetail.component.TravelMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
internal fun TravelDetailRoute(
    viewModel: TravelDetailViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToTravelPlaceDetail: (String, TipContent?, List<AlternativePlace>?, Int, Long) -> Unit,
    navigateToAddItinerary:
    (travelId: Long, day: Int, country: String, representativeLatitude: Double, representativeLongitude: Double) -> Unit,
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is TravelDetailSideEffect.NavigateBack -> navigateBack()
            is TravelDetailSideEffect.NavigateToTravelPlaceDetail -> {
                navigateToTravelPlaceDetail(
                    sideEffect.googlePlaceId,
                    sideEffect.tipContent,
                    sideEffect.alternativePlaces,
                    sideEffect.day,
                    sideEffect.itineraryId,
                )
            }

            is TravelDetailSideEffect.NavigateToBrowser -> {
                context.launchBrowser(sideEffect.url)
            }

            is TravelDetailSideEffect.NavigateToAddItinerary -> {
                navigateToAddItinerary(
                    sideEffect.travelId,
                    sideEffect.day,
                    sideEffect.countryCode,
                    sideEffect.representativeLatLng.latitude,
                    sideEffect.representativeLatLng.longitude,
                )
            }

            is TravelDetailSideEffect.NavigateToMyTravel -> navigateBack()

            // FIXME: 임의로 넣은 애니메이션, 추후 수정 가능성 있음
            is TravelDetailSideEffect.ScrollToPlace -> {
                coroutineScope.launch {
                    val dayIndex = state.selectedDay - 1
                    val places = state.itineraries.getOrNull(dayIndex)?.places.orEmpty()
                    val placeIndex = places.indexOfFirst { it.id == sideEffect.placeId }

                    if (placeIndex >= 0) {
                        val targetIndex = state.placesOffset + placeIndex
                        delay(100)
                        listState.animateScrollToItem(targetIndex)
                    }
                }
            }

            is TravelDetailSideEffect.AnimatePlaceChange -> {
                coroutineScope.launch {
                    val dayIndex = state.selectedDay - 1
                    val places = state.itineraries.getOrNull(dayIndex)?.places.orEmpty()
                    val placeIndex = places.indexOfFirst { it.placeInfo.googlePlaceId == sideEffect.googlePlaceId }

                    if (placeIndex >= 0) {
                        val targetIndex = state.placesOffset + placeIndex
                        delay(100)
                        listState.animateScrollToItem(targetIndex)
                    }
                }
            }
        }
    }

    TravelDetailScreen(
        state = state,
        listState = listState,
        clickBack = { viewModel.onIntent(TravelDetailIntent.ClickBack) },
        selectDay = { viewModel.onIntent(TravelDetailIntent.SelectDay(it)) },
        clickStartTimeSetting = { viewModel.onIntent(TravelDetailIntent.ClickStartTimeSetting) },
        clickEditTravel = { viewModel.onIntent(TravelDetailIntent.ClickEditTravel) },
        clickAddScheduleButton = { viewModel.onIntent(TravelDetailIntent.ClickAddScheduleButton) },
        checkPlaceItem = { placeId ->
            viewModel.onIntent(TravelDetailIntent.CheckPlaceItem(placeId))
        },
        checkSelectAll = { viewModel.onIntent(TravelDetailIntent.CheckSelectAll) },
        clickDeleteSelectedPlaces = { viewModel.onIntent(TravelDetailIntent.ClickDeleteSelectedPlaces) },
        confirmDeleteSelectedPlaces = { viewModel.onIntent(TravelDetailIntent.ConfirmDeleteSelectedPlaces) },
        dismissDeleteModal = { viewModel.onIntent(TravelDetailIntent.DismissDeleteModal) },
        confirmCancelEditMode = { viewModel.onIntent(TravelDetailIntent.ConfirmCancelEditMode) },
        dismissCancelEditModal = { viewModel.onIntent(TravelDetailIntent.DismissCancelEditModal) },
        longClickPlaceItem = { viewModel.onIntent(TravelDetailIntent.LongClickPlaceItem) },
        dismissStartTimeSettingBottomSheet = { viewModel.onIntent(TravelDetailIntent.DismissStartTimeSettingBottomSheet) },
        confirmStartTimeSetting = { startTime -> viewModel.onIntent(TravelDetailIntent.ConfirmStartTimeSetting(startTime)) },
        reorderPlaces = { dayIndex, fromIndex, toIndex -> viewModel.onIntent(TravelDetailIntent.ReorderPlaces(dayIndex, fromIndex, toIndex)) },
        clickTransportSegment = { place -> viewModel.onIntent(TravelDetailIntent.ClickTransportSegment(place)) },
        confirmChangeTransport = { segment -> viewModel.onIntent(TravelDetailIntent.ConfirmChangeTransportSegment(segment)) },
        dismissTransportBottomSheet = { viewModel.onIntent(TravelDetailIntent.DismissTransportBottomSheet) },
        confirmEditMode = { viewModel.onIntent(TravelDetailIntent.ConfirmEditMode) },
        clickPlaceItem = { viewModel.onIntent(TravelDetailIntent.ClickPlaceItem(it)) },
        dismissPlaceBottomSheet = { viewModel.onIntent(TravelDetailIntent.DismissPlaceBottomSheet) },
        navigateToTravelPlaceDetail = { viewModel.onIntent(TravelDetailIntent.NavigateToTravelPlaceDetail(it)) },
        clickAddTime = { viewModel.onIntent(TravelDetailIntent.ClickAddTime(it)) },
        clickAddMemo = { viewModel.onIntent(TravelDetailIntent.ClickAddMemo(it)) },
        clickAddCost = { viewModel.onIntent(TravelDetailIntent.ClickAddCost(it)) },
        clickFindRoute = { viewModel.onIntent(TravelDetailIntent.ClickFindRoute(it)) },
        dismissTimeBottomSheet = { viewModel.onIntent(TravelDetailIntent.DismissTimeBottomSheet) },
        confirmDuration = {
            viewModel.onIntent(TravelDetailIntent.ConfirmDuration(it))
        },
        dismissCostModal = { viewModel.onIntent(TravelDetailIntent.DismissCostModal) },
        confirmCost = { viewModel.onIntent(TravelDetailIntent.ConfirmCost(it)) },
        dismissMemoModal = { viewModel.onIntent(TravelDetailIntent.DismissMemoModal) },
        confirmMemo = { viewModel.onIntent(TravelDetailIntent.ConfirmMemo(it)) },
    )
}

@Composable
private fun TravelDetailScreen(
    state: TravelDetailState,
    listState: LazyListState,
    clickBack: () -> Unit,
    selectDay: (Int) -> Unit,
    clickStartTimeSetting: () -> Unit,
    clickEditTravel: () -> Unit,
    clickAddScheduleButton: () -> Unit,
    checkPlaceItem: (Long) -> Unit,
    checkSelectAll: () -> Unit,
    clickDeleteSelectedPlaces: () -> Unit,
    confirmDeleteSelectedPlaces: () -> Unit,
    dismissDeleteModal: () -> Unit,
    confirmCancelEditMode: () -> Unit,
    dismissCancelEditModal: () -> Unit,
    longClickPlaceItem: () -> Unit,
    dismissStartTimeSettingBottomSheet: () -> Unit,
    confirmStartTimeSetting: (Duration) -> Unit,
    reorderPlaces: (Int, Int, Int) -> Unit,
    confirmEditMode: () -> Unit,
    clickTransportSegment: (TravelPlace) -> Unit,
    confirmChangeTransport: (TransportSegment) -> Unit,
    dismissTransportBottomSheet: () -> Unit,
    clickPlaceItem: (TravelPlace) -> Unit,
    clickAddTime: (Long) -> Unit,
    clickAddMemo: (Long) -> Unit,
    clickAddCost: (Long) -> Unit,
    clickFindRoute: (String) -> Unit,
    dismissPlaceBottomSheet: () -> Unit,
    navigateToTravelPlaceDetail: (String) -> Unit,
    dismissTimeBottomSheet: () -> Unit,
    confirmDuration: (Duration) -> Unit,
    dismissCostModal: () -> Unit,
    confirmCost: (Int) -> Unit,
    dismissMemoModal: () -> Unit,
    confirmMemo: (String) -> Unit,
) {
    BackHandler(enabled = state.isEditMode) {
        clickBack()
    }

    val tabs = (1..state.contentInfo.days).map { day ->
        NDGLChipTabAttr.Tab(
            tag = "d$day",
            name = stringResource(R.string.day_format, day),
        )
    }.let { persistentListOf(*it.toTypedArray()) }

    val isHeaderSticky by remember {
        derivedStateOf {
            val firstItem = listState.layoutInfo.visibleItemsInfo.firstOrNull()
            val result = firstItem?.index == 1 && firstItem.offset <= 0
            result
        }
    }
    var isMapScrolled by remember { mutableStateOf(true) }

    val tempPlaces = remember(state.tempItineraries, state.selectedDay) {
        state.tempItineraries.getOrNull(state.selectedDay - 1)?.places.orEmpty().toMutableStateList()
    }
    val reorderableState = rememberReorderableState(
        list = tempPlaces,
        lazyListState = listState,
        offset = state.placesOffset,
        isReorderable = { key -> key is String && key.startsWith("place_") },
    )
    var isDragMode by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NDGLTheme.colors.white)
            .navigationBarsPadding(),
    ) {
        LazyColumn(
            state = listState,
            userScrollEnabled = isMapScrolled && !isDragMode,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp)
                .then(
                    if (state.isEditMode) {
                        Modifier.reorderable(reorderableState) { from, to ->
                            if (from != null && to != null && from != to) {
                                reorderPlaces(state.selectedDay - 1, from - state.placesOffset, to - state.placesOffset)
                            }
                        }
                    } else {
                        Modifier
                    },
                ),
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                        .background(NDGLTheme.colors.black50)
                        .statusBarsPadding(),
                ) {
                    NDGLNavigationBar(
                        textAlignType = NDGLNavigationBarAttr.TextAlignType.START,
                        leadingIcon = R.drawable.ic_28_chevron_left,
                        onLeadingIconClick = clickBack,
                        trailingContents = {
                            NDGLNavigationIcon(
                                icon = R.drawable.ic_28_share,
                                onClick = {},
                            )
                        },
                    )
                    ContentCard(contentInfo = state.contentInfo)
                }
            }

            stickyHeader {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NDGLTheme.colors.white)
                        .then(
                            if (isHeaderSticky) {
                                Modifier
                                    .statusBarsPadding()
                                    .padding(top = 10.dp)
                            } else {
                                Modifier.padding(top = 22.dp)
                            },
                        )
                        .padding(horizontal = 24.dp),
                ) {
                    NDGLChipTab(
                        tabs = tabs,
                        selectedIndex = state.selectedDay - 1,
                        onTabSelected = { index -> selectDay(index + 1) },
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            item(key = "map_${state.selectedDay}") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    state.currentItinerary?.let { itinerary ->
                        if (itinerary.places.isNotEmpty()) {
                            TravelMap(
                                places = itinerary.places,
                                onScrollEnabledChange = { isMapScrolled = it },
                            )
                            if (state.isEditMode) {
                                val currentDayPlaceIds = itinerary.places.map { it.id }.toSet()
                                val selectedInCurrentDay = state.selectedPlaceIds.intersect(currentDayPlaceIds)
                                val isAllSelected = selectedInCurrentDay.size == itinerary.places.size
                                EditControlBar(
                                    isAllSelected = isAllSelected,
                                    onSelectAllClick = checkSelectAll,
                                    onDeleteSelectedClick = { if (state.selectedPlaceIds.isNotEmpty()) clickDeleteSelectedPlaces() },
                                )
                            } else {
                                TravelDetailToolBar(
                                    startTime = if (itinerary.isStartTimeSet) itinerary.startTime else null,
                                    clickStartTimeSetting = clickStartTimeSetting,
                                    clickEditTravel = clickEditTravel,
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Spacer(Modifier.height(80.dp))
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.img_empty_suitcase),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = stringResource(R.string.no_schedule_message),
                                    color = NDGLTheme.colors.black500,
                                    style = NDGLTheme.typography.subtitleMdSemiBold,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.no_schedule_message_detail, state.selectedDay),
                                    color = NDGLTheme.colors.black400,
                                    style = NDGLTheme.typography.bodyLgRegular,
                                )
                            }
                        }
                    }
                }
            }

            if (state.isEditMode) {
                itemsIndexed(
                    items = tempPlaces,
                    key = { _, place -> "place_${state.selectedDay}_${place.id}" },
                ) { index, place ->
                    val isDragging = reorderableState.currentIndex == index + state.placesOffset
                    Box(
                        modifier = Modifier
                            .animateItem()
                            .fillMaxWidth()
                            .zIndex(if (isDragging) 1f else 0f)
                            .padding(bottom = 16.dp)
                            .background(if (isDragging) NDGLTheme.colors.black50.copy(0.9f) else Color.Transparent)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        isDragMode = true
                                        tryAwaitRelease()
                                        isDragMode = false
                                    },
                                )
                            },
                    ) {
                        EditablePlaceItem(
                            place = place,
                            checked = state.selectedPlaceIds.contains(place.id),
                            onCheck = { checkPlaceItem(place.id) },
                        )
                    }
                }
            } else {
                itemsIndexed(
                    items = state.currentPlaces,
                    key = { _, place -> "place_${state.selectedDay}_${place.id}" },
                ) { index, place ->
                    Column {
                        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                            PlaceItem(
                                place = place,
                                onClick = { clickPlaceItem(place) },
                                onLongClick = longClickPlaceItem,
                            )
                        }

                        if (index < state.currentPlaces.size - 1) {
                            Spacer(Modifier.height(10.dp))

                            place.transportToNext?.let { segment ->
                                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                                    TransportSegment(
                                        segment = segment,
                                        onClick = { clickTransportSegment(place) },
                                    )
                                }
                            }

                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(60.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .dropShadow(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.06f),
                    blur = 20.dp,
                    offsetY = (-10).dp,
                )
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(NDGLTheme.colors.white)
                .padding(top = 20.dp, bottom = 16.dp)
                .padding(horizontal = 24.dp),
        ) {
            if (state.isEditMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = NDGLTheme.colors.black50)
                            .clickable {
                                clickAddScheduleButton()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_24_plus), contentDescription = null)
                    }
                    NDGLCTAButton(
                        modifier = Modifier.fillMaxWidth(),
                        type = NDGLCTAButtonAttr.Type.PRIMARY,
                        size = NDGLCTAButtonAttr.Size.LARGE,
                        status = NDGLCTAButtonAttr.Status.ACTIVE,
                        label = stringResource(R.string.edit_done),
                        onClick = confirmEditMode,
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    NDGLCTAButton(
                        modifier = Modifier.fillMaxWidth(),
                        type = NDGLCTAButtonAttr.Type.PRIMARY,
                        size = NDGLCTAButtonAttr.Size.LARGE,
                        status = NDGLCTAButtonAttr.Status.ACTIVE,
                        label = if (state.isEmptyItinerary) {
                            stringResource(
                                R.string.add_schedule_button_text_no_schedule,
                                state.selectedDay,
                            )
                        } else {
                            stringResource(R.string.add_schedule_button_text)
                        },
                        onClick = clickAddScheduleButton,
                    )
                }
            }
        }

        if (state.showDeleteModal) {
            NDGLModal(
                onDismissRequest = dismissDeleteModal,
                title = stringResource(R.string.delete_place_dialog_title),
                body = stringResource(R.string.delete_place_dialog_message),
                negativeButtonText = stringResource(R.string.delete_place_dialog_cancel),
                onNegativeButtonClick = dismissDeleteModal,
                positiveButtonText = stringResource(R.string.delete_place_dialog_confirm),
                onPositiveButtonClick = confirmDeleteSelectedPlaces,
            )
        }

        if (state.showCancelEditModal) {
            NDGLModal(
                onDismissRequest = dismissCancelEditModal,
                title = stringResource(R.string.cancel_edit_dialog_title),
                body = stringResource(R.string.cancel_edit_dialog_message),
                negativeButtonText = stringResource(R.string.cancel_edit_dialog_cancel),
                onNegativeButtonClick = confirmCancelEditMode,
                positiveButtonText = stringResource(R.string.cancel_edit_dialog_confirm),
                onPositiveButtonClick = dismissCancelEditModal,
            )
        }

        if (state.showStartTimeSettingBottomSheet) {
            NDGLBottomSheet(
                onDismissRequest = dismissStartTimeSettingBottomSheet,
                showDragHandle = false,
                title = stringResource(R.string.start_time_setting_title),
            ) {
                TimelineContent(
                    startTime = state.currentItinerary?.startTime ?: Itinerary.DEFAULT_START_TIME.hours,
                    totalDuration = state.currentItinerary?.totalDuration ?: 0.hours,
                    onConfirm = confirmStartTimeSetting,
                )
            }
        }

        if (state.showTransportBottomSheet && state.selectedPlace != null && state.selectedPlace.transportToNext != null) {
            TransportBottomSheet(
                initialTransport = state.selectedPlace.transportToNext,
                availableTransports = state.availableTransports,
                onDismissRequest = dismissTransportBottomSheet,
                onConfirm = confirmChangeTransport,
            )
        }

        if (state.showPlaceBottomSheet && state.selectedPlace != null) {
            PlaceBottomSheet(
                place = state.selectedPlace,
                onDismissRequest = dismissPlaceBottomSheet,
                navigateToTravelPlaceDetail = { navigateToTravelPlaceDetail(state.selectedPlace.placeInfo.googlePlaceId) },
                onAddTimeClick = clickAddTime,
                onAddCostClick = clickAddCost,
                onAddMemoClick = clickAddMemo,
                onFindRouteClick = clickFindRoute,
            )
        }

        if (state.showTimeBottomSheet && state.selectedPlace != null) {
            NDGLBottomSheet(
                onDismissRequest = dismissTimeBottomSheet,
                showDragHandle = false,
            ) {
                val currentDuration = state.selectedPlace.duration
                DurationPickerContent(
                    currentDuration = currentDuration,
                    onDismissRequest = dismissTimeBottomSheet,
                    onConfirm = { duration ->
                        confirmDuration(duration)
                    },
                )
            }
        }

        if (state.showCostModal && state.selectedPlace != null) {
            var cost by remember { mutableStateOf(state.selectedPlace.userData.cost?.toString() ?: "") }

            NDGLInputModal(
                onDismissRequest = dismissCostModal,
                title = stringResource(R.string.cost_modal_title),
                value = cost,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        cost = newValue
                    }
                },
                placeholder = stringResource(R.string.cost_modal_placeholder),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                positiveButtonText = stringResource(R.string.cost_modal_confirm),
                onPositiveButtonClick = {
                    cost.toIntOrNull()?.let { costValue ->
                        confirmCost(costValue)
                    }
                },
                negativeButtonText = stringResource(R.string.cost_modal_cancel),
                textAlign = TextAlign.Center,
                placeholderStyle = NDGLTheme.typography.subtitleLgSemiBold,
                textStyle = NDGLTheme.typography.subtitleLgSemiBold,
            )
        }

        if (state.showMemoModal && state.selectedPlace != null) {
            var memo by remember { mutableStateOf(state.selectedPlace.userData.memo ?: "") }

            NDGLInputModal(
                onDismissRequest = dismissMemoModal,
                title = stringResource(R.string.memo_modal_title),
                value = memo,
                onValueChange = { memo = it },
                placeholder = stringResource(R.string.memo_modal_placeholder),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Default,
                ),
                positiveButtonText = stringResource(R.string.memo_modal_confirm),
                onPositiveButtonClick = {
                    confirmMemo(memo)
                },
                negativeButtonText = stringResource(R.string.cost_modal_cancel),
                minHeight = 180.dp,
                placeholderStyle = NDGLTheme.typography.bodyLgMedium,
                textStyle = NDGLTheme.typography.bodyLgRegular,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TravelDetailScreenPreview() {
    NDGLTheme {
        TravelDetailScreen(
            listState = rememberLazyListState(),
            state = TravelDetailState(
                contentInfo = ContentInfo(
                    country = "태국",
                    city = "방콕",
                    budgetPerPerson = Budget(1200000),
                    nights = 3,
                    days = 4,
                    videoInfo = VideoInfo(
                        title = "방콕 풀코스, 동남아 안 가본 곽튜브와 함께 【방콕】",
                        creatorName = "빠니보틀",
                        profileImage = "",
                        thumbnail = "",
                        link = "",
                        summary = "빠니보틀은 주말을 이용해 직장인들도 충분히 다녀올 수 있는 '금요일 퇴근 후 방콕 여행'의 가능성을 보여주며, 곽튜브와의 티격태격 케미를 통해 방콕의 매력을 소개합니다",
                    ),
                ),
                selectedDay = 1,
                itineraries = listOf(
                    Itinerary(
                        places = listOf(
                            TravelPlace(
                                id = 1,
                                placeInfo = PlaceInfo(
                                    day = 1,
                                    sequence = 1,
                                    googlePlaceId = "",
                                    thumbnail = "",
                                    latitude = 35.6585805,
                                    longitude = 139.7454329,
                                    name = "도쿄 타워",
                                    googleMapsUri = "",
                                    placeType = PlaceType.ATTRACTION,
                                ),
                                userData = TravelPlace.UserData(estimatedDuration = 90.minutes),
                                transportToNext = TransportSegment(
                                    googlePlaceId = "1",
                                    type = TransportType.DRIVING,
                                    duration = 25.minutes,
                                    distance = 3500,
                                ),
                                startTime = 8.hours,
                            ),
                            TravelPlace(
                                id = 2,
                                placeInfo = PlaceInfo(
                                    day = 1,
                                    sequence = 2,
                                    googlePlaceId = "",
                                    thumbnail = "",
                                    latitude = 35.6654,
                                    longitude = 139.7707,
                                    name = "츠키지 스시 다이",
                                    googleMapsUri = "",
                                    placeType = PlaceType.RESTAURANT,
                                ),
                                userData = TravelPlace.UserData(estimatedDuration = 60.minutes),
                                startTime = 0.hours,
                                transportToNext = null,
                            ),
                        ),
                    ),
                ),
                isEditMode = false,
                selectedPlaceIds = emptySet(),
            ),
            clickBack = {},
            selectDay = {},
            clickStartTimeSetting = {},
            clickEditTravel = {},
            clickAddScheduleButton = {},
            checkPlaceItem = {},
            checkSelectAll = {},
            clickDeleteSelectedPlaces = {},
            confirmDeleteSelectedPlaces = {},
            dismissDeleteModal = {},
            confirmCancelEditMode = {},
            dismissCancelEditModal = {},
            longClickPlaceItem = {},
            dismissStartTimeSettingBottomSheet = {},
            confirmStartTimeSetting = {},
            reorderPlaces = { _, _, _ -> },
            confirmEditMode = {},
            clickPlaceItem = {},
            clickAddTime = {},
            clickAddMemo = {},
            clickAddCost = {},
            clickFindRoute = {},
            dismissPlaceBottomSheet = {},
            navigateToTravelPlaceDetail = {},
            dismissTimeBottomSheet = {},
            confirmDuration = { _ -> },
            dismissCostModal = {},
            confirmCost = { _ -> },
            dismissMemoModal = {},
            confirmMemo = { _ -> },
            clickTransportSegment = {},
            confirmChangeTransport = { _ -> },
            dismissTransportBottomSheet = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TravelDetailScreenEditModePreview() {
    NDGLTheme {
        TravelDetailScreen(
            state = TravelDetailState(
                contentInfo = ContentInfo(
                    travelId = 0,
                    country = "태국",
                    city = "방콕",
                    budgetPerPerson = Budget(1200000),
                    nights = 3,
                    days = 4,
                    videoInfo = VideoInfo(
                        title = "방콕 풀코스, 동남아 안 가본 곽튜브와 함께 【방콕】",
                        creatorName = "빠니보틀",
                        profileImage = "",
                        thumbnail = "",
                        link = "",
                        summary = "빠니보틀은 주말을 이용해 직장인들도 충분히 다녀올 수 있는 '금요일 퇴근 후 방콕 여행'의 가능성을 보여주며, 곽튜브와의 티격태격 케미를 통해 방콕의 매력을 소개합니다",
                    ),
                ),
                selectedDay = 1,
                itineraries = listOf(
                    Itinerary(
                        places = listOf(
                            TravelPlace(
                                id = 1,
                                placeInfo = PlaceInfo(
                                    day = 1,
                                    sequence = 1,
                                    googlePlaceId = "",
                                    thumbnail = "",
                                    latitude = 35.6585805,
                                    longitude = 139.7454329,
                                    name = "도쿄 타워",
                                    googleMapsUri = "",
                                    placeType = PlaceType.ATTRACTION,
                                ),
                                userData = TravelPlace.UserData(estimatedDuration = 90.minutes),
                                transportToNext = TransportSegment(
                                    googlePlaceId = "1",
                                    type = TransportType.DRIVING,
                                    duration = 25.minutes,
                                    distance = 3500,
                                ),
                                startTime = 0.hours,
                            ),
                            TravelPlace(
                                id = 2,
                                placeInfo = PlaceInfo(
                                    day = 1,
                                    sequence = 2,
                                    googlePlaceId = "",
                                    thumbnail = "",
                                    latitude = 35.6654,
                                    longitude = 139.7707,
                                    name = "츠키지 스시 다이",
                                    googleMapsUri = "",
                                    placeType = PlaceType.RESTAURANT,
                                ),
                                userData = TravelPlace.UserData(estimatedDuration = 60.minutes),
                                transportToNext = null,
                                startTime = 8.hours,
                            ),
                        ),
                    ),
                ),
                isEditMode = true,
                selectedPlaceIds = setOf(1),
            ),
            clickBack = {},
            selectDay = {},
            clickStartTimeSetting = {},
            clickEditTravel = {},
            clickAddScheduleButton = {},
            checkPlaceItem = {},
            checkSelectAll = {},
            clickDeleteSelectedPlaces = {},
            confirmDeleteSelectedPlaces = {},
            dismissDeleteModal = {},
            confirmCancelEditMode = {},
            dismissCancelEditModal = {},
            longClickPlaceItem = {},
            dismissStartTimeSettingBottomSheet = {},
            confirmStartTimeSetting = {},
            reorderPlaces = { _, _, _ -> },
            confirmEditMode = {},
            clickPlaceItem = {},
            clickAddTime = {},
            clickAddMemo = {},
            clickAddCost = {},
            clickFindRoute = {},
            dismissPlaceBottomSheet = {},
            navigateToTravelPlaceDetail = {},
            dismissTimeBottomSheet = {},
            confirmDuration = { _ -> },
            dismissCostModal = {},
            confirmCost = { _ -> },
            dismissMemoModal = {},
            confirmMemo = { _ -> },
            clickTransportSegment = {},
            confirmChangeTransport = { _ -> },
            dismissTransportBottomSheet = {},
            listState = rememberLazyListState(),
        )
    }
}
