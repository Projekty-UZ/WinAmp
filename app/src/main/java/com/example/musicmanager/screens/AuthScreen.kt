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

/**
 * Composable function for rendering the authentication screen.
 * Provides functionality for user login, registration, and password reset.
 * Displays error and success messages based on the authentication process.
 */
@Composable
fun AuthScreen() {
    var database = LocalDatabaseViewModel.current // ViewModel for database operations.
    var errorMessage by remember { mutableStateOf<String?>(null) } // State for error messages.
    var successMessage by remember { mutableStateOf<String?>(null) } // State for success messages.
    val login_failed = stringResource(R.string.login_failed) // String resource for login failure message.
    val register_success = stringResource(R.string.register_succes) // String resource for registration success message.
    var email_sent = stringResource(R.string.password_reset_sent) // String resource for password reset success message.

    // Render the authentication form with callbacks for login, registration, and password reset.
    AuthForm(
        onLogin = { email, password ->
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("AuthScreen", "Login successful") // Log successful login.
                        database.userAuthenticated = true // Update authentication state in the database.
                        NavigationEventHolder.navigateTo(Screens.SongScreen.route) // Navigate to the song screen.
                        database.authenticateUser() // Perform additional authentication actions.
                        errorMessage = null // Clear error message.
                    } else {
                        Log.e("AuthScreen", login_failed + "${task.exception?.message}") // Log login failure.
                        errorMessage = task.exception?.localizedMessage // Set error message.
                    }
                }
        },
        onRegister = { email, password ->
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("AuthScreen", "Registration successful") // Log successful registration.
                        successMessage = register_success // Set success message.
                        errorMessage = null // Clear error message.
                    } else {
                        Log.e("AuthScreen", "Registration failed: ${task.exception?.message}") // Log registration failure.
                        errorMessage = task.exception?.localizedMessage // Set error message.
                    }
                }
        },
        onResetPassword = { email ->
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("AuthScreen", "Password reset email sent.") // Log successful password reset.
                        successMessage = email_sent // Set success message.
                        errorMessage = null // Clear error message.
                    } else {
                        Log.e("AuthScreen", "Password reset failed: ${task.exception?.message}") // Log password reset failure.
                        errorMessage = task.exception?.localizedMessage // Set error message.
                        successMessage = null // Clear success message.
                    }
                }
        },
        errorMessage = errorMessage, // Pass error message to the form.
        successMessage = successMessage // Pass success message to the form.
    )
}

/**
 * Composable function for rendering the authentication form.
 * Provides input fields for email and password, and buttons for login, registration, and password reset.
 * Displays error and success messages based on user actions.
 *
 * @param onLogin Callback function for handling user login.
 * @param onRegister Callback function for handling user registration.
 * @param onResetPassword Callback function for handling password reset.
 * @param errorMessage Optional error message to display.
 * @param successMessage Optional success message to display.
 */
@Composable
fun AuthForm(
    onLogin: (String, String) -> Unit,
    onRegister: (String, String) -> Unit,
    onResetPassword: (String) -> Unit,
    errorMessage: String? = null,
    successMessage: String? = null
) {
    var isLogin by remember { mutableStateOf(true) } // State to toggle between login and registration.
    var email by remember { mutableStateOf("") } // State for email input.
    var password by remember { mutableStateOf("") } // State for password input.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp), // Apply padding to the form.
        horizontalAlignment = Alignment.CenterHorizontally, // Center content horizontally.
        verticalArrangement = Arrangement.Center // Center content vertically.
    ) {
        Text(
            text = if (isLogin) stringResource(R.string.login) else stringResource(R.string.register), // Display login or register text.
            style = MaterialTheme.typography.headlineMedium, // Apply headline style.
            modifier = Modifier.padding(bottom = 24.dp) // Add bottom padding.
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage, // Display error message.
                color = Color.Red, // Set text color to red.
                style = MaterialTheme.typography.bodyMedium, // Apply body style.
                modifier = Modifier.padding(bottom = 16.dp) // Add bottom padding.
            )
        }

        if (successMessage != null) {
            Text(
                text = successMessage, // Display success message.
                color = Color.Green, // Set text color to green.
                style = MaterialTheme.typography.bodyMedium, // Apply body style.
                modifier = Modifier.padding(bottom = 16.dp) // Add bottom padding.
            )
        }

        OutlinedTextField(
            value = email, // Bind email state to the text field.
            onValueChange = { email = it }, // Update email state on input change.
            label = { Text(text = stringResource(R.string.email)) }, // Set label text.
            modifier = Modifier.fillMaxWidth() // Make the text field fill the width.
        )

        Spacer(modifier = Modifier.height(8.dp)) // Add vertical spacing.

        OutlinedTextField(
            value = password, // Bind password state to the text field.
            onValueChange = { password = it }, // Update password state on input change.
            label = { Text(text = stringResource(R.string.password)) }, // Set label text.
            modifier = Modifier.fillMaxWidth(), // Make the text field fill the width.
            visualTransformation = PasswordVisualTransformation() // Mask the password input.
        )

        TextButton(
            onClick = {
                if (email.isNotBlank()) {
                    onResetPassword(email) // Trigger password reset if email is not blank.
                }
            },
            modifier = Modifier.align(Alignment.End) // Align the button to the end.
        ) {
            Text(text = stringResource(id = R.string.forgot_password)) // Display "Forgot Password" text.
        }

        Spacer(modifier = Modifier.height(16.dp)) // Add vertical spacing.

        Button(
            onClick = {
                if (isLogin) onLogin(email, password) // Trigger login if in login mode.
                else onRegister(email, password) // Trigger registration if in register mode.
            },
            modifier = Modifier.fillMaxWidth() // Make the button fill the width.
        ) {
            Text(if (isLogin) stringResource(R.string.login) else stringResource(R.string.register)) // Display login or register text.
        }

        Spacer(modifier = Modifier.height(12.dp)) // Add vertical spacing.

        TextButton(onClick = { isLogin = !isLogin }) { // Toggle between login and registration mode.
            Text(
                if (isLogin)
                    stringResource(id = R.string.no_account) // Display "No Account?" text.
                else
                    stringResource(id = R.string.already_have_account), // Display "Already Have Account?" text.
            )
        }
    }
}