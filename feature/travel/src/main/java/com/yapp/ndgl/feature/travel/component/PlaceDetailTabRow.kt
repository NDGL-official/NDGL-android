package com.yapp.ndgl.feature.travel.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.feature.travel.model.PlaceDetailTab

@Composable
internal fun PlaceDetailTabRow(
    selectedTab: PlaceDetailTab,
    onTabSelected: (PlaceDetailTab) -> Unit,
) {
    val tabs = PlaceDetailTab.entries
    val selectedIndex = tabs.indexOf(selectedTab)
    Column(modifier = Modifier.fillMaxWidth()) {
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
                            if (isSelected) NDGLTheme.colors.white else NDGLTheme.colors.black100,
                        ),
                        text = {
                            Text(
                                stringResource(tab.titleRes),
                                color = if (isSelected) NDGLTheme.colors.black600 else NDGLTheme.colors.black400,
                                style = NDGLTheme.typography.bodyMdSemiBold,
                                textAlign = TextAlign.Center,
                            )
                        },
                    )
                }
            }
        }

        HorizontalDivider(thickness = 1.dp, color = NDGLTheme.colors.black200)
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceDetailTabRowPreview() {
    NDGLTheme {
        PlaceDetailTabRow(
            selectedTab = PlaceDetailTab.INFO,
            onTabSelected = {},
        )
    }
}
