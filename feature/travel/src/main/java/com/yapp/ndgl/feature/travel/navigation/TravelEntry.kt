package com.yapp.ndgl.feature.travel.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.yapp.ndgl.feature.travel.TravelRoute
import com.yapp.ndgl.feature.travel.detail.TravelDetailRoute
import com.yapp.ndgl.feature.travel.detail.TravelDetailViewModel
import com.yapp.ndgl.navigation.Navigator
import com.yapp.ndgl.navigation.Route

fun EntryProviderScope<NavKey>.travelEntry(navigator: Navigator) {
    entry<Route.Travel> {
        TravelRoute(
            navigateToDetail = { travelId ->
                navigator.navigate(Route.TravelDetail(travelId))
            },
        )
    }
    entry<Route.TravelDetail> { route ->
        val viewModel =
            hiltViewModel<TravelDetailViewModel, TravelDetailViewModel.Factory> { factory ->
                factory.create(travelId = route.travelId)
            }
        TravelDetailRoute(viewModel = viewModel)
    }
}
