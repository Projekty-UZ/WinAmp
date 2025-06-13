package com.example.musicmanager.screens

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.Composable
import com.google.android.gms.location.LocationServices
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Looper
import android.provider.Settings
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.example.musicmanager.navigation.Navigation
import com.example.musicmanager.navigation.NavigationEventHolder
import com.example.musicmanager.navigation.Screens
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.example.musicmanager.R
/**
 * Composable function for rendering the Location Tracker screen.
 * Tracks the user's location and displays latitude and longitude.
 * Handles location permissions and provides functionality to open Google Maps with the current location.
 */
@SuppressLint("NewApi")
@Composable
fun LocationTrackerScreen() {
    // State variables for location data and permission dialog visibility.
    var location by remember { mutableStateOf("Waiting for location") }
    var longitude by remember { mutableStateOf(0.0) }
    var latitude by remember { mutableStateOf(0.0) }
    val context = LocalContext.current

    // FusedLocationProviderClient for accessing location services.
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // State variable to control the visibility of the permission denied dialog.
    var showPermissionDeniedDialog by remember { mutableStateOf(false) }

    // Configuration for location request with high accuracy and intervals.
    val locationRequest = remember {
        LocationRequest.create().apply {
            interval = 10000 // Request location updates every 10 seconds.
            fastestInterval = 5000 // Allow updates as fast as every 5 seconds.
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Use high accuracy for location updates.
        }
    }

    // Callback to handle location updates.
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation.let { loc ->
                    location = "Location received" // Update location status.
                    latitude = loc.latitude // Update latitude.
                    longitude = loc.longitude // Update longitude.
                }
            }
        }
    }

    // Launcher for requesting location permission.
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Start location updates if permission is granted.
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } else {
            // Check if the user permanently denied the permission.
            if (!shouldShowRequestPermissionRationale(context as Activity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                showPermissionDeniedDialog = true // Show dialog to guide the user to app settings.
            }
        }
    }

    // Effect to check and request location permission on screen launch.
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } else {
            locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Dialog to guide the user to app settings if permission is permanently denied.
    if (showPermissionDeniedDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDeniedDialog = false },
            title = { Text(stringResource(id = R.string.alert_localization_required)) },
            text = { Text(stringResource(id = R.string.alert_go_to_settings)) },
            confirmButton = {
                Button(
                    onClick = {
                        // Open app settings.
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.fromParts("package", context.packageName, null)
                        context.startActivity(intent)
                        showPermissionDeniedDialog = false
                    }
                ) {
                    Text(stringResource(id = R.string.alert_settings))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showPermissionDeniedDialog = false
                        NavigationEventHolder.navigateTo(Screens.SongScreen.route) // Navigate to the Song screen.
                    }
                ) {
                    Text(stringResource(id = R.string.alert_cancel))
                }
            }
        )
    }

    // Effect to clean up location updates when the composable is disposed.
    DisposableEffect(Unit) {
        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    /**
     * Function to open Google Maps with the given latitude and longitude.
     *
     * @param lat Latitude of the location.
     * @param lon Longitude of the location.
     */
    fun openGoogleMaps(lat: Double, lon: Double) {
        val gmmIntentUri = Uri.parse("geo:$lat,$lon?q=$lat,$lon(My+Location)")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps") // Ensure Google Maps is used.
        context.startActivity(mapIntent)
    }

    // UI layout for displaying location data and actions.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (location == "Waiting for location") {
            Text(text = stringResource(id = R.string.waiting_for_location)) // Display waiting message.
        } else {
            Text(text = stringResource(id = R.string.location)) // Display location label.
            Text(text = stringResource(id = R.string.latitude, latitude)) // Display latitude.
            Text(text = stringResource(id = R.string.longitude, longitude)) // Display longitude.
            Button(onClick = { openGoogleMaps(latitude, longitude) }) {
                Text(text = stringResource(id = R.string.open_google_maps)) // Button to open Google Maps.
            }
        }
    }
}