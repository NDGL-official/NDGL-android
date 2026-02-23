package com.yapp.ndgl.feature.travel.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.google.android.gms.maps.model.LatLng
import com.yapp.ndgl.feature.travel.additinerary.AddItineraryRoute
import com.yapp.ndgl.feature.travel.additinerary.AddItineraryViewModel
import com.yapp.ndgl.feature.travel.addplace.AddPlaceRoute
import com.yapp.ndgl.feature.travel.addplace.AddPlaceViewModel
import com.yapp.ndgl.feature.travel.datepicker.DatePickerRoute
import com.yapp.ndgl.feature.travel.datepicker.DatePickerViewModel
import com.yapp.ndgl.feature.travel.followtravel.FollowTravelRoute
import com.yapp.ndgl.feature.travel.followtravel.FollowTravelViewModel
import com.yapp.ndgl.feature.travel.followtravel.placedetail.FollowPlaceDetailRoute
import com.yapp.ndgl.feature.travel.followtravel.placedetail.FollowPlaceDetailViewModel
import com.yapp.ndgl.feature.travel.mytravel.MyTravelRoute
import com.yapp.ndgl.feature.travel.placedetail.PlaceDetailRoute
import com.yapp.ndgl.feature.travel.placedetail.PlaceDetailViewModel
import com.yapp.ndgl.feature.travel.traveldetail.TravelDetailRoute
import com.yapp.ndgl.feature.travel.traveldetail.TravelDetailViewModel
import com.yapp.ndgl.navigation.Navigator
import com.yapp.ndgl.navigation.Route
import com.yapp.ndgl.navigation.model.RouteAlternativePlace
import com.yapp.ndgl.navigation.model.RouteTipContent

fun EntryProviderScope<NavKey>.travelEntry(navigator: Navigator) {
    entry<Route.Travel> {
        MyTravelRoute(
            navigateToTemplateSearch = {
                navigator.navigate(Route.TemplateSearch)
            },
            navigateToSettings = {
                navigator.navigate(Route.Settings)
            },
            navigateToFollowTravel = { travelId, days ->
                navigator.navigate(Route.FollowTravel(travelId, days))
            },
            navigateToTravelDetail = { travelId, days ->
                navigator.navigate(Route.TravelDetail(travelId, days))
            },
            navigateToTravelPlace = { placeId ->
                navigator.navigate(Route.PlaceDetail(placeId))
            },
            navigateToPopularTravelList = {
                navigator.navigate(Route.PopularTravelList)
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
            navigateToDatePicker = { templateId, tripDays ->
                navigator.navigate(Route.DatePicker(templateId, tripDays))
            },
            navigateToFollowPlaceDetail = { googlePlaceId, tipContent, alternativePlaces ->
                navigator.navigate(
                    Route.FollowPlaceDetail(
                        googlePlaceId = googlePlaceId,
                        tipContent = tipContent?.let { content ->
                            content.tips?.let { tips ->
                                RouteTipContent(creatorName = content.creatorName, tips = tips)
                            }
                        },
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
                factory.create(travelId = route.travelId, days = route.days)
            }
        TravelDetailRoute(
            viewModel = viewModel,
            navigateBack = { navigator.goBack() },
            navigateToTravelPlaceDetail = { googlePlaceId, tipContent, alternativePlaces ->
                navigator.navigate(
                    Route.PlaceDetail(
                        googlePlaceId = googlePlaceId,
                        tipContent = tipContent?.let { content ->
                            content.tips?.let { tips ->
                                RouteTipContent(creatorName = content.creatorName, tips = tips)
                            }
                        },
                        alternativePlaces = alternativePlaces?.map {
                            RouteAlternativePlace(id = it.id, name = it.name, thumbnail = it.thumbnail, placeType = it.placeType.name)
                        },
                    ),
                )
            },
            navigateToAddItinerary = { travelId, day, country, representativeLatitude, representativeLongitude ->
                navigator.navigate(Route.AddItinerary(travelId, day, country, representativeLatitude, representativeLongitude))
            },
        )
    }
    entry<Route.FollowPlaceDetail> { route ->
        val viewModel =
            hiltViewModel<FollowPlaceDetailViewModel, FollowPlaceDetailViewModel.Factory> { factory ->
                factory.create(
                    googlePlaceId = route.googlePlaceId,
                    tipContent = route.tipContent,
                    alternativePlaces = route.alternativePlaces,
                )
            }
        FollowPlaceDetailRoute(
            viewModel = viewModel,
            navigateBack = { navigator.goBack() },
            navigateToAlternativePlaceDetail = { googlePlaceId -> navigator.navigate(Route.PlaceDetail(googlePlaceId)) },
        )
    }
    entry<Route.PlaceDetail> { route ->
        val viewModel =
            hiltViewModel<PlaceDetailViewModel, PlaceDetailViewModel.Factory> { factory ->
                factory.create(
                    googlePlaceId = route.googlePlaceId,
                    tipContent = route.tipContent,
                    alternativePlaces = route.alternativePlaces,
                )
            }
        PlaceDetailRoute(
            viewModel = viewModel,
            navigateBack = { navigator.goBack() },
            navigateToAlternativePlaceDetail = { googlePlaceId -> navigator.navigate(Route.PlaceDetail(googlePlaceId)) },
        )
    }
    entry<Route.DatePicker> { route ->
        val viewModel =
            hiltViewModel<DatePickerViewModel, DatePickerViewModel.Factory> { factory ->
                factory.create(templateId = route.templateId, tripDays = route.tripDays)
            }
        DatePickerRoute(
            viewModel = viewModel,
            navigateBack = { navigator.goBack() },
            navigateToTravelDetail = { travelId, days ->
                timber.log.Timber.d("TravelEntry navigateToTravelDetail called: travelId=$travelId, days=$days")
                timber.log.Timber.d("TravelEntry calling navigateAndPopUpTo with Route.TravelDetail($travelId, $days)")
                navigator.navigateAndPopUpTo(
                    destination = Route.TravelDetail(travelId, days),
                    popRoutes = arrayOf(Route.DatePicker::class, Route.FollowTravel::class),
                )
                timber.log.Timber.d("TravelEntry navigateAndPopUpTo completed")
            },
        )
    }
    entry<Route.AddItinerary> { route ->
        val viewModel =
            hiltViewModel<AddItineraryViewModel, AddItineraryViewModel.Factory> { factory ->
                factory.create(
                    travelId = route.travelId,
                    day = route.day,
                    countryCode = route.countryCode,
                    representativeLatLng = LatLng(route.representativeLatitude, route.representativeLongitude),
                )
            }
        AddItineraryRoute(
            viewModel = viewModel,
            navigateBack = { navigator.goBack() },
            navigateToAddPlace = { placeId -> navigator.navigate(Route.AddPlace(placeId)) },
        )
    }
    entry<Route.AddPlace> { route ->
        val viewModel =
            hiltViewModel<AddPlaceViewModel, AddPlaceViewModel.Factory> { factory ->
                factory.create(placeId = route.placeId)
            }
        AddPlaceRoute(
            viewModel = viewModel,
            navigateBack = { navigator.goBack() },
        )
    }
}
