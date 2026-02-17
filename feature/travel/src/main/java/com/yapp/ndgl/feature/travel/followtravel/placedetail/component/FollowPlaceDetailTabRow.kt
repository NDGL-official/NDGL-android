package com.yapp.ndgl.feature.travel.followtravel.placedetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.feature.travel.model.PlaceDetailTab
import androidx.compose.material3.SecondaryTabRow as SecondaryTabRow

@Composable
internal fun FollowPlaceDetailTabRow(
    selectedTab: PlaceDetailTab,
    onTabSelected: (PlaceDetailTab) -> Unit,
) {
    val tabs = PlaceDetailTab.entries
    val selectedIndex = tabs.indexOf(selectedTab)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
    ) {
        SecondaryTabRow(
            selectedTabIndex = selectedIndex,
            modifier = Modifier.fillMaxWidth(),
            containerColor = NDGLTheme.colors.white,
            contentColor = NDGLTheme.colors.black900,
            indicator = {},
            divider = {},
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = index == selectedIndex
                Tab(
                    selected = isSelected,
                    onClick = { onTabSelected(tab) },
                    modifier = Modifier.background(
                        if (isSelected) NDGLTheme.colors.black100 else NDGLTheme.colors.white,
                    ),
                    text = {
                        Text(
                            stringResource(tab.titleRes),
                            color = NDGLTheme.colors.black600,
                            style = NDGLTheme.typography.bodyMdSemiBold,
                            textAlign = TextAlign.Center,
                        )
                    },
                )
            }
        }
    }
}
