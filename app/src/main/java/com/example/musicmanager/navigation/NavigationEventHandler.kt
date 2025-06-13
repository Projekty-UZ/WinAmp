package com.example.musicmanager.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Object responsible for managing navigation events within the application.
 * Provides functionality to trigger navigation to a specific route and reset navigation state.
 */
object NavigationEventHolder {
    // Mutable state flow to hold the current navigation route.
    private val _navigateTo = MutableStateFlow<String?>(null)

    // Public state flow exposing the current navigation route.
    val navigateTo: StateFlow<String?> = _navigateTo

    /**
     * Triggers navigation to the specified route.
     *
     * @param route The route to navigate to.
     */
    fun navigateTo(route: String) {
        _navigateTo.value = route
    }

    /**
     * Resets the navigation state after navigation is complete.
     */
    fun onNavigationComplete() {
        _navigateTo.value = null
    }
}