package com.example.musicmanager.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.musicmanager.database.models.Song
import com.example.musicmanager.ui.theme.viewModels.DatabaseViewModel
import com.example.musicmanager.ui.theme.viewModels.LocalDatabaseViewModel

@Composable
fun AddSongScreen(navController: NavHostController) {
    val databaseViewModel = LocalDatabaseViewModel.current
    if(!Python.isStarted()) {
        Python.start(AndroidPlatform(LocalContext.current))
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
            val infotext = remember { mutableStateOf("") }
            var yt_link by remember { mutableStateOf("") }
            var isenabled by remember { mutableStateOf(true) }
            TextField(
                value = yt_link,
                onValueChange = { yt_link = it},
                placeholder = { Text("Enter YouTube Link") },
                modifier = Modifier.width(300.dp),
                singleLine = true,
                )
            Button(
                enabled = isenabled,
                onClick = {
                    isenabled = false
                    python_script_button(module,yt_link,infotext,databaseViewModel)
                    isenabled = true
                },

                ) {
                Text("Download Song")
            }
            Text(infotext.value)
        }
    }
}
fun python_script_button(module:PyObject,yt_link:String,infotext: MutableState<String>,databaseViewModel: DatabaseViewModel){
    val validated = validate_input(yt_link)
    var return_table = emptyList<String>()
    if(validated){
        download_from_yt(module,yt_link)
        return_table = module.callAttr("get_message").asList().map { it.toString() }
        println(return_table)
        if(return_table[0] == "Downloaded") {
            infotext.value = "Downloaded"
            val song = Song(id=0,title=return_table[1],artist=return_table[2],duration=return_table[3].toInt(),pathToFile=return_table[4])
            databaseViewModel.addSong(song)
        }
        else{
            infotext.value = "Download Failed"
        }
    }else{
        infotext.value = "Invalid YouTube Link"
    }
}

fun download_from_yt(module:PyObject,yt_link: String){
    module.callAttr("download_from_yt",yt_link)
}
fun validate_input(yt_link:String):Boolean{
    val pattern = """^(http(s)?://)?((w){3}.)?youtu(be|.be)?(\.com)?/.+""".toRegex()
    return pattern.matches(yt_link)

}