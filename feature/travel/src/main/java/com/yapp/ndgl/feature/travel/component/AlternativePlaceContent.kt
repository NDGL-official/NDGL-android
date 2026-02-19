package com.yapp.ndgl.feature.travel.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.yapp.ndgl.feature.travel.model.AlternativePlace
import com.yapp.ndgl.feature.travel.model.PlaceType

@Composable
internal fun AlternativePlaceContent(
    alternativePlaces: List<AlternativePlace>,
    onClick: (String) -> Unit,
    onChangePlaceClick: ((AlternativePlace) -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        alternativePlaces.forEach { alternativePlace ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onClick(alternativePlace.id) }
                    .border(1.dp, NDGLTheme.colors.black50, RoundedCornerShape(16.dp))
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = alternativePlace.thumbnail,
                    contentDescription = null,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop,
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            imageVector = ImageVector.vectorResource(alternativePlace.placeType.iconRes),
                            contentDescription = null,
                            tint = Color.Unspecified,
                        )
                        Text(
                            stringResource(alternativePlace.placeType.labelRes),
                            color = NDGLTheme.colors.black400,
                            style = NDGLTheme.typography.bodySmMedium,
                        )
                    }
                    Text(alternativePlace.name, color = NDGLTheme.colors.black700, style = NDGLTheme.typography.bodyMdSemiBold)
                }
                if (onChangePlaceClick != null) {
                    Row(
                        modifier = Modifier
                            .height(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(NDGLTheme.colors.black500)
                            .clickable {
                                onChangePlaceClick(alternativePlace)
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            stringResource(R.string.place_detail_change_place),
                            style = NDGLTheme.typography.bodySmSemiBold,
                            color = NDGLTheme.colors.white,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AlternativePlaceContentPreview() {
    val alternativePlaces = listOf(
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
    )

    NDGLTheme {
        AlternativePlaceContent(alternativePlaces, onClick = {}, onChangePlaceClick = {})
    }
}
