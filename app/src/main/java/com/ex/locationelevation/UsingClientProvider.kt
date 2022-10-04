package com.ex.locationelevation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.ex.locationelevation.databinding.ActivityUsingClientProviderBinding
import com.google.android.gms.location.*

class UsingClientProvider : AppCompatActivity() {

    private lateinit var bind:ActivityUsingClientProviderBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationBeingCalledBack: LocationCallback
    private var REQUESTING_LOCATION_UPDATES_KEY = "locationUpdateKey"
    private var requestingLocationUpdatesStatus = true
    private val PermissionID:Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityUsingClientProviderBinding.inflate(layoutInflater)
        setContentView(bind.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        bind.AtUCRadioButton.isChecked = true

        updateValuesFromBundle(savedInstanceState)

        listeners()

    }

    private fun listeners(){

        bind.backWithoutClientButton.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java)); finish()
        }

        bind.startCallbackFloatingActionButton.setOnClickListener{
            safetyCheck(); currentLocationPlease()
            Toast.makeText(this, "Started Location Tracking", Toast.LENGTH_SHORT).show()
        }

        bind.stopCallbackFloatingActionButton.setOnClickListener{
            safetyCheck(); stopCallingBackLocation()
            Toast.makeText(this, "Stopped Location Tracking", Toast.LENGTH_SHORT).show()
        }

        bind.getLocationWithClientButton.setOnClickListener{
            safetyCheck(); currentLocationPlease()
        }

        bind.experimentalButton.setOnClickListener{
            startActivity(Intent(this, reimaginedView::class.java))
            finish()
        }


    }

    private fun getLocationSelection():String{
        return findViewById<RadioButton>(bind.locationSelectionRadioGroup.checkedRadioButtonId).text.toString()
    }

    private fun startCallingBackLocation(){
        locationBeingCalledBack = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                for (location in p0.locations){
                    tellMeWhatFloorImOn(location.altitude, location.longitude, location.latitude)
                }
            }
        }

    }

    private fun stopCallingBackLocation(){
        fusedLocationClient.removeLocationUpdates(locationBeingCalledBack)
    }

//    @SuppressLint("MissingPermission")
//    private fun lastLocationPlease(){
//        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//            if(location!=null) {
//                altnow = location.altitude
//                lonnow = location.longitude
//                latnow = location.latitude
//            }
//        }
//    }

    @SuppressLint("MissingPermission")
    private fun currentLocationPlease(){
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        startCallingBackLocation()

        val locationResquesting = LocationRequest.create()
        locationResquesting.priority = Priority.PRIORITY_HIGH_ACCURACY
        locationResquesting.interval = 100
        locationResquesting.fastestInterval = 50
        locationResquesting.maxWaitTime = 100

        fusedLocationClient.requestLocationUpdates(
            locationResquesting,
            locationBeingCalledBack,
            Looper.getMainLooper() )

    }

    private fun tellMeWhatFloorImOn(alti:Double, long:Double, lati:Double){

        var safe = true
        var theAltitude = 0.00
        var theLat = 0.00
        var theLong = 0.00
        try{
            theAltitude = alti
            theLat = lati
            theLong = long
        } catch (e:Exception) {
//            Toast.makeText(this, "Invalid Altitude received", Toast.LENGTH_SHORT).show()
            safe = false
        }

        if(!safe){return}
        Toast.makeText(this, "$theAltitude", Toast.LENGTH_SHORT).show()

        val pieceMessage = when(getLocationSelection()) {
            "Home" -> atHome(theAltitude)
            "UCiputra" -> atUC(theLat, theLong, theAltitude)
            else -> {"Error in Acquiring Location"}
        }

        val finalMessage =
            "Current Latitude: $theLat \n" +
            "Current Longitude: $theLong \n" +
            "Current Altitude: $theAltitude \n" +
            "You're on the $pieceMessage"

        bind.locationDetailTextView.text = finalMessage
    }

    private fun atHome(theAltitude: Double):String{
        return if(theAltitude >= 33.0) {"2nd Floor"}
        else if(theAltitude in 32.0..33.0){ "stairs" }
        else if(theAltitude in 29.0..32.0) {"1st Floor"}
        else {"underground"}
    }

    private fun atUC(theLat:Double, theLong:Double, theAltitude:Double):String{

        return if(theAltitude > 90.0) {"Space Elevator, bye see you in Heaven"}
        else if(theAltitude in 80.0..90.0 && theLat in -7.286..-7.2855 && theLong in 112.6311..112.6322){ "Metrodata" }
        else if(theAltitude in 80.0..90.0){ "7th Floor" }
        else if (theAltitude in 79.61..79.99) { "6-7 Floor" }
        else if(theAltitude in 77.2..79.6) { "6th Floor" }
        else if (theAltitude in 76.01..77.19) { "5-6 Floor" }
        else if (theAltitude in 72.9..76.0) { "5th Floor" }
        else if (theAltitude in 60.01..72.8) { "2-4 Floor" }
        else if(theAltitude in 54.0..60.0 && theLat in -7.2862..-7.2857 && theLong in 112.6319700..112.63225) {"Corepreneur"}
        else if (theAltitude in 54.0..60.0) { "1st Floor" }
        else { "Underground" }



    }

    private fun safetyCheck():Boolean{
        var allclear = true
        if(!checkingLocationPermissions()) { requestingLocationPermissions(); allclear = false}
        if(!isTheLocationEnabled()) { requestLocationBeEnabled(); allclear = false}

        return allclear
    }

    private fun isTheLocationEnabled():Boolean{
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestLocationBeEnabled(){
        Toast.makeText(this, "Please turn on your location first", Toast.LENGTH_LONG).show()
        val intenting = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intenting)
    }

    private fun requestingLocationPermissions(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            PermissionID)
    }

    private fun checkingLocationPermissions():Boolean{

        // this could be turned into a for statement that takes in parameters-ish.
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ||
            ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PermissionID) {
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Permission(s) Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "Permission(s) Denied", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if(requestingLocationUpdatesStatus) startCallingBackLocation()
    }

    override fun onPause() {
        super.onPause()
        stopCallingBackLocation()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdatesStatus)
        super.onSaveInstanceState(outState)
    }

    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState ?: return

        // Update the value of requestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            requestingLocationUpdatesStatus = savedInstanceState.getBoolean(
                REQUESTING_LOCATION_UPDATES_KEY)
        }
    }

}