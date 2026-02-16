@file:OptIn(ExperimentalMaterial3Api::class)

package com.yapp.ndgl.feature.travel.traveldetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.designsystem.NDGLBottomSheet
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.core.ui.util.noRippleClickable
import com.yapp.ndgl.core.util.formatDecimal
import com.yapp.ndgl.core.util.formatString
import com.yapp.ndgl.feature.travel.traveldetail.PlaceType
import com.yapp.ndgl.feature.travel.traveldetail.TravelPlace
import kotlin.Int
import kotlin.String
import kotlin.time.Duration.Companion.hours

@Composable
internal fun PlaceBottomSheet(
    place: TravelPlace,
    onDismissRequest: () -> Unit,
    navigateToPlaceDetail: () -> Unit,
    onAddTimeClick: (Int) -> Unit,
    onAddCostClick: (Int) -> Unit,
    onAddMemoClick: (Int) -> Unit,
    onFindRouteClick: (String) -> Unit,
) {
    NDGLBottomSheet(
        onDismissRequest = onDismissRequest,
        showDragHandle = false,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp, bottom = 12.dp),

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = place.name,
                            style = NDGLTheme.typography.titleMdSemiBold,
                            color = NDGLTheme.colors.black800,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(color = NDGLTheme.colors.black50)
                                .clickable {
                                    navigateToPlaceDetail()
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_24_chevron_right), contentDescription = null)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            imageVector = ImageVector.vectorResource(place.placeType.iconRes),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = stringResource(place.placeType.labelRes),
                            style = NDGLTheme.typography.bodyMdMedium,
                            color = NDGLTheme.colors.black500,
                        )
                        Text(
                            text = "•",
                            style = NDGLTheme.typography.bodyMdMedium,
                            color = NDGLTheme.colors.black500,
                        )
                        Text(
                            text = stringResource(
                                R.string.estimated_duration_format,
                                place.duration.formatString(),
                            ),
                            style = NDGLTheme.typography.bodyMdMedium,
                            color = NDGLTheme.colors.black500,
                        )
                        Icon(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .noRippleClickable { onAddTimeClick(place.id) },
                            imageVector = ImageVector.vectorResource(R.drawable.ic_20_pen),
                            contentDescription = null,
                            tint = NDGLTheme.colors.black600,
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.opening_hours_format, place.regularOpeningHours),
                        style = NDGLTheme.typography.bodyMdMedium,
                        color = NDGLTheme.colors.black500,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.noRippleClickable { onAddMemoClick(place.id) },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_20_clipboard),
                                contentDescription = null,
                                tint = NDGLTheme.colors.black400,
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                stringResource(R.string.add_memo),
                                color = NDGLTheme.colors.black400,
                                style = NDGLTheme.typography.bodyMdMedium,
                            )
                            place.userData?.memo?.let { memo ->
                                Spacer(Modifier.width(16.dp))
                                Text(
                                    text = memo,
                                    color = NDGLTheme.colors.black500,
                                    style = NDGLTheme.typography.bodyMdMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(Modifier.weight(1f))
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.ic_20_chevron_right),
                                    contentDescription = null,
                                    tint = NDGLTheme.colors.black600,
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.noRippleClickable { onAddCostClick(place.id) },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_20_card),
                                contentDescription = null,
                                tint = NDGLTheme.colors.black400,
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                stringResource(R.string.add_cost),
                                color = NDGLTheme.colors.black400,
                                style = NDGLTheme.typography.bodyMdMedium,
                            )
                            place.userData?.cost?.let { cost ->
                                Spacer(Modifier.width(16.dp))
                                Text(
                                    text = "${cost.formatDecimal()}원",
                                    color = NDGLTheme.colors.black500,
                                    style = NDGLTheme.typography.bodyMdMedium,
                                )
                                Spacer(Modifier.weight(1f))
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.ic_20_chevron_right),
                                    contentDescription = null,
                                    tint = NDGLTheme.colors.black600,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onFindRouteClick(place.googleMapsUri)
                                }
                                .background(NDGLTheme.colors.white)
                                .border(1.dp, NDGLTheme.colors.black200, RoundedCornerShape(8.dp))
                                .padding(vertical = 17.5.dp),
                            horizontalArrangement = Arrangement.spacedBy(
                                space = 8.dp,
                                alignment = Alignment.CenterHorizontally,
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(R.string.find_route),
                                style = NDGLTheme.typography.bodyLgSemiBold,
                                color = NDGLTheme.colors.black600,
                            )
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_24_map),
                                contentDescription = null,
                                tint = NDGLTheme.colors.black600,
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceBottomSheetPreview() {
    val mockPlace = TravelPlace(
        id = 1,
        day = 1,
        sequence = 1,
        googlePlaceId = "",
        thumbnail = "",
        latitude = 0.0,
        longitude = 0.0,
        name = "콜로세움",
        regularOpeningHours = "11:00~23:00",
        googleMapsUri = "",
        placeType = PlaceType.CAFE,
        userData = TravelPlace.UserData(
            memo = "장소에 대한 메모",
            cost = 20000,
            estimatedDuration = 1.hours,
        ),
        startTime = 8.hours,
    )

    NDGLTheme {
        Box(Modifier.fillMaxSize()) {
            PlaceBottomSheet(
                place = mockPlace,
                onDismissRequest = {},
                navigateToPlaceDetail = {},
                onFindRouteClick = {},
                onAddTimeClick = {},
                onAddCostClick = {},
                onAddMemoClick = {},
            )
        }
    }
}
