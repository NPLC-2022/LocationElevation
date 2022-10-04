package com.ex.locationelevation

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.ex.locationelevation.databinding.ActivityReimaginedBinding


class reimaginedView:AppCompatActivity() {

    private lateinit var bind:ActivityReimaginedBinding
    private lateinit var thisModel:reimaginedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityReimaginedBinding.inflate(layoutInflater)

        supportActionBar?.hide()

//        ActivityCompat.requestPermissions(this, arrayOf(
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ), 0)

        setContentView(bind.root)

        thisModel = ViewModelProvider(this)[reimaginedViewModel::class.java]

        observers()
        listeners()

    }

    private fun observers(){
        thisModel.theAltitude.observe(this) { bind.textView2.text = it.toString() }

        thisModel.messageToDisplay.observe(this){
            bind.DisplayFloorTextView.text = thisModel.messageToDisplay.value.toString()
        }

        thisModel.rangeArray.observe(this){
            val topRange = thisModel.rangeArray.value?.get(1).toString()
            val lowRange = thisModel.rangeArray.value?.get(0).toString()
            bind.currentRangeTextView.text = "$lowRange - $topRange"
        }
    }

    private fun listeners(){
        bind.customAltitudeButton.setOnClickListener{
            val newCustomElevation = bind.CustomAltitudeEditTextNumberDecimal.text.toString()
            if(newCustomElevation.isNotEmpty()){
                thisModel.theAltitude.value = newCustomElevation.toDouble()
            }
        }

        bind.getLocationButton.setOnClickListener{
            Intent(this, LocationService::class.java).apply{
                action = LocationService.ACTION_START
                startService(this)
            }
        }

        bind.stopGettingLocationButton.setOnClickListener{
            Intent(this, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                startService(this)
            }
        }

        bind.returnToClientButton.setOnClickListener{
            Intent(this, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                startService(this)
            }
            startActivity(Intent(this, UsingClientProvider::class.java))
            finish()
        }
    }



}