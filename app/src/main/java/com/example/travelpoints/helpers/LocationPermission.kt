package com.example.travelpoints.helpers

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.travelpoints.models.fromStringToCategory
import com.example.travelpoints.models.getMarkerIcon
import com.example.travelpoints.util.drawableToBitmap
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LocationPermission(
    private val moveToUserLocation: (LatLng) -> Unit = {}
) {
    fun instanceLocationRequest(locationRequest: LocationRequest) {
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000L
        locationRequest.fastestInterval = 2000L
    }

    fun getCurrentLocation(activity: Activity, locationRequest: LocationRequest, map: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            if (isGPSEnabled(activity)) {
                LocationServices.getFusedLocationProviderClient(activity)
                    .requestLocationUpdates(locationRequest, object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            super.onLocationResult(locationResult)

                            LocationServices.getFusedLocationProviderClient(activity).removeLocationUpdates(this)
                            if (locationResult.locations.size > 0) {
                                val index = locationResult.locations.size - 1
                                val latitude = locationResult.locations[index].latitude
                                val longitude = locationResult.locations[index].longitude

                                setMarkerOnCurrentPosition(map, latitude, longitude, activity)
                            }
                        }

                    }, Looper.getMainLooper())
            } else {
                turnOnGPS(activity, locationRequest)
            }
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1);
        }
    }

    private fun isGPSEnabled(activity: Activity): Boolean {
        val locationManager: LocationManager? = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        var isEnabled = false

        if (locationManager != null) {
            isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }
        return isEnabled
    }

    private fun turnOnGPS(activity: Activity, locationRequest: LocationRequest) {
        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(activity).checkLocationSettings(builder.build())

        result.addOnCompleteListener(object : OnCompleteListener<LocationSettingsResponse> {
            override fun onComplete(task: Task<LocationSettingsResponse>) {

                try {
                    val response: LocationSettingsResponse = task.getResult(ApiException::class.java)
                    Toast.makeText(activity, "GPS is already turned on", Toast.LENGTH_SHORT).show()
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            try {
                                val resolvableApiException: ResolvableApiException = e as ResolvableApiException
                                resolvableApiException.startResolutionForResult(activity, 2)
                            } catch (ex: IntentSender.SendIntentException) {
                                ex.printStackTrace()
                            }
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            return
                        }
                    }
                }
            }
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setMarkerOnCurrentPosition(map: GoogleMap, a: Double, b: Double, context: Context) {
        val drawable: Drawable = context.resources.getDrawable(context.resources.getIdentifier("current_location_drawable", "drawable", context.packageName))
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(drawableToBitmap(drawable)!!)

        val marker = MarkerOptions().position(LatLng(a, b)).title("You are here").icon(bitmapDescriptor)
        map.addMarker(marker)

        moveToUserLocation(LatLng(a, b))

        getAllSitesAndSetMarkers(map, context)
    }

    private fun getAllSitesAndSetMarkers(map: GoogleMap, context: Context) {
        val siteNumber = FirebaseDatabase.getInstance().getReference("Sites")
        siteNumber.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        val latitude = it.child("Location").child("Latitude").getValue(Double::class.java)
                        val longitude = it.child("Location").child("Longitude").getValue(Double::class.java)
                        val name = it.child("Name").value.toString()
                        val category = fromStringToCategory(it.child("Category").value.toString())

                        if (latitude != null && longitude != null) {
                            map.addMarker(
                                MarkerOptions().position(LatLng(latitude, longitude)).title(name)
                                    .icon(getMarkerIcon(category, context))
                            )
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}