package com.yapp.ndgl.navigation

import androidx.annotation.DrawableRes
import androidx.navigation3.runtime.NavKey
import com.yapp.designsystem.R

enum class TopLevelRoute(
    @get:DrawableRes val icon: Int,
    val label: String,
    val navKey: NavKey,
) {
    TRAVEL_HELPER(
        icon = R.drawable.ic_nav_helper,
        label = "여행 도구",
        navKey = Route.TravelHelper,
    ),
    HOME(
        icon = R.drawable.ic_nav_home,
        label = "홈",
        navKey = Route.Home,
    ),
    TRAVEL(
        icon = R.drawable.ic_nav_travel,
        label = "여행",
        navKey = Route.Travel,
    ),
    ;
}
