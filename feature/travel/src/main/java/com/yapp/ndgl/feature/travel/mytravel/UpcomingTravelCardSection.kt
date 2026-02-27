package com.yapp.ndgl.feature.travel.mytravel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.core.util.formatString
import com.yapp.ndgl.data.travel.model.PlaceCategory
import com.yapp.ndgl.feature.travel.R
import com.yapp.ndgl.feature.travel.mytravel.MyTravelState.TravelPlace
import com.yapp.ndgl.feature.travel.mytravel.MyTravelState.UpcomingTravel
import com.yapp.ndgl.feature.travel.util.toDisplayNameRes
import com.yapp.ndgl.feature.travel.util.toDrawableRes
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.minutes
import com.yapp.ndgl.core.ui.R as CoreR

@Composable
internal fun UpcomingTravelCardSection(
    modifier: Modifier,
    upcomingTravel: UpcomingTravel,
    onTravelClick: (Long, Int) -> Unit,
    onPlaceClick: (String) -> Unit,
) {
    when (upcomingTravel) {
        is UpcomingTravel.Upcoming -> {
            val days = java.time.temporal.ChronoUnit.DAYS.between(
                upcomingTravel.startDate,
                upcomingTravel.endDate,
            ).toInt() + 1
            UpcomingTravelCard(
                modifier = modifier,
                travel = upcomingTravel,
                onCardClick = { onTravelClick(upcomingTravel.travelId, days) },
            )
        }

        is UpcomingTravel.InProgress -> {
            val days = java.time.temporal.ChronoUnit.DAYS.between(
                upcomingTravel.startDate,
                upcomingTravel.endDate,
            ).toInt() + 1
            InProgressTravelCard(
                travel = upcomingTravel,
                onTravelClick = { travelId -> onTravelClick(travelId, days) },
                onPlaceClick = onPlaceClick,
            )
        }
    }
}

@Composable
private fun UpcomingTravelCard(
    modifier: Modifier,
    travel: UpcomingTravel.Upcoming,
    onCardClick: () -> Unit,
) {
    val dateFormat = stringResource(R.string.my_travel_upcoming_travel_date_format)
    val dateFormatter = remember(dateFormat) {
        DateTimeFormatter.ofPattern(dateFormat)
    }

    CardContainer(
        modifier = modifier,
        onCardClick = onCardClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = travel.imageUrl,
                contentDescription = travel.title,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DayTag(dDay = travel.dDay)
                    Text(
                        text = travel.title,
                        color = NDGLTheme.colors.black700,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = NDGLTheme.typography.subtitleMdSemiBold,
                    )
                }
                Text(
                    text = stringResource(
                        R.string.my_travel_upcoming_travel_travel_duration,
                        travel.startDate.format(dateFormatter),
                        travel.endDate.format(dateFormatter),
                    ),
                    style = NDGLTheme.typography.bodyMdRegular,
                    color = NDGLTheme.colors.black600,
                )
            }
        }
    }
}

@Composable
private fun DayTag(
    dDay: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(
                color = NDGLTheme.colors.black100,
                shape = RoundedCornerShape(999.dp),
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (dDay > 0) {
                stringResource(R.string.my_travel_upcoming_travel_d_day_minus, dDay)
            } else {
                stringResource(R.string.my_travel_upcoming_travel_d_day_plus, dDay)
            },
            style = NDGLTheme.typography.bodyMdMedium,
            color = NDGLTheme.colors.black400,
        )
    }
}

@Composable
private fun InProgressTravelCard(
    travel: UpcomingTravel.InProgress,
    onTravelClick: (Long) -> Unit,
    onPlaceClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateFormat = stringResource(R.string.my_travel_upcoming_travel_date_format)
    val dateFormatter = remember(dateFormat) {
        DateTimeFormatter.ofPattern(dateFormat)
    }

    CardContainer(
        modifier = modifier,
        onCardClick = { onTravelClick(travel.travelId) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = travel.title,
                        modifier = Modifier.weight(1f, fill = false),
                        style = NDGLTheme.typography.subtitleMdSemiBold,
                        color = NDGLTheme.colors.black700,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = stringResource(
                            R.string.my_travel_upcoming_travel_in_progress_day_count,
                            travel.dayCount,
                        ),
                        style = NDGLTheme.typography.subtitleMdSemiBold,
                        color = NDGLTheme.colors.black700,
                        maxLines = 1,
                    )
                }
                Text(
                    text = stringResource(
                        R.string.my_travel_upcoming_travel_travel_duration,
                        travel.startDate.format(dateFormatter),
                        travel.endDate.format(dateFormatter),
                    ),
                    style = NDGLTheme.typography.bodyMdRegular,
                    color = NDGLTheme.colors.black500,
                )
            }

            if (travel.currentPlace != null) {
                PlaceInfoCard(
                    place = travel.currentPlace,
                    onPlaceClick = { onPlaceClick(travel.currentPlace.placeId) },
                )
            }
        }
    }
}

@Composable
private fun PlaceInfoCard(
    place: TravelPlace,
    onPlaceClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(NDGLTheme.colors.white)
            .clickable(onClick = onPlaceClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(place.category.toDrawableRes()),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = NDGLTheme.colors.black400,
                )
                Text(
                    text = stringResource(place.category.toDisplayNameRes()),
                    color = NDGLTheme.colors.black400,
                    style = NDGLTheme.typography.bodySmMedium,
                )
                Text(
                    text = stringResource(CoreR.string.common_dot_separator),
                    color = NDGLTheme.colors.black400,
                    style = NDGLTheme.typography.bodyMdMedium,
                )
                Text(
                    text = stringResource(
                        CoreR.string.estimated_duration_format,
                        place.estimatedDuration.minutes.formatString(),
                    ),
                    color = NDGLTheme.colors.black400,
                    style = NDGLTheme.typography.bodySmMedium,
                )
            }
            Text(
                text = place.name,
                color = NDGLTheme.colors.black900,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = NDGLTheme.typography.bodyLgSemiBold,
            )
        }

        AsyncImage(
            model = place.thumbnailUrl,
            contentDescription = place.name,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun CardContainer(
    modifier: Modifier,
    onCardClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(NDGLTheme.colors.black50)
            .clickable(onClick = onCardClick),
        content = content,
    )
}

@Preview(showBackground = true)
@Composable
private fun UpcomingTravelCardPreview() {
    NDGLTheme {
        UpcomingTravelCardSection(
            modifier = Modifier,
            upcomingTravel = UpcomingTravel.Upcoming(
                travelId = 1L,
                title = "도쿄 여행",
                startDate = LocalDate.of(2025, 2, 15),
                endDate = LocalDate.of(2025, 2, 20),
                dDay = 7,
                imageUrl = "",
            ),
            onTravelClick = { _, _ -> },
            onPlaceClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InProgressTravelCardPreview() {
    NDGLTheme {
        UpcomingTravelCardSection(
            modifier = Modifier,
            upcomingTravel = UpcomingTravel.InProgress(
                travelId = 1L,
                title = "인도 여행",
                startDate = LocalDate.of(2025, 2, 1),
                endDate = LocalDate.of(2025, 2, 10),
                dayCount = 3,
                currentPlace = TravelPlace(
                    placeId = "place1",
                    category = PlaceCategory.ATTRACTION,
                    estimatedDuration = 60,
                    name = "인도 국제 공항",
                    thumbnailUrl = "",
                ),
            ),
            onTravelClick = { _, _ -> },
            onPlaceClick = {},
        )
    }
}
