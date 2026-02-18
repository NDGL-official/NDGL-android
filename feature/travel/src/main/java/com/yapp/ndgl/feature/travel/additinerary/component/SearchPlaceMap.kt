package com.yapp.ndgl.feature.travel.additinerary.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.feature.travel.additinerary.SelectedPlaceDetail

@Composable
internal fun SearchPlaceMap(
    modifier: Modifier = Modifier,
    representativeLatLng: LatLng,
    selectedPlaceDetail: SelectedPlaceDetail? = null,
) {
    val placeInfo = selectedPlaceDetail?.placeInfo
    val hasValidLocation = placeInfo != null && (placeInfo.latitude != 0.0 || placeInfo.longitude != 0.0)

    val cameraTarget = if (hasValidLocation) {
        LatLng(placeInfo.latitude, placeInfo.longitude)
    } else {
        representativeLatLng
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cameraTarget, 15f)
    }

    LaunchedEffect(placeInfo?.latitude, placeInfo?.longitude) {
        val target = if (hasValidLocation) {
            LatLng(placeInfo.latitude, placeInfo.longitude)
        } else {
            representativeLatLng
        }
        cameraPositionState.position = CameraPosition.fromLatLngZoom(target, 15f)
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        contentPadding = PaddingValues(bottom = 100.dp),
        uiSettings = MapUiSettings(
            mapToolbarEnabled = false,
            zoomControlsEnabled = false,
            scrollGesturesEnabled = true,
            zoomGesturesEnabled = true,
            tiltGesturesEnabled = false,
            rotationGesturesEnabled = false,
            scrollGesturesEnabledDuringRotateOrZoom = false,
            compassEnabled = false,
            myLocationButtonEnabled = false,
        ),
    ) {
        if (hasValidLocation) {
            val pinState =
                rememberMarkerState(key = "pin_${placeInfo.id}", position = LatLng(placeInfo.latitude, placeInfo.longitude))
            val labelState =
                rememberMarkerState(key = "label_${placeInfo.id}", position = LatLng(placeInfo.latitude, placeInfo.longitude))

            Marker(state = pinState)
            MarkerComposable(
                state = labelState,
                anchor = Offset(0.5f, -0.5f),
            ) {
                Text(
                    text = placeInfo.name,
                    style = NDGLTheme.typography.bodyMdSemiBold,
                    color = NDGLTheme.colors.black700,
                )
            }
        }
    }
}
