package com.yapp.ndgl.feature.travel.additinerary.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.DraggableAnchors
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.yapp.ndgl.core.ui.designsystem.NDGLBottomSheetDragHandle
import com.yapp.ndgl.core.ui.designsystem.NDGLCheckbox
import com.yapp.ndgl.core.ui.designsystem.NDGLNonModalBottomSheet
import com.yapp.ndgl.core.ui.designsystem.rememberNDGLNonModalBottomSheetState
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.core.ui.util.dropShadow
import com.yapp.ndgl.feature.travel.additinerary.AddItineraryChip
import com.yapp.ndgl.feature.travel.additinerary.SelectablePlace
import com.yapp.ndgl.feature.travel.model.PlaceType

@Immutable
enum class AddItineraryBottomSheetValue {
    Expanded,
    PartiallyExpanded,
    Collapsed,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddItineraryBottomSheet(
    modifier: Modifier = Modifier,
    initialValue: AddItineraryBottomSheetValue = AddItineraryBottomSheetValue.PartiallyExpanded,
    places: List<SelectablePlace>,
    day: Int,
    selectedChip: AddItineraryChip,
    checkedPlaceId: String? = null,
    selectChip: (AddItineraryChip) -> Unit,
    checkSelectablePlace: (String) -> Unit,
    clickSelectablePlace: (String) -> Unit,
    clickAddItinerary: () -> Unit,
) {
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val screenHeightPx = windowInfo.containerSize.height.toFloat()
    val anchors = DraggableAnchors {
        AddItineraryBottomSheetValue.Collapsed at screenHeightPx * 0.77f
        AddItineraryBottomSheetValue.PartiallyExpanded at screenHeightPx * 0.45f
        AddItineraryBottomSheetValue.Expanded at screenHeightPx * 0.12f
    }
    val sheetState = rememberNDGLNonModalBottomSheetState(
        initialValue = initialValue,
        anchors,
    )

    NDGLNonModalBottomSheet(
        sheetState = sheetState,
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .dropShadow(
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    color = Color.Black.copy(alpha = 0.06f),
                    blur = 20.dp,
                    offsetY = (-10).dp,
                )
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(NDGLTheme.colors.white),
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                NDGLBottomSheetDragHandle()
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                Spacer(Modifier.height(27.dp))
                AddItineraryChipRow(day = day, selectedChip = selectedChip, selectChip = selectChip)
                Spacer(Modifier.height(28.dp))
                val lazyListState = rememberLazyListState()
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    items(places, key = { it.googlePlaceId }) { place ->
                        SelectablePlaceItem(
                            place = place,
                            checked = place.googlePlaceId == checkedPlaceId,
                            onCheck = { checkSelectablePlace(place.googlePlaceId) },
                            onClick = { clickSelectablePlace(place.googlePlaceId) },
                        )
                    }
                }
            }
            AddItineraryButton(
                modifier = Modifier.navigationBarsPadding(),
                enabled = places.any { it.googlePlaceId == checkedPlaceId },
                clickAddItinerary = clickAddItinerary,
            )
            Spacer(modifier = Modifier.height(with(density) { sheetState.anchoredDraggableState.offset.toDp() }))
        }
    }
}

@Composable
private fun AddItineraryChipRow(
    day: Int,
    selectedChip: AddItineraryChip,
    selectChip: (AddItineraryChip) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AddItineraryChip.entries.forEach { chip ->
            AddItineraryChip(
                chip = chip,
                label = stringResource(chip.labelRes, day),
                selected = chip == selectedChip,
                onClick = { selectChip(chip) },
            )
        }
    }
}

@Composable
internal fun AddItineraryChip(
    modifier: Modifier = Modifier,
    chip: AddItineraryChip,
    label: String,
    selected: Boolean,
    onClick: (AddItineraryChip) -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(34.dp))
            .background(
                if (selected) NDGLTheme.colors.green50 else NDGLTheme.colors.white,
            )
            .border(1.25.dp, if (selected) NDGLTheme.colors.green500 else NDGLTheme.colors.black200, RoundedCornerShape(34.dp))
            .clickable { onClick(chip) }
            .padding(horizontal = 16.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = NDGLTheme.typography.bodyMdSemiBold,
            color = if (selected) NDGLTheme.colors.green500 else NDGLTheme.colors.black500,
        )
    }
}

@Composable
internal fun SelectablePlaceItem(
    place: SelectablePlace,
    checked: Boolean,
    onCheck: (String) -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NDGLCheckbox(checked = checked, onClick = { onCheck(place.googlePlaceId) })
        Spacer(Modifier.width(16.dp))
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = place.thumbnail,
                contentDescription = null,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(NDGLTheme.colors.black300),
                contentScale = ContentScale.Crop,
            )
            Spacer(Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = place.name,
                    style = NDGLTheme.typography.bodyMdSemiBold,
                    color = NDGLTheme.colors.black700,
                    maxLines = 2,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(place.placeType.labelRes),
                        style = NDGLTheme.typography.bodyMdMedium,
                        color = NDGLTheme.colors.black400,
                    )
                }
            }
        }
    }
}

@Preview(name = "AddItinerary - Expanded", showBackground = true)
@Composable
private fun AddItineraryBottomSheet_Expanded_Preview() {
    val mockPlaces = List(5) {
        SelectablePlace(googlePlaceId = "$it", name = "장소 $it", thumbnail = "", placeType = PlaceType.ATTRACTION)
    }
    NDGLTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AddItineraryBottomSheet(
                day = 1,
                selectedChip = AddItineraryChip.RECOMMENDED_PLACE,
                places = mockPlaces,
                selectChip = {},
                checkSelectablePlace = {},
                clickSelectablePlace = {},
                clickAddItinerary = {},
                initialValue = AddItineraryBottomSheetValue.Expanded,
            )
        }
    }
}

@Preview(name = "AddItinerary - PartiallyExpanded", showBackground = true)
@Composable
private fun AddItineraryBottomSheet_PartiallyExpanded_Preview() {
    val mockPlaces = List(5) {
        SelectablePlace(googlePlaceId = "$it", name = "장소 $it", thumbnail = "", placeType = PlaceType.ATTRACTION)
    }
    NDGLTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AddItineraryBottomSheet(
                day = 1,
                selectedChip = AddItineraryChip.RECOMMENDED_PLACE,
                places = mockPlaces,
                selectChip = {},
                checkSelectablePlace = {},
                clickSelectablePlace = {},
                clickAddItinerary = {},
                initialValue = AddItineraryBottomSheetValue.PartiallyExpanded,
            )
        }
    }
}

@Preview(name = "AddItinerary - Collapsed", showBackground = true)
@Composable
private fun AddItineraryBottomSheet_Collapsed_Preview() {
    val mockPlaces = List(5) {
        SelectablePlace(googlePlaceId = "$it", name = "장소 $it", thumbnail = "", placeType = PlaceType.ATTRACTION)
    }
    NDGLTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AddItineraryBottomSheet(
                day = 1,
                selectedChip = AddItineraryChip.RECOMMENDED_PLACE,
                places = mockPlaces,
                selectChip = {},
                checkSelectablePlace = {},
                clickSelectablePlace = {},
                clickAddItinerary = {},
                initialValue = AddItineraryBottomSheetValue.Collapsed,
            )
        }
    }
}
