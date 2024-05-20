package com.example.musicmanager

import android.app.Application

class MusicManagerApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        AppContainer().provide(this)
    }
}
