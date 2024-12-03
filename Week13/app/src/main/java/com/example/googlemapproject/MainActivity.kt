package com.example.googlemapproject

import android.os.Bundle
import android.widget.Toast
import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.googlemapproject.ui.theme.GoogleMapProjectTheme
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

class MainActivity : ComponentActivity() {

    val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
            permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION] == true &&
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
            // Permissions granted
        } else {
            Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        setContent {

            GoogleMapComposeApp()
            GeofencingExampleApp()
//            val Toronto = LatLng(4.60971, -74.08175 )
//            val cameraPositionState = rememberCameraPositionState {
//                position = CameraPosition.fromLatLngZoom(Toronto, 10f)
//            }
//
//            GoogleMapView(Toronto, cameraPositionState)

        }
    }



    @Composable
    fun GeofencingExampleApp() {
        val context = LocalContext.current
        val geofenceId = "ExampleGeofence"
        val geofenceCenter = LatLng(43.9019, -79.3910)
        val geofenceRadius = 200f // 200 meters

        // Setup Geofence when the app starts
        LaunchedEffect(Unit) {
            val geofence = createGeofence(geofenceId, geofenceCenter, geofenceRadius)
            addGeofence(context, geofence)
        }
    }



    @SuppressLint("MissingPermission")
    @Composable
    fun GoogleMapComposeApp() {

        val context = LocalContext.current
        var userLocation by remember { mutableStateOf<LatLng?>(null) }
        val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
        val locationRequest = remember {
            LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 10000 // Update every 10 seconds
                fastestInterval = 5000
            }
        }
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 15f)
        }
        // Request location updates
        LaunchedEffect(Unit) {
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let {
                        location ->
                        val newLocation = LatLng(location.latitude, location.longitude)
                        userLocation = newLocation
                        cameraPositionState.move(
                            CameraUpdateFactory.newLatLng(newLocation)
                        )
                    }
                }
            }

            if (userLocation == null) {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    context.mainLooper
                )
            }
        }

        GoogleMapView(userLocation,cameraPositionState)
    }



    @Composable
    fun GoogleMapView(userLocation: LatLng?, cameraPositionState: CameraPositionState) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            userLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "You are here"
                )
            }
        }
    }


}
