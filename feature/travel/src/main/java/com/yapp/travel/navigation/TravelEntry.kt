package com.yapp.travel.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.yapp.ndgl.navigation.Navigator
import com.yapp.ndgl.navigation.Route
import com.yapp.travel.detail.TravelDetailRoute
import com.yapp.travel.travel.TravelRoute

fun EntryProviderScope<NavKey>.travelEntry(navigator: Navigator) {
    entry<Route.Travel> {
        TravelRoute(onNavigateToDetail = { navigator.navigate(Route.TravelDetail) })
    }
    entry<Route.TravelDetail> {
        TravelDetailRoute()
    }
}