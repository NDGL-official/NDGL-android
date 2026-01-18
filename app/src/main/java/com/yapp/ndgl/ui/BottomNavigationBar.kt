package com.yapp.ndgl.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation3.runtime.NavKey
import com.yapp.ndgl.navigation.Route
import com.yapp.ndgl.navigation.TopLevelRoute

@Composable
internal fun BottomNavigationBar(
    currentTab : Route,
    onTabSelected : (Route) -> Unit,
) {
    NavigationBar {
        TopLevelRoute.entries.forEach { topLevelRoute ->
            NavigationBarItem(
                selected = currentTab == topLevelRoute.navKey,
                onClick = { onTabSelected(topLevelRoute.navKey as Route) },
                icon = {
                    Icon(
                        painter = painterResource(id = topLevelRoute.icon),
                        contentDescription = topLevelRoute.label
                    )
                },
                label = { Text(text = topLevelRoute.label) }
            )
        }
    }
}
