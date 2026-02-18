package com.yapp.ndgl.feature.travel.additinerary.component

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.core.ui.util.noRippleClickable
import com.yapp.ndgl.feature.travel.additinerary.SearchResult

@Composable
internal fun SearchEmptyContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_140_serach),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(140.dp),
        )
        Text(
            text = stringResource(R.string.add_itinerary_search_empty_hint),
            style = NDGLTheme.typography.bodyLgMedium,
            color = NDGLTheme.colors.black400,
        )
    }
}

@Composable
internal fun NoSearchResultContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_140_serach),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(140.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.add_itinerary_search_no_result_title),
            style = NDGLTheme.typography.titleMdSemiBold,
            color = NDGLTheme.colors.black700,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.add_itinerary_search_no_result_description),
            style = NDGLTheme.typography.bodyMdMedium,
            color = NDGLTheme.colors.black500,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
internal fun SearchResultItem(
    result: SearchResult,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .noRippleClickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(NDGLTheme.colors.black50),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_20_search),
                contentDescription = null,
                tint = NDGLTheme.colors.black400,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = result.name,
            style = NDGLTheme.typography.bodyLgRegular,
            color = NDGLTheme.colors.black700,
            maxLines = 1,
        )
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_24_arrow_up_right),
            contentDescription = null,
            tint = NDGLTheme.colors.black400,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchEmptyContentPreview() {
    NDGLTheme {
        SearchEmptyContent()
    }
}

@Preview(showBackground = true)
@Composable
private fun NoSearchResultContentPreview() {
    NDGLTheme {
        NoSearchResultContent()
    }
}
