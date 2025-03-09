package com.example.musicmanager.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object NavigationEventHolder {
    private val _navigateTo = MutableStateFlow<String?>(null)
    val navigateTo: StateFlow<String?> = _navigateTo

    fun navigateTo(route: String) {
        _navigateTo.value = route
    }

    fun onNavigationComplete() {
        _navigateTo.value = null
    }
}