package com.ex.locationelevation

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ex.locationelevation.databinding.ActivityReimaginedBinding


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
        thisModel.theAltitude.observe(this) { bind.textView2.text = it.toString() }

        thisModel.messageToDisplay.observe(this){ bind.DisplayFloorTextView.text = it }

        thisModel.rangeArray.observe(this){
            val topRange = it[1].toString()
            val lowRange = it[0].toString()
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
            Intent(applicationContext, LocationService::class.java).apply{
                action = LocationService.ACTION_START
                startService(this)
            }
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