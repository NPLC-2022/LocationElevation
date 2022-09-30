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
//    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    private lateinit var locationBeingCalledBack: LocationCallback
    private var REQUESTING_LOCATION_UPDATES_KEY = "locationUpdateKey"
    private var requestingLocationUpdatesStatus = true
    private val PermissionID:Int = 100

    private var altnow: Double = 0.0
    private var latnow: Double = 0.0
    private var lonnow: Double = 0.0
    private val coarseLocationRef = android.Manifest.permission.ACCESS_COARSE_LOCATION
    private val fineLocationRef = android.Manifest.permission.ACCESS_FINE_LOCATION
    private val backgroundLocationRef = android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
    private val granted = PackageManager.PERMISSION_GRANTED

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

//        bind.backWithoutClientButton.setOnClickListener{ finish() }

        bind.backWithoutClientButton.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java)); finish();
        }

        bind.startCallbackFloatingActionButton.setOnClickListener{
            safetyCheck(); startCallingBackLocation() }

        bind.stopCallbackFloatingActionButton.setOnClickListener{
            safetyCheck(); stopCallingBackLocation() }

        bind.getLocationWithClientButton.setOnClickListener{
            safetyCheck()
            currentLocationPlease()
            tellMeWhatFloorImOn(alti = altnow, long = lonnow, lati = latnow)
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

    @SuppressLint("MissingPermission")
    private fun lastLocationPlease(){

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if(location!=null) {
                altnow = location.altitude
                lonnow = location.longitude
                latnow = location.latitude
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun currentLocationPlease(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
//        bind.locationDetailTextView.text = "current Altitude: $altitudes"

        var safe = true
        var theAltitude = 9.99
        try{
            theAltitude = alti
        } catch (e:Exception) {
//            Toast.makeText(this, "Invalid Altitude received", Toast.LENGTH_SHORT).show()
            safe = false
        }

        if(!safe){return}
        Toast.makeText(this, "$theAltitude", Toast.LENGTH_SHORT).show()

        val pieceMessage = when(getLocationSelection()) {
            "Home" -> atHome(theAltitude)
            "UCiputra" -> atUC(theAltitude)
            else -> {"Error in Acquiring Location"}
        }

        val finalMessage =
            "Current Latitude: $lati \n" +
            "Current Longitude: $long \n" +
            "Current Altitude $alti \n" + "You're on the $pieceMessage"

        bind.locationDetailTextView.text = finalMessage
    }

    private fun atHome(theAltitude: Double):String{
        return if(theAltitude >= 33.0) {"2nd Floor"}
        else if(theAltitude in 32.0..33.0){ "stairs" }
        else if(theAltitude in 29.0..32.0) {"1st Floor"}
        else {"underground"}
    }

    private fun atUC(theAltitude:Double):String{
        return if(theAltitude >= 74){ "7th Floor" }
        else if(theAltitude >= 73) { "6th Floor" }
        else if (theAltitude >= 72) { "5th Floor" }
        else if (theAltitude >= 70) { "4th Floor" }
        else if (theAltitude >= 68) { "3rd Floor" }
        else if (theAltitude >= 65) { "2nd Floor" }
        else if (theAltitude >= 55.5) { "1st Floor" }
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
            arrayOf(coarseLocationRef, fineLocationRef, backgroundLocationRef),
            PermissionID)
    }

    private fun checkingLocationPermissions():Boolean{

        // this could be turned into a for statement that takes in parameters-ish.
        if(ActivityCompat.checkSelfPermission(this, coarseLocationRef) == granted
            ||
            ActivityCompat.checkSelfPermission(this, fineLocationRef) == granted
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
        if(requestingLocationUpdatesStatus) startCallingBackLocation();
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