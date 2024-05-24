package com.example.musicmanager.navigation

sealed class Screens (val route: String){
    object SplashScreen: Screens("splashscreen")
    object SongScreen: Screens("songs")
    object PlaylistScreen: Screens("playlists")
    object AddSongScreen: Screens("add_song")
}