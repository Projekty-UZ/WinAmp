package com.example.musicmanager.navigation

/**
 * Sealed class representing the different screens in the application.
 * Each screen is associated with a unique route for navigation purposes.
 *
 * @property route The navigation route associated with the screen.
 */
sealed class Screens(val route: String) {
    /**
     * Object representing the Splash Screen.
     * Route: "splashscreen"
     */
    object SplashScreen : Screens("splashscreen")

    /**
     * Object representing the Song Screen.
     * Route: "songs"
     */
    object SongScreen : Screens("songs")

    /**
     * Object representing the Playlist Screen.
     * Route: "playlists"
     */
    object PlaylistScreen : Screens("playlists")

    /**
     * Object representing the Add Song Screen.
     * Route: "add_song/{ytLink}"
     */
    object AddSongScreen : Screens("add_song/{ytLink}") {
        /**
         * Function to create a route for the Add Song Screen with a specific YouTube link.
         *
         * @param ytLink The YouTube link to include in the route.
         * @return The complete route string with the YouTube link.
         */
        fun createRoute(ytLink: String): String {
            return "add_song/$ytLink"
        }
    }

    /**
     * Object representing the Song Control Screen.
     * Route: "song_control"
     */
    object SongControlScreen : Screens("song_control")

    /**
     * Object representing the Step Counter Screen.
     * Route: "step_counter"
     */
    object StepCounterScreen : Screens("step_counter")

    /**
     * Object representing the Location Tracker Screen.
     * Route: "location_tracker"
     */
    object LocationTrackerScreen : Screens("location_tracker")

    /**
     * Object representing the Authentication Screen.
     * Route: "auth"
     */
    object AuthScreen : Screens("auth")
}