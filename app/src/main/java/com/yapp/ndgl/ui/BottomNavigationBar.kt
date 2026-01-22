package com.yapp.ndgl.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.yapp.ndgl.navigation.BottomNavTab
import com.yapp.ndgl.navigation.Route

@Composable
internal fun BottomNavigationBar(
    currentTab: Route,
    onTabSelected: (Route) -> Unit,
) {
    // FIXME 네비게이션 바 디자인 수정 및 추상화
    NavigationBar {
        BottomNavTab.entries.forEach { topLevelRoute ->
            NavigationBarItem(
                selected = currentTab == topLevelRoute.route,
                onClick = { onTabSelected(topLevelRoute.route) },
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = topLevelRoute.icon),
                        contentDescription = topLevelRoute.label,
                    )
                },
                label = { Text(text = topLevelRoute.label) },
            )
        }
    }
}
