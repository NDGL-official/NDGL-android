package com.yapp.ndgl.data.travel.repository

import com.yapp.ndgl.data.travel.api.RouteApi
import com.yapp.ndgl.data.travel.model.ComputeRoutesRequest
import com.yapp.ndgl.data.travel.model.LatLng
import com.yapp.ndgl.data.travel.model.Location
import com.yapp.ndgl.data.travel.model.RouteInfo
import com.yapp.ndgl.data.travel.model.RouteLocation
import com.yapp.ndgl.data.travel.model.TravelMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteRepository @Inject constructor(
    private val routeApi: RouteApi,
) {
    suspend fun computeRoute(
        originLatitude: Double,
        originLongitude: Double,
        destinationLatitude: Double,
        destinationLongitude: Double,
        travelMode: TravelMode,
    ): RouteInfo? {
        val request = ComputeRoutesRequest(
            origin = RouteLocation(
                location = Location(
                    latLng = LatLng(
                        latitude = originLatitude,
                        longitude = originLongitude,
                    ),
                ),
            ),
            destination = RouteLocation(
                location = Location(
                    latLng = LatLng(
                        latitude = destinationLatitude,
                        longitude = destinationLongitude,
                    ),
                ),
            ),
            travelMode = travelMode,
        )

        val response = routeApi.computeRoutes(request)
        return response.routes.firstOrNull()
    }
}
