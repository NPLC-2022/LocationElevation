package com.ex.locationelevation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.ex.locationelevation.databinding.ActivityMainBinding
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient:FusedLocationProviderClient
    private lateinit var locationRequest:com.google.android.gms.location.LocationRequest

    private lateinit var bind:ActivityMainBinding
    private val PERMISSIONID = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

//        bind.getLocationButton.setOnClickListener{
//            getLastLocation()
//            getNewLocation()
//        }

        bind.getPreviousLocationButton.setOnClickListener{
            getNewLocation()
            getLastLocation()
        }

        bind.goToClientProviderButton.setOnClickListener{
            startActivity(Intent(this, UsingClientProvider::class.java))
        }

    }
//
    private fun CheckPermission():Boolean{
        if(ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun getLastLocation(){
        if(!CheckPermission()){ RequestPermission() } else
            if(CheckPermission() && isLocationEnabled()){

                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    RequestPermission()
                }

                fusedLocationProviderClient.lastLocation.addOnCompleteListener{ task ->
                    val Location:Location? = task.result
                    var message = ""
                    if(Location != null){

                        // first, 5th, 6th and 7th floor
                        if(Location.altitude >= 72) {message = "5th floor"}
                        else if(Location.altitude >= 68) {message = "3rd floor"}
                        else if(Location.altitude >= 54) {message = "1st floor"}
                        else {message = "underground"}

                        bind.locationTextView.setText(
                            "Your Current Coordinates are:\n " +
                            "Lat: " + Location.latitude + ";\n " +
                            "Long: " + Location.longitude + "; Alt: " + Location.altitude + "\n " +
                            "You're at the " + message
                        )
                    }
                }

//            }

        } else {
            Toast.makeText(this, "Please enable your location service", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    @SuppressLint("MissingPermission")
    private fun getNewLocation(){
        if (!CheckPermission()){ return }
        if (!isLocationEnabled()){ return }

        locationRequest = LocationRequest()
        locationRequest.priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 1
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )

    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location? = locationResult.lastLocation
            if (lastLocation != null) {
//                Log.d("Debug:","your last last location: "+ lastLocation.longitude.toString())
                bind.locationTextView.text = "You Last Location is : \n " +
                        "Long: "+ lastLocation.longitude + " \n " +
                        "Lat: " + lastLocation.latitude + " \n " +
                        "Alt: " + lastLocation.altitude
            } else {
                Toast.makeText(this@MainActivity, "Error in getting your location", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun RequestPermission(){

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION),
            PERMISSIONID
        )

    }

    private fun isLocationEnabled():Boolean{
        var locationManager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSIONID) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug", "You have the permission")
            }
        }

    }
}