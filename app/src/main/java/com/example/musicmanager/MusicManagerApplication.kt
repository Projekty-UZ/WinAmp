package com.example.musicmanager

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MusicManagerApplication: Application(){
    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { AppContainer().db }
    val repository by lazy { AppContainer().repository }
    override fun onCreate() {
        super.onCreate()
        AppContainer().provide(this, applicationScope)
    }
}
