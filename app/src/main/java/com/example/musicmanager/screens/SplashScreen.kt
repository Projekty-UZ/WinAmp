package com.example.musicmanager.screens

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.example.musicmanager.R
import com.example.musicmanager.navigation.Screens
import kotlinx.coroutines.delay
/**
 * Composable function for rendering an animated splash screen.
 * Displays a fading animation for the foreground image and navigates to the authentication screen after the animation completes.
 *
 * @param navController The NavController used for handling navigation between screens.
 */
@Composable
fun AnimatedSplashScreen(navController: NavHostController) {
    // State variable to control the start of the animation.
    var startAnimation by remember { mutableStateOf(false) }

    // Animation state for controlling the alpha value of the foreground image.
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f, // Target alpha value based on animation state.
        animationSpec = tween(
            durationMillis = 3000, // Duration of the animation in milliseconds.
            easing = LinearOutSlowInEasing // Easing function for smooth animation.
        )
    )

    // Effect to start the animation and navigate to the authentication screen after a delay.
    LaunchedEffect(key1 = true) {
        startAnimation = true // Start the animation.
        delay(3000) // Wait for the animation to complete.
        navController.popBackStack() // Remove the splash screen from the back stack.
        navController.navigate(Screens.AuthScreen.route) // Navigate to the authentication screen.
    }

    // Render the splash screen with the animated alpha value.
    SplashScreen(alphaAnim.value)
}

/**
 * Composable function for rendering the splash screen layout.
 * Displays a background image and a foreground image with adjustable alpha for animation effects.
 *
 * @param alpha The alpha value for the foreground image, used for fade-in animation.
 */
@Composable
fun SplashScreen(alpha: Float) {
    Box(
        modifier = Modifier.fillMaxSize(), // Modifier to make the Box fill the entire screen.
        contentAlignment = Alignment.Center // Align the content to the center of the Box.
    ) {
        // Background image filling the entire screen.
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background), // Resource ID for the background image.
            contentDescription = null, // No content description for accessibility.
            modifier = Modifier.fillMaxSize(), // Modifier to make the image fill the entire screen.
            contentScale = ContentScale.FillBounds // Scale the image to fill the bounds.
        )
        // Foreground image with adjustable alpha for animation.
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Resource ID for the foreground image.
            contentDescription = null, // No content description for accessibility.
            alpha = alpha, // Apply the alpha value for fade-in effect.
            modifier = Modifier.fillMaxSize() // Modifier to make the image fill the entire screen.
        )
    }
}

/**
 * Preview function for the splash screen layout.
 * Allows developers to preview the SplashScreen composable in Android Studio's design editor.
 */
@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen(alpha = 1f) // Render the splash screen with full alpha for preview.
}