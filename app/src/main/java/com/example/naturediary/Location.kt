package com.example.naturediary

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import java.util.*

//Location
class Location {

    companion object {
        private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

        lateinit var mainContext: Context
        lateinit var mainActivity: Activity
        lateinit var mainLocationManager: LocationManager

        var lastLat: MutableLiveData<Double> = MutableLiveData()
        var lastLon: MutableLiveData<Double> = MutableLiveData()
        var locationString: MutableLiveData<String> = MutableLiveData()

        //Init gets context and other properties from MainActivity
        fun init(context: Context, activity: Activity, locationManager: LocationManager) {
            mainContext = context
            mainActivity = activity
            mainLocationManager = locationManager
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
                mainActivity
            )
        }
    }

    //Get last location
    fun getLastLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        newLocationData()
                    } else {
                        lastLat.value = location.latitude
                        lastLon.value = location.longitude
                        locationString.value = getAddress(location.latitude, location.longitude)
                    }
                }
            } else {
                Toast.makeText(mainContext, "Please Turn on GPS", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            requestPermission()
        }
    }

    //On new location data, init locationRequest
    private fun newLocationData() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mainActivity)
        if (checkPermission()) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, null
            )
        }
    }

    //Location call back
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location = locationResult.lastLocation
            Log.d(TAG, "$lastLocation")
        }
    }

    //Get permissions if needed
    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                mainContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                mainContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) return true
        return false
    }

    //Request permissions
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            mainActivity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), 1010
        )
    }

    //Check if location is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager = mainLocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    //Get nice string of current address
    private fun getAddress(lat: Double, long: Double): String {
        val geoResult = Geocoder(mainContext, Locale.getDefault()).getFromLocation(lat, long, 3)[0]

        return "${geoResult.thoroughfare} ${geoResult.featureName}, ${geoResult.postalCode}, ${geoResult.locality}"
    }

}