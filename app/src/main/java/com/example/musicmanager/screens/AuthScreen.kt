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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.glance.LocalContext
import com.example.musicmanager.navigation.NavigationEventHolder
import com.example.musicmanager.navigation.Screens
import com.example.musicmanager.ui.viewModels.LocalDatabaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.musicmanager.R
@Composable
fun AuthScreen() {
    var database = LocalDatabaseViewModel.current
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val login_failed = stringResource(R.string.login_failed)
    val register_success = stringResource(R.string.register_succes)

    AuthForm(
        onLogin = { email, password ->
             FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("AuthScreen", "Login successful")
                        // Navigate to the main screen or perform other actions
                        database.userAuthenticated = true
                        NavigationEventHolder.navigateTo(Screens.SongScreen.route)
                        database.authenticateUser()
                        errorMessage = null
                    } else {
                        Log.e("AuthScreen", login_failed + "${task.exception?.message}")
                        errorMessage = task.exception?.localizedMessage
                    }
                }
        },
        onRegister = { email, password ->
             FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("AuthScreen", "Registration successful")
                        successMessage = register_success
                        errorMessage = null
                    } else {
                        Log.e("AuthScreen", "Registration failed: ${task.exception?.message}")
                        errorMessage = task.exception?.localizedMessage
                    }
                }
        },
        errorMessage = errorMessage,
        successMessage = successMessage
    )
}

@Composable
fun AuthForm(
    onLogin: (String, String) -> Unit,
    onRegister: (String, String) -> Unit,
    errorMessage: String? = null,
    successMessage: String? = null
) {
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isLogin) stringResource(R.string.login) else stringResource(R.string.register),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (successMessage != null) {
            Text(
                text = successMessage,
                color = Color.Green,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = stringResource(R.string.email)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = stringResource(R.string.password)) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isLogin) onLogin(email, password)
                else onRegister(email, password)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLogin) stringResource(R.string.login) else stringResource(R.string.register))
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = { isLogin = !isLogin }) {
            Text(
                if (isLogin)
                    stringResource(id = R.string.no_account)
                else
                    stringResource(id = R.string.already_have_account),
            )
        }
    }
}