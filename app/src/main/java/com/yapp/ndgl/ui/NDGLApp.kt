package com.yapp.ndgl.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.yapp.travel.helper.navigation.travelHelperEntry
import com.yapp.home.navigation.homeEntry
import com.yapp.ndgl.navigation.BottomNavTab
import com.yapp.navigation.Navigator
import com.yapp.navigation.Route
import com.yapp.navigation.rememberNavigationState
import com.yapp.navigation.toEntries
import com.yapp.travel.navigation.travelEntry

@Composable
fun NDGLApp() {
    val navigationState = rememberNavigationState(
        startRoute = Route.Home, topLevelKeys = BottomNavTab.entries.map { it.route }.toSet(),
    )
    val navigator = remember { Navigator(navigationState) }

    val entryProvider = entryProvider {
        homeEntry(navigator)
        travelEntry(navigator)
        travelHelperEntry(navigator)
    }

    val shouldShowBottomBar = remember(navigationState.currentKey) { navigationState.currentKey in navigationState.topLevelKeys }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = shouldShowBottomBar,
            ) {
                BottomNavigationBar(
                    currentTab = navigationState.currentTopLevelKey as Route,
                    onTabSelected = { key -> navigator.navigate(key) },
                )
            }
        },
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onBack = navigator::goBack,
            entries = navigationState.toEntries(entryProvider),
        )
    }
}

