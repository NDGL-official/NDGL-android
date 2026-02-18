package com.yapp.ndgl.feature.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.yapp.ndgl.feature.home.main.HomeRoute
import com.yapp.ndgl.feature.home.popular.PopularTravelListRoute
import com.yapp.ndgl.feature.home.search.TemplateSearchRoute
import com.yapp.ndgl.navigation.Navigator
import com.yapp.ndgl.navigation.Route

fun EntryProviderScope<NavKey>.homeEntry(
    navigator: Navigator,
) {
    entry<Route.Home> {
        HomeRoute(
            navigateToTemplateSearch = {
                navigator.navigate(Route.TemplateSearch)
            },
            navigateToFollowTravel = { travelId, days ->
                navigator.navigate(Route.FollowTravel(travelId = travelId, days = days))
            },
            navigateToPopularTravelList = {
                navigator.navigate(Route.PopularTravelList)
            },
        )
    }
    entry<Route.TemplateSearch> {
        TemplateSearchRoute(
            goBack = {
                navigator.goBack()
            },
            navigateToFollowTravel = { travelId, days ->
                navigator.navigate(Route.FollowTravel(travelId = travelId, days = days))
            },
        )
    }
    entry<Route.PopularTravelList> {
        PopularTravelListRoute(
            goBack = {
                navigator.goBack()
            },
            navigateToTemplateSearch = {
                navigator.navigate(Route.TemplateSearch)
            },
            navigateToFollowTravel = { travelId, days ->
                navigator.navigate(Route.FollowTravel(travelId = travelId, days = days))
            },
        )
    }
}
