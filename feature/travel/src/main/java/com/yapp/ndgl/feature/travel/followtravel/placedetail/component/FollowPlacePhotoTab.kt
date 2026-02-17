package com.yapp.ndgl.feature.travel.followtravel.placedetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.yapp.ndgl.feature.travel.model.PlacePhoto

@Composable
internal fun FollowPlacePhotoTab(leftPhotos: List<PlacePhoto>, rightPhotos: List<PlacePhoto>) {
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
                        .then(
                            if (photo.height == 0) {
                                Modifier.height(44.dp)
                            } else {
                                Modifier
                                    .aspectRatio(photo.aspectRatio)
                            },
                        )
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
                    modifier = if (photo.height == 0) {
                        Modifier.size(44.dp)
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(photo.aspectRatio)
                    }
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}
