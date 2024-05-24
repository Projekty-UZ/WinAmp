package com.example.musicmanager.ui.viewModels

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AddSongScreenViewModel:ViewModel() {
    var yt_link = mutableStateOf("")
    var loading = mutableStateOf(false)
    var progress = mutableFloatStateOf(0f)
}