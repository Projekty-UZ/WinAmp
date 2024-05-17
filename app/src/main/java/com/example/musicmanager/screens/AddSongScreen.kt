package com.example.musicmanager.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

@Composable
fun AddSongScreen() {
    if(!Python.isStarted()) {
        Python.start(AndroidPlatform(LocalContext.current))
    }
    val py = Python.getInstance()
    val module= py.getModule("test")
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            val text = remember { mutableStateOf("") }
            Button(onClick = {
                module.callAttr("download_from_yt","https://www.youtube.com/watch?v=suAR1PYFNYA","Houdini")
                text.value = "Python script executed successfully!"
            }) {
                Text("Run Python Script")
            }
            Text(text.value)
        }
    }
}