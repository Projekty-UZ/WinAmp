package com.example.musicmanager.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.LocalContext
import com.example.musicmanager.MainActivity
import com.example.musicmanager.database.models.AuthData
import com.example.musicmanager.navigation.NavigationEventHolder
import com.example.musicmanager.navigation.Screens
import com.example.musicmanager.ui.viewModels.LocalDatabaseViewModel

@Composable
fun AuthScreen() {
    val database = LocalDatabaseViewModel.current
    var userData by remember { mutableStateOf(AuthData(1, "", "")) }

    LaunchedEffect(key1 = true) {
        userData = database.getAuthData()
        Log.d("AuthScreen", "User data: $userData")
    }

    if(userData == null || userData.password.isEmpty() || userData.recoveryEmail.isEmpty()) {
        // Show the auth form
        AuthForm(
            onAuthDataSubmitted = { newUserData ->
                userData = newUserData
                // Save the new auth data to the database
                database.insertAuthData(newUserData.password, newUserData.recoveryEmail)
                database.authenticateUser()
                database.userAuthenticated = true
            }
        )
    } else {
        // Show the enter password form
        EnterPasswordForm(
            userData = userData,
            onPasswordCorrect = {
                database.userAuthenticated = true
                NavigationEventHolder.navigateTo(Screens.SongScreen.route)
                database.authenticateUser()
            }
        )
    }

}

@Composable
fun AuthForm(onAuthDataSubmitted: (AuthData) -> Unit) {
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var recoveryEmail by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = repeatPassword,
            onValueChange = { repeatPassword = it },
            label = { Text("Repeat Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = recoveryEmail,
            onValueChange = { recoveryEmail = it },
            label = { Text("Recovery Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if(password != repeatPassword) {
                    error = "Passwords do not match"
                    return@Button
                }
                //hash the password

                val newUserData = AuthData(1, password, recoveryEmail)
                onAuthDataSubmitted(newUserData)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }

        if(error != null) {
            Text(
                text = error!!,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun EnterPasswordForm(userData: AuthData, onPasswordCorrect: () -> Unit) {
    var enteredPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = enteredPassword,
            onValueChange = { enteredPassword = it },
            label = { Text("Enter Password") },
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (enteredPassword == userData.password) {
                    onPasswordCorrect()
                } else {
                    errorMessage = "Incorrect password"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}