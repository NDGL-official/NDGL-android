package com.yapp.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.yapp.home.HomeRoute
import com.yapp.navigation.Navigator
import com.yapp.navigation.Route

fun EntryProviderScope<NavKey>.homeEntry(navigator: Navigator) {
    entry<Route.Home> {
        HomeRoute()
    }
}
