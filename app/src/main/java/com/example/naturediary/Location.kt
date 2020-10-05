package com.example.naturediary

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.location.*
import java.util.*



class Location {


    //initializing variables to be used in other classes
    companion object {
        lateinit var mainContext: Context

        private lateinit var fusedLocationClient: FusedLocationProviderClient
        private lateinit var locationRequest: LocationRequest
        private lateinit var locationCallback: LocationCallback

        var locationString = ""

        private lateinit var mainLocationManager: LocationManager
        lateinit var mainActivity: Activity

        //init fun to main
        fun init (context: Context, locationManager: LocationManager, activity: Activity) {
            mainContext = context
            mainLocationManager = locationManager
            mainActivity = activity

        }

    }
    //function to be used in main to get location data
     fun checkLocation() {
        val manager = mainLocationManager
        /*if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertLocation()
        }*/
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
        getLocationUpdates()
    }

    /*private fun showAlertLocation() {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage("Your location settings is set to Off, Please enable location to use this application")
        dialog.setPositiveButton("Settings") { _, _ ->
            val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(myIntent)
        }
        dialog.setNegativeButton("Cancel") { _, _ ->
            finish()
        }
        dialog.setCancelable(false)
        dialog.show()
    }*/

    private fun getLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
        locationRequest = LocationRequest()
        locationRequest.interval = 50000
        locationRequest.fastestInterval = 50000
        locationRequest.smallestDisplacement = 170f
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                if (locationResult.locations.isNotEmpty()) {
                    val location = locationResult.lastLocation
                    Log.e(TAG, location.toString())
                    val addresses: List<Address>?
                    val geoCoder = Geocoder(mainContext, Locale.getDefault())
                    addresses = geoCoder.getFromLocation(
                        locationResult.lastLocation.latitude,
                        locationResult.lastLocation.longitude,
                        1
                    )
                    //return address to the textfield instead of coordinates
                    if (addresses != null && addresses.isNotEmpty()) {
                        val address: String = addresses[0].getAddressLine(0)
                        val city: String = addresses[0].locality
                        val country: String = addresses[0].countryName
                        Log.e(TAG, "$address $city$country")
                        locationString = "$address"
                        Log.d(TAG, "loc upattu")
                        Firebase().upload(MainActivity.deviceId, "Dani", locationString)
                    }
                }
            }
        }
    }

    //check permissions for location
     fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                mainContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mainContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null /* Looper */
        )
    }

    // updates stopped onPause in MainActivity
     fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }



}