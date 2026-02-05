package com.yapp.ndgl.navigation

import androidx.annotation.DrawableRes
import com.yapp.ndgl.core.ui.R

enum class BottomNavTab(
    @get:DrawableRes val icon: Int,
    val label: String,
    val route: Route,
) {
    TRAVEL_HELPER(
        icon = R.drawable.ic_24_tool,
        label = "여행 도구",
        route = Route.TravelHelper,
    ),
    HOME(
        icon = R.drawable.ic_24_home,
        label = "홈",
        route = Route.Home,
    ),
    TRAVEL(
        icon = R.drawable.ic_24_bag,
        label = "여행",
        route = Route.Travel,
    ),
}
