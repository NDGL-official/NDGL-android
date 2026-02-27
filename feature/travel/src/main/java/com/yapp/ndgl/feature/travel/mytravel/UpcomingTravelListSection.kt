package com.yapp.ndgl.feature.travel.mytravel

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.feature.travel.R
import com.yapp.ndgl.feature.travel.mytravel.MyTravelState.UpcomingTravelItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.yapp.ndgl.core.ui.R as CoreR

@Composable
internal fun UpcomingTravelListSection(
    upcomingTravels: ImmutableList<UpcomingTravelItem>,
    onUserTravelClick: (Long, Int) -> Unit,
    onNewTravelFindClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Header()
        if (upcomingTravels.isNotEmpty()) {
            upcomingTravels.forEach { travel ->
                UpcomingTravel(
                    travel = travel,
                    onUserTravelClick = onUserTravelClick,
                )
            }
        } else {
            EmptyTravel(
                onNewTravelFindClick = onNewTravelFindClick,
            )
        }
    }
}

@Composable
private fun Header() {
    Text(
        text = stringResource(R.string.my_travel_upcoming_list_header),
        modifier = Modifier.padding(horizontal = 24.dp),
        color = NDGLTheme.colors.black700,
        style = NDGLTheme.typography.subtitleLgSemiBold,
    )
}

@Composable
private fun UpcomingTravel(
    travel: UpcomingTravelItem,
    onUserTravelClick: (Long, Int) -> Unit,
) {
    val dateFormat = stringResource(R.string.my_travel_upcoming_list_date_format)
    val dateFormatter = remember(dateFormat) {
        DateTimeFormatter.ofPattern(dateFormat)
    }
    val days = java.time.temporal.ChronoUnit.DAYS.between(
        travel.startDate,
        travel.endDate,
    ).toInt() + 1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onUserTravelClick(travel.travelId, days) })
            .padding(horizontal = 24.dp, vertical = 8.dp),
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
                    R.string.my_travel_upcoming_list_travel_duration,
                    travel.startDate.format(dateFormatter),
                    travel.endDate.format(dateFormatter),
                ),
                color = NDGLTheme.colors.black600,
                style = NDGLTheme.typography.bodyMdRegular,
            )
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
                shape = CircleShape,
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (dDay > 0) {
                stringResource(R.string.my_travel_upcoming_list_d_day_minus, dDay)
            } else {
                stringResource(R.string.my_travel_upcoming_list_d_day_plus, dDay)
            },
            color = NDGLTheme.colors.black400,
            style = NDGLTheme.typography.bodyMdMedium,
        )
    }
}

@Composable
private fun EmptyTravel(
    onNewTravelFindClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(CoreR.drawable.img_empty_suitcase),
            contentDescription = null,
            modifier = Modifier.size(100.dp),
        )
        Text(
            text = stringResource(R.string.my_travel_upcoming_list_empty_title),
            modifier = Modifier.padding(top = 16.dp),
            color = NDGLTheme.colors.black500,
            style = NDGLTheme.typography.subtitleMdSemiBold,
        )
        Text(
            text = stringResource(R.string.my_travel_upcoming_list_empty_description),
            modifier = Modifier.padding(top = 4.dp),
            color = NDGLTheme.colors.black400,
            style = NDGLTheme.typography.bodyLgRegular,
        )
        FindNewTravelCtaButton(
            modifier = Modifier.padding(top = 12.dp),
            onClick = onNewTravelFindClick,
        )
    }
}

@Composable
private fun FindNewTravelCtaButton(
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(color = NDGLTheme.colors.black200)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.my_travel_upcoming_list_find_new_travel),
            color = NDGLTheme.colors.black800,
            style = NDGLTheme.typography.bodyMdSemiBold,
        )
        Icon(
            imageVector = ImageVector.vectorResource(CoreR.drawable.ic_20_search),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = NDGLTheme.colors.black600,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UpcomingTravelListSectionPreview() {
    NDGLTheme {
        UpcomingTravelListSection(
            upcomingTravels = persistentListOf(
                UpcomingTravelItem(
                    travelId = 1L,
                    title = "도쿄 여행",
                    startDate = LocalDate.of(2026, 3, 1),
                    endDate = LocalDate.of(2026, 3, 4),
                    imageUrl = "",
                    dDay = -7,
                ),
                UpcomingTravelItem(
                    travelId = 2L,
                    title = "오사카 여행",
                    startDate = LocalDate.of(2026, 4, 10),
                    endDate = LocalDate.of(2026, 4, 14),
                    imageUrl = "",
                    dDay = -47,
                ),
            ),
            onUserTravelClick = { _, _ -> },
            onNewTravelFindClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UpcomingTravelListSectionEmptyPreview() {
    NDGLTheme {
        UpcomingTravelListSection(
            upcomingTravels = persistentListOf(),
            onUserTravelClick = { _, _ -> },
            onNewTravelFindClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UpcomingTravelPreview() {
    NDGLTheme {
        UpcomingTravel(
            travel = UpcomingTravelItem(
                travelId = 1L,
                title = "도쿄 여행",
                startDate = LocalDate.of(2026, 3, 1),
                endDate = LocalDate.of(2026, 3, 4),
                imageUrl = "",
                dDay = -7,
            ),
            onUserTravelClick = { _, _ -> },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DayTagPreview() {
    NDGLTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DayTag(dDay = -7)
            DayTag(dDay = 0)
            DayTag(dDay = 3)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyTravelPreview() {
    NDGLTheme {
        EmptyTravel(onNewTravelFindClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun FindNewTravelCtaButtonPreview() {
    NDGLTheme {
        FindNewTravelCtaButton(
            modifier = Modifier,
            onClick = {},
        )
    }
}
