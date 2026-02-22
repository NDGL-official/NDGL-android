package com.yapp.ndgl.navigation

import androidx.navigation3.runtime.NavKey
import kotlin.reflect.KClass

class Navigator(val state: NavigationState) {

    fun navigate(key: NavKey) {
        when (key) {
            state.currentTopLevelKey -> clearSubStack()
            in state.topLevelKeys -> goToTopLevel(key)
            else -> goToKey(key)
        }
    }

    fun navigateAndPopUpTo(destination: NavKey, vararg popRoutes: KClass<out NavKey>) {
        popRoutes.forEach { routeClass ->
            val toRemove = state.currentSubStack.filter { stackItem ->
                routeClass.isInstance(stackItem)
            }
            toRemove.forEach { route ->
                state.currentSubStack.remove(route)
            }
        }

        navigate(destination)
    }

    fun goBack() {
        when (state.currentKey) {
            state.currentTopLevelKey -> {
                state.topLevelStack.removeLastOrNull()
            }

            else -> state.currentSubStack.removeLastOrNull()
        }
    }

    private fun goToKey(key: NavKey) {
        state.currentSubStack.apply {
            remove(key)
            add(key)
        }
    }

    private fun goToTopLevel(key: NavKey) {
        state.topLevelStack.apply {
            remove(key)
            add(key)
        }
    }

    private fun clearSubStack() {
        state.currentSubStack.run {
            if (size > 1) subList(1, size).clear()
        }
    }
}
