package com.example.musicmanager.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.musicmanager.database.models.Song
import com.example.musicmanager.ui.viewModels.AddSongScreenViewModel
import com.example.musicmanager.ui.viewModels.DatabaseViewModel
import com.example.musicmanager.ui.viewModels.LocalDatabaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AddSongScreen() {
    val databaseViewModel = LocalDatabaseViewModel.current
    val coroutineScope = rememberCoroutineScope()
    val addSongScreenViewModel: AddSongScreenViewModel = viewModel()
    val context = LocalContext.current
    if(!Python.isStarted()) {
        Python.start(AndroidPlatform(context))
    }
    val py = Python.getInstance()
    val module= py.getModule("test")
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = addSongScreenViewModel.yt_link.value,
                onValueChange = { addSongScreenViewModel.yt_link.value = it},
                placeholder = { Text("Enter YouTube Link") },
                modifier = Modifier.width(300.dp),
                singleLine = true,
                )
            Button(
                enabled = !addSongScreenViewModel.loading.value,
                onClick = {
                    addSongScreenViewModel.loading.value = true
                    addSongScreenViewModel.progress.floatValue = 0f
                    coroutineScope.launch {
                        // Start the python script in a background thread
                        withContext(Dispatchers.IO) {
                            python_script_button(module, addSongScreenViewModel.yt_link.value, context,addSongScreenViewModel, databaseViewModel)
                        }

                    }
                    addSongScreenViewModel.viewModelScope.launch{
                        while(module.callAttr("get_progress").toFloat() == 1f){
                            delay(100)
                        }
                        while(addSongScreenViewModel.loading.value){
                            addSongScreenViewModel.progress.floatValue = module.callAttr("get_progress").toFloat()
                            Log.d("Progress", addSongScreenViewModel.progress.floatValue.toString())
                            delay(500)
                            if(addSongScreenViewModel.progress.floatValue == 1f){
                                addSongScreenViewModel.loading.value = false
                                addSongScreenViewModel.progress.floatValue = 0f
                            }
                        }

                    }
                },
                ) {
                Text("Download Song")
            }
            if(addSongScreenViewModel.loading.value){
                LinearProgressIndicator(
                    progress = addSongScreenViewModel.progress.floatValue,
                    modifier = Modifier.width(300.dp),
                )
            }
        }
    }
}
fun python_script_button(module:PyObject, yt_link:String, context : Context, addSongScreenViewModel: AddSongScreenViewModel, databaseViewModel: DatabaseViewModel){
    val validated = validate_input(yt_link)
    if(validated){
        download_from_yt(module,yt_link)
        val returnTable = module.callAttr("get_message").asList().map { it.toString() }
        if(returnTable[0] == "Downloaded") {
            databaseViewModel.viewModelScope.launch(Dispatchers.Main) {
                Toast.makeText(context, "Successful Download", Toast.LENGTH_SHORT).show()
            }
            val song = Song(id=0,title=returnTable[1],artist=returnTable[2],duration=returnTable[3].toInt(),pathToFile=returnTable[4])
            databaseViewModel.addSong(song)
        }
        else{
            databaseViewModel.viewModelScope.launch(Dispatchers.Main) {
                Toast.makeText(context, "Download Failed", Toast.LENGTH_SHORT).show()
                addSongScreenViewModel.loading.value = false
                addSongScreenViewModel.progress.floatValue = 0f
            }
        }
    }else{
        databaseViewModel.viewModelScope.launch(Dispatchers.Main) {
            Toast.makeText(context, "Enter Valid YT Link", Toast.LENGTH_SHORT).show()
            addSongScreenViewModel.loading.value = false
            addSongScreenViewModel.progress.floatValue = 0f
        }
    }
}

fun download_from_yt(module:PyObject,yt_link: String){
    module.callAttr("download_from_yt",yt_link)
}
fun validate_input(yt_link:String):Boolean{
    val pattern = """^(http(s)?://)?((w){3}.)?youtu(be|.be)?(\.com)?/.+""".toRegex()
    return pattern.matches(yt_link)

}