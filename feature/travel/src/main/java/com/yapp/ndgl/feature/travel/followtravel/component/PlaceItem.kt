package com.yapp.ndgl.feature.travel.followtravel.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.core.util.formatString
import com.yapp.ndgl.feature.travel.followtravel.TravelPlace
import com.yapp.ndgl.feature.travel.model.PlaceInfo
import com.yapp.ndgl.feature.travel.model.PlaceType
import com.yapp.ndgl.feature.travel.model.getColor
import kotlin.time.Duration.Companion.minutes

@Composable
internal fun PlaceItem(
    place: TravelPlace,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlaceNumber(number = place.placeInfo.sequence, placeType = place.placeInfo.placeType)
        Spacer(Modifier.width(8.dp))
        PlaceCard(place = place, interactionSource = interactionSource)
    }
}

@Composable
private fun PlaceNumber(
    number: Int,
    placeType: PlaceType,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(placeType.getColor()),
    ) {
        Text(
            text = number.toString(),
            color = NDGLTheme.colors.white,
            style = NDGLTheme.typography.bodySmSemiBold,
        )
    }
}

@Composable
fun PlaceCard(
    place: TravelPlace,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .indication(interactionSource, ripple(bounded = true)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NDGLTheme.colors.white),
        border = BorderStroke(width = 1.dp, color = NDGLTheme.colors.black50),
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = ImageVector.vectorResource(place.placeInfo.placeType.iconRes),
                        contentDescription = null,
                        tint = Color.Unspecified,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${stringResource(place.placeInfo.placeType.labelRes)} • ${
                            stringResource(
                                R.string.estimated_duration_format,
                                place.placeInfo.estimatedDuration.formatString(),
                            )
                        }",
                        color = NDGLTheme.colors.black400,
                        style = NDGLTheme.typography.bodySmMedium,
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = place.placeInfo.name,
                    color = NDGLTheme.colors.black900,
                    style = NDGLTheme.typography.bodyLgSemiBold,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            AsyncImage(
                model = place.placeInfo.thumbnail,
                contentDescription = "Place Thumbnail",
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Preview
@Composable
private fun PlaceItemPreview() {
    NDGLTheme {
        PlaceItem(
            place = TravelPlace(
                id = 1,
                placeInfo = PlaceInfo(
                    day = 1,
                    sequence = 1,
                    estimatedDuration = 60.minutes,
                    googlePlaceId = "",
                    thumbnail = "",
                    latitude = 35.6585805,
                    longitude = 139.7454329,
                    name = "도쿄 타워",
                    googleMapsUri = "",
                    placeType = PlaceType.ATTRACTION,
                ),
            ),
            onClick = {},
        )
    }
}
