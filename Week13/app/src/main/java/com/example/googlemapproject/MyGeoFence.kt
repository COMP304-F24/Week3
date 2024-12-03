package com.example.googlemapproject
import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng


fun createGeofence(
    geofenceId: String,
    latLng: LatLng,
    radius: Float
): Geofence {
    return Geofence.Builder()
        .setRequestId(geofenceId)
        .setCircularRegion(latLng.latitude, latLng.longitude, radius)
        .setExpirationDuration(Geofence.NEVER_EXPIRE)
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
        .build()
}

fun buildGeofencingRequest(geofence: Geofence): GeofencingRequest {
    return GeofencingRequest.Builder()
        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
        .addGeofence(geofence)
        .build()
}

fun addGeofence(context: Context, geofence: Geofence) {
    val geofencingClient = LocationServices.getGeofencingClient(context)
    val geofencingRequest = buildGeofencingRequest(geofence)
    val geofencePendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        Intent(context, GeofenceBroadcastReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    )

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }
    geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)
        .addOnSuccessListener {
            Toast.makeText(context, "Geofence added", Toast.LENGTH_LONG).show()
            Log.d("app","Geofence added" )
        }
        .addOnFailureListener {

            Toast.makeText(context, "Failed to add geofence", Toast.LENGTH_LONG).show()
        }
}
