package com.example.timekeeping_beta.Fragments.MobileTimeEntry

import android.Manifest
import android.app.AlertDialog
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.location.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    var currentLocation = MutableLiveData<JSONArray>()

    private var fusedLocationClient: FusedLocationProviderClient
    private var mLocationRequest: LocationRequest
    private var geocoder: Geocoder

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(application.baseContext)
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 60 * 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(application.baseContext)

        //Geocoder converts g
        geocoder = Geocoder(application.baseContext, Locale.getDefault())
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {

        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)

            if (p0 is LocationResult) {
                val location_list = p0.getLocations()

                if (location_list.size > 0) {

                    val location = location_list.get(location_list.size - 1)

                    val lat = location.latitude
                    val long = location.longitude

                    var location_array = JSONArray()
                    var location_object = JSONObject()
                    location_object.put("latitude", lat)
                    location_object.put("longitude", long)
                    location_array.put(location_object)

                    currentLocation.value = location_array
                }
            }
        }

    }

    fun startLocationListener() {

        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }


    fun stopUpdates() {
        fusedLocationClient.flushLocations()
        fusedLocationClient.removeLocationUpdates(mLocationCallback)
    }
}