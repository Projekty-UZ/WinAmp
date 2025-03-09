package com.example.musicmanager.navigation

sealed class Screens (val route: String){
    object SplashScreen: Screens("splashscreen")
    object SongScreen: Screens("songs")
    object PlaylistScreen: Screens("playlists")
    object AddSongScreen: Screens("add_song/{ytLink}"){
        fun createRoute(ytLink: String): String {
            return "add_song/$ytLink"
        }
    }
    object SongControlScreen: Screens("song_control")
    object StepCounterScreen: Screens("step_counter")
    object LocationTrackerScreen: Screens("location_tracker")
}