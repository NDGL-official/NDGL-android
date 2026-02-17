package com.yapp.ndgl.feature.travel.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.yapp.ndgl.feature.travel.datepicker.DatePickerRoute
import com.yapp.ndgl.feature.travel.datepicker.DatePickerViewModel
import com.yapp.ndgl.feature.travel.followtravel.FollowTravelRoute
import com.yapp.ndgl.feature.travel.followtravel.FollowTravelViewModel
import com.yapp.ndgl.feature.travel.followtravel.placedetail.FollowPlaceDetailRoute
import com.yapp.ndgl.feature.travel.followtravel.placedetail.FollowPlaceDetailViewModel
import com.yapp.ndgl.feature.travel.placedetail.PlaceDetailRoute
import com.yapp.ndgl.feature.travel.placedetail.PlaceDetailViewModel
import com.yapp.ndgl.feature.travel.travel.TravelRoute
import com.yapp.ndgl.feature.travel.traveldetail.TravelDetailRoute
import com.yapp.ndgl.feature.travel.traveldetail.TravelDetailViewModel
import com.yapp.ndgl.navigation.Navigator
import com.yapp.ndgl.navigation.Route
import com.yapp.ndgl.navigation.model.RouteAlternativePlace
import com.yapp.ndgl.navigation.model.RouteTipContent

fun EntryProviderScope<NavKey>.travelEntry(navigator: Navigator) {
    entry<Route.Travel> {
        TravelRoute(
            navigateToFollowTravel = { travelId, days ->
                navigator.navigate(Route.FollowTravel(travelId, days))
            },
            navigateToTravelDetail = { travelId ->
                navigator.navigate(Route.TravelDetail(travelId))
            },
        )
    }
    entry<Route.FollowTravel> { route ->
        val viewModel =
            hiltViewModel<FollowTravelViewModel, FollowTravelViewModel.Factory> { factory ->
                factory.create(travelId = route.travelId, days = route.days)
            }
        FollowTravelRoute(
            viewModel = viewModel,
            navigateBack = { navigator.goBack() },
            navigateToDatePicker = { tripDays ->
                navigator.navigate(Route.DatePicker(tripDays))
            },
            navigateToFollowPlaceDetail = { placeId, tipContent, alternativePlaces ->
                navigator.navigate(
                    Route.FollowPlaceDetail(
                        placeId = placeId,
                        tipContent = tipContent?.let { RouteTipContent(creatorName = it.creatorName, tips = it.tips) },
                        alternativePlaces = alternativePlaces?.map {
                            RouteAlternativePlace(id = it.id, name = it.name, thumbnail = it.thumbnail, placeType = it.placeType.name)
                        } ?: emptyList(),
                    ),
                )
            },
        )
    }
    entry<Route.TravelDetail> { route ->
        val viewModel =
            hiltViewModel<TravelDetailViewModel, TravelDetailViewModel.Factory> { factory ->
                factory.create(travelId = route.travelId)
            }
        TravelDetailRoute(
            viewModel = viewModel,
            navigateBack = { navigator.goBack() },
            navigateToTravelPlaceDetail = { placeId, tipContent, alternativePlaces ->
                navigator.navigate(
                    Route.PlaceDetail(
                        placeId = placeId,
                        tipContent = tipContent?.let { RouteTipContent(creatorName = it.creatorName, tips = it.tips) },
                        alternativePlaces = alternativePlaces?.map {
                            RouteAlternativePlace(id = it.id, name = it.name, thumbnail = it.thumbnail, placeType = it.placeType.name)
                        } ?: emptyList(),
                    ),
                )
            },
        )
    }
    entry<Route.FollowPlaceDetail> { route ->
        val viewModel =
            hiltViewModel<FollowPlaceDetailViewModel, FollowPlaceDetailViewModel.Factory> { factory ->
                factory.create(
                    placeId = route.placeId,
                    tipContent = route.tipContent,
                    alternativePlaces = route.alternativePlaces,
                )
            }
        FollowPlaceDetailRoute(
            viewModel = viewModel,
            navigateBack = { navigator.goBack() },
        )
    }
    entry<Route.PlaceDetail> { route ->
        val viewModel =
            hiltViewModel<PlaceDetailViewModel, PlaceDetailViewModel.Factory> { factory ->
                factory.create(
                    placeId = route.placeId,
                    tipContent = route.tipContent,
                    alternativePlaces = route.alternativePlaces,
                )
            }
        PlaceDetailRoute(
            viewModel = viewModel,
            navigateBack = { navigator.goBack() },
        )
    }
    entry<Route.DatePicker> { route ->
        val viewModel =
            hiltViewModel<DatePickerViewModel, DatePickerViewModel.Factory> { factory ->
                factory.create(tripDays = route.tripDays)
            }
        DatePickerRoute(
            viewModel = viewModel,
            navigateBack = { navigator.goBack() },
        )
    }
}
