package com.yapp.travel.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.yapp.navigation.Navigator
import com.yapp.navigation.Route
import com.yapp.travel.detail.TravelDetailRoute
import com.yapp.travel.detail.TravelDetailViewModel
import com.yapp.travel.travel.TravelRoute

fun EntryProviderScope<NavKey>.travelEntry(navigator: Navigator) {
    entry<Route.Travel> {
        TravelRoute(onNavigateToDetail = { travelId ->
            navigator.navigate(Route.TravelDetail(travelId))
        })
    }
    entry<Route.TravelDetail> { route ->
        val viewModel = hiltViewModel<TravelDetailViewModel, TravelDetailViewModel.Factory> { factory ->
            factory.create(travelId = route.travelId)
        }
        TravelDetailRoute(viewModel = viewModel)
    }
}
