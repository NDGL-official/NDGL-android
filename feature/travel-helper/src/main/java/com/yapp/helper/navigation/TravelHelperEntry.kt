package com.yapp.helper.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.yapp.helper.TravelHelperRoute
import com.yapp.ndgl.navigation.Navigator
import com.yapp.ndgl.navigation.Route

fun EntryProviderScope<NavKey>.travelHelperEntry(navigator: Navigator) {
    entry<Route.TravelHelper> {
        TravelHelperRoute()
    }
}