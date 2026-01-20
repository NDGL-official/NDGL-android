package com.yapp.ndgl.navigation

import androidx.annotation.DrawableRes
import com.yapp.designsystem.R

enum class BottomNavTab(
    @get:DrawableRes val icon: Int,
    val label: String,
    val route: Route,
) {
    TRAVEL_HELPER(
        icon = R.drawable.ic_nav_helper,
        label = "여행 도구",
        route = Route.TravelHelper,
    ),
    HOME(
        icon = R.drawable.ic_nav_home,
        label = "홈",
        route = Route.Home,
    ),
    TRAVEL(
        icon = R.drawable.ic_nav_travel,
        label = "여행",
        route = Route.Travel,
    ),
    ;
}
