package com.yapp.ndgl.feature.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.core.util.FlagEmojiUtil.toFlagEmoji
import com.yapp.ndgl.feature.home.R
import com.yapp.ndgl.feature.home.model.TravelContent

@Composable
internal fun TravelTemplate(
    travel: TravelContent,
    onTravelTemplateClick: (Long, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = { onTravelTemplateClick(travel.travelId, travel.days) })
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        AsyncImage(
            model = travel.thumbnail,
            contentDescription = travel.title,
            modifier = Modifier
                .width(140.dp)
                .height(88.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop,
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = travel.country.toFlagEmoji(),
                    style = NDGLTheme.typography.bodyLgMedium,
                )
                Text(
                    text = travel.country,
                    color = NDGLTheme.colors.black400,
                    style = NDGLTheme.typography.bodyMdMedium,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = travel.title,
                    color = NDGLTheme.colors.black900,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = NDGLTheme.typography.bodyLgMedium,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = travel.city,
                        color = NDGLTheme.colors.black400,
                        style = NDGLTheme.typography.bodyMdMedium,
                    )
                    Text(
                        text = stringResource(R.string.home_common_dot_separator),
                        color = NDGLTheme.colors.black400,
                        style = NDGLTheme.typography.bodyMdMedium,
                    )
                    Text(
                        text = stringResource(
                            R.string.home_popular_travel_nights_days,
                            travel.nights,
                            travel.days,
                        ),
                        color = NDGLTheme.colors.black400,
                        style = NDGLTheme.typography.bodyMdRegular,
                    )
                }
            }
        }
    }
}
