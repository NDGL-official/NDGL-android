package com.yapp.ndgl.feature.travelhelper.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.yapp.ndgl.feature.travelhelper.main.TravelHelperRoute
import com.yapp.ndgl.navigation.Navigator
import com.yapp.ndgl.navigation.Route

fun EntryProviderScope<NavKey>.travelHelperEntry(navigator: Navigator) {
    entry<Route.TravelHelper> {
        TravelHelperRoute(
            navigateToSearch = { navigator.navigate(Route.TemplateSearch) },
            navigateToTravelDetail = { travelId, days ->
                navigator.navigate(Route.TravelDetail(travelId, days))
            },
            navigateToPopularTravelList = { navigator.navigate(Route.PopularTravelList) },
            navigateToPlaceDetail = { placeId -> navigator.navigate(Route.PlaceDetail(placeId)) },
        )
    }
}
