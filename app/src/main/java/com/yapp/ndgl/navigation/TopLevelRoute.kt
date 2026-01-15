package com.yapp.ndgl.navigation

import androidx.annotation.DrawableRes
import androidx.navigation3.runtime.NavKey
import com.yapp.designsystem.R

enum class TopLevelRoute(
    @DrawableRes val icon: Int,
    val label: String,
    val navKey: NavKey,
) {
    HOME(
        icon = R.drawable.ic_nav_home,
        label = "Home",
        navKey = Route.Home,
    ),
    TRAVEL(
        icon = R.drawable.ic_nav_travel,
        label = "Travel",
        navKey = Route.Travel,
    ),
    TRAVEL_HELPER(
        icon = R.drawable.ic_nav_helper,
        label = "Travel Helper",
        navKey = Route.TravelHelper,
    ),
    ;
}