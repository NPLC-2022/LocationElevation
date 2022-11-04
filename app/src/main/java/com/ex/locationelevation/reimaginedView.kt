package com.ex.locationelevation

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.ex.locationelevation.LocationService.Companion.ACTION_START
import com.ex.locationelevation.databinding.ActivityReimaginedBinding
import kotlinx.coroutines.flow.SharingCommand
import kotlinx.coroutines.launch


class reimaginedView:AppCompatActivity() {

    private lateinit var bind:ActivityReimaginedBinding
    lateinit var thisModel:reimaginedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityReimaginedBinding.inflate(layoutInflater)

        supportActionBar?.hide()
        setContentView(bind.root)

        thisModel = ViewModelProvider(this)[reimaginedViewModel::class.java]
        thisModel.checkActivityForLocationPermission(this)

        observers()
        listeners()

    }

    private fun observers(){
        // When changes are made in the ViewModel, it will automatically show here

        thisModel.theLatitude.observe(this) { bind.latitudeTextView.text = it.toString() }

        thisModel.theLongitude.observe(this) { bind.longitudeTextView.text = it.toString() }

        thisModel.theAltitude.observe(this) { bind.altitudeTextView.text = it.toString() }

        thisModel.messageToDisplay.observe(this){ bind.DisplayFloorTextView.text = it }

        thisModel.rangeArray.observe(this){
            val topRange = it[1].toString()
            val lowRange = it[0].toString()
            bind.currentRangeTextView.text = "$lowRange - $topRange"
        }
    }

    fun subscribeToLocationServer(){
        lifecycleScope.launch {
            LocationService.freshLatitude.observe(this@reimaginedView){
                bind.latitudeTextView.text = it.toString()
            }
            LocationService.freshLongitude.observe(this@reimaginedView){
                bind.longitudeTextView.text = it.toString()
            }
            LocationService.freshAltitude.observe(this@reimaginedView){
                bind.altitudeTextView.text = it.toString()
            }
            LocationService.freshAccuracy.observe(this@reimaginedView){
                bind.accuracyTextView.text = it.toString()
            }
            thisModel.theDummy.observe(this@reimaginedView){
                bind.dummyTextView.text = it.toString()
            }
        }
    }

    private fun listeners(){
        bind.customAltitudeButton.setOnClickListener{
            val newCustomElevation = bind.CustomAltitudeEditTextNumberDecimal.text.toString()
            if(newCustomElevation.isNotEmpty()){
                thisModel.theAltitude.value = newCustomElevation.toDouble()
            }
        }

        bind.StartDummyFlowButton.setOnClickListener{
            thisModel.dummyDataFlow()
        }

        bind.getLocationButton.setOnClickListener{
//            thisModel.startLocationTracking(this)
            Intent(applicationContext, LocationService::class.java).apply{
                action = LocationService.ACTION_START
                startService(this)
            }
            subscribeToLocationServer()
        }

        bind.anotherGetLocationButton.setOnClickListener{
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                startService(this)
            }
            thisModel.generateLocations(applicationContext)
//            thisModel.requestLocationTrackingData()
        }

        bind.stopGettingLocationButton.setOnClickListener{
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                startService(this)
            }

        }

        bind.returnToClientButton.setOnClickListener{
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                startService(this)
            }

            startActivity(Intent(this, UsingClientProvider::class.java))
            finish()
        }
    }





}