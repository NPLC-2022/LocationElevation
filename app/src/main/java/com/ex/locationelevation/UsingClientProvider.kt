package com.ex.locationelevation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.lifecycleScope
import com.ex.locationelevation.databinding.ActivityUsingClientProviderBinding
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UsingClientProvider : AppCompatActivity() {

    private lateinit var bind:ActivityUsingClientProviderBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
//    private lateinit var locationBeingCalledBack: LocationCallback
    private lateinit var altToast: Toast
    private var locationBeingCalledBack = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            for (location in p0.locations){
                tellMeWhatFloorImOn(
                    location.altitude,
                    location.verticalAccuracyMeters.toDouble(),
                    location.longitude,
                    location.latitude,
                    location.accuracy.toDouble())
            }
        }

    }

    private var REQUESTING_LOCATION_UPDATES_KEY = "locationUpdateKey"
    // False means I'm not asking atm, True means I am asking for it.
    private var requestingLocationUpdatesStatus = false
    private val PermissionID:Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityUsingClientProviderBinding.inflate(layoutInflater)
        setContentView(bind.root)
        supportActionBar?.hide()

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
            safetyCheck(); currentLocationPlease(); cancelOutAllToasts()
            Toast.makeText(this, "Started Location Tracking", Toast.LENGTH_SHORT).show()
        }

        bind.stopCallbackFloatingActionButton.setOnClickListener{
            safetyCheck(); removeLocationCallback(); cancelOutAllToasts()
            Toast.makeText(this, "Stopped Location Tracking", Toast.LENGTH_SHORT).show()

            if(bind.locationTrackingStatusTextView.text.toString()=="Active") { //Green
                bind.locationTrackingStatusTextView.text = "Disabled"
                bind.locationTrackingStatusTextView.setTextColor(Color.RED)
            }

        }

        bind.getLocationWithClientButton.setOnClickListener{
            safetyCheck(); currentLocationPlease(); cancelOutAllToasts()
        }

        bind.experimentalButton.setOnClickListener{
            startActivity(Intent(this, reimaginedView::class.java))
            finish()
        }

        bind.toQRPageActivity.setOnClickListener{
            startActivity(Intent(this, QRGeneratorActivity::class.java))
//            finish()
        }


    }

    private fun createShortToastMessage(string:String){
        altToast = Toast.makeText(this, string, Toast.LENGTH_SHORT)
    }

    private fun displayToastMessage() = altToast.show()

    private fun cancelOutAllToasts() {
        if (!::altToast.isInitialized) { return }

        altToast.cancel()
    }

    private fun getLocationSelection():String{
        return findViewById<RadioButton>(bind.locationSelectionRadioGroup.checkedRadioButtonId).text.toString()
    }

    private fun removeLocationCallback(){
        if(!requestingLocationUpdatesStatus) {return }
        fusedLocationClient.removeLocationUpdates(locationBeingCalledBack)
        requestingLocationUpdatesStatus = false
    }

    @SuppressLint("MissingPermission")
    private fun currentLocationPlease(){

        if(requestingLocationUpdatesStatus){return }

//        locationBeingCalledBack = object : LocationCallback() {
//            override fun onLocationResult(p0: LocationResult) {
//                for (location in p0.locations){
//                    tellMeWhatFloorImOn(location.altitude, location.longitude, location.latitude)
//                }
//            }
//        }

        val locationResquesting = LocationRequest.create()
        locationResquesting.priority = Priority.PRIORITY_HIGH_ACCURACY
        locationResquesting.interval = 100
        locationResquesting.fastestInterval = 50
        locationResquesting.maxWaitTime = 100
        locationResquesting.isWaitForAccurateLocation = true

        fusedLocationClient.requestLocationUpdates(
            locationResquesting,
            locationBeingCalledBack,
            Looper.getMainLooper() )

        requestingLocationUpdatesStatus = true
    }

    private fun tellMeWhatFloorImOn(alti:Double, altcc:Double, long:Double, lati:Double, acc:Double){

//        if(!safe){return}
        createShortToastMessage("$alti"); displayToastMessage()

        val pieceMessage = when(getLocationSelection()) {
            "Home" -> atHome(alti)
            "UCiputra" -> atUC(lati, long, alti)
            else -> {"Error in Acquiring Location"}
        }

        val activeStatusMessage = when(requestingLocationUpdatesStatus){
            true -> "Active"; false -> "Disabled"
        }

        val finalMessage =
                "Latitude: $lati \n" +
                "Longitude: $long \n" +
                "Accuracy: $acc \n" +
                "Altitude: $alti \n" +
                "Alt-Acc: $altcc \n" +
                "You're on the $pieceMessage"

        bind.locationDetailTextView.text = finalMessage
        bind.locationTrackingStatusTextView.text = activeStatusMessage

        // this might kill your app cause of bad parsing
        if(activeStatusMessage=="Disabled"){ //Red
            bind.locationTrackingStatusTextView.setTextColor(Color.RED)
        } else { //Green
            bind.locationTrackingStatusTextView.setTextColor(Color.GREEN)
        }

    }

    private fun atHome(theAltitude: Double):String{
        return if(theAltitude >= 33.0) {"2nd Floor"}
        else if(theAltitude in 32.0..33.0){ "stairs" }
        else if(theAltitude in 29.0..32.0) {"1st Floor"}
        else {"underground"}
    }

    private fun atUC(theLat:Double, theLong:Double, theAltitude:Double):String{

        return if(theAltitude > 90.00) {"Space Elevator, bye see you in Heaven"}
        else if(theAltitude in 80.00..90.00 && theLat in -7.2859..-7.2853 && theLong in 112.6315..112.63185){ "Dian Auditorium" }
        else if(theAltitude in 80.00..90.00 && theLat in -7.286..-7.2857 && theLong in 112.6314..112.6322){ "Metrodata" }
//        else if(theAltitude in 80.00..90.00 && theLat in -7.2857..-7.2856 && theLong in 112.6316..112.6318){ "Dian Auditorium" }
        else if(theAltitude in 80.00..90.00){ "7th Floor" }
        else if (theAltitude in 79.61..79.99) { "6-7 Floor" }
        else if(theAltitude in 77.20..79.60) { "6th Floor" }
        else if (theAltitude in 76.01..77.19) { "5-6 Floor" }
        else if (theAltitude in 72.90..76.00) { "5th Floor" }
        else if (theAltitude in 60.01..72.80) { "2-4 Floor" }
//        else if(theAltitude in 54.0..60.0 && theLat in -7.2862..-7.2857 && theLong in 112.6319700..112.63225) {"Corepreneur"}
        else if(theAltitude in 54.00..60.00 && theLat in -7.2862..-7.2857 && theLong in 112.6319..112.6322) {"Corepreneur"}
        else if (theAltitude in 54.00..60.0) { "1st Floor" }
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
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
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
        if(requestingLocationUpdatesStatus) currentLocationPlease()
    }

    override fun onPause() {
        super.onPause()
        removeLocationCallback()
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