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
@SuppressLint("NewApi")
@Composable
fun LocationTrackerScreen() {
    var location by remember { mutableStateOf("Waiting for location") }
    var longitude by remember { mutableStateOf(0.0) }
    var latitude by remember { mutableStateOf(0.0) }
    val context = LocalContext.current
    //check if location permission is granted
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    var showPermissionDeniedDialog by remember { mutableStateOf(false) }

    val locationRequest = remember {
        LocationRequest.create().apply {
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation.let { loc ->
                    location = "Location received"
                    latitude = loc.latitude
                    longitude = loc.longitude
                }
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } else {
            // Sprawdź, czy użytkownik trwale odrzucił uprawnienie
            if (!shouldShowRequestPermissionRationale(context as Activity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                showPermissionDeniedDialog = true // Pokaż dialog z prośbą o przejście do ustawień
            }
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } else {
            locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    // Dialog z prośbą o przejście do ustawień jesli użytkownik trwale odrzucił uprawnienie
    if (showPermissionDeniedDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDeniedDialog = false },
            title = { Text(stringResource(id = R.string.alert_localization_required)) },
            text = { Text(stringResource(id = R.string.alert_go_to_settings)) },
            confirmButton = {
                Button(
                    onClick = {
                        // Otwórz ustawienia aplikacji
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
                        NavigationEventHolder.navigateTo(Screens.SongScreen.route)
                    }
                ) {
                    Text(stringResource(id = R.string.alert_cancel))
                }
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    fun openGoogleMaps(lat: Double, lon: Double) {
        val gmmIntentUri = Uri.parse("geo:$lat,$lon?q=$lat,$lon(My+Location)")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps") // Ensure Google Maps is used
        context.startActivity(mapIntent)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if(location == "Waiting for location") {
            Text(text = stringResource(id = R.string.waiting_for_location))
        }else{
            Text(text = stringResource(id = R.string.location))
            Text(text = stringResource(id = R.string.latitude, latitude))
            Text(text = stringResource(id = R.string.longitude, longitude))
            Button(onClick = { openGoogleMaps(latitude, longitude) }) {
                Text(text = stringResource(id = R.string.open_google_maps))
            }
        }
    }
}