package com.ex.locationelevation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.ex.locationelevation.databinding.ActivityReimaginedBinding
import kotlinx.coroutines.flow.collectLatest
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

//        thisModel.theDummy.observe(this, Observer {
//            bind.dummyTextView.text = it.toString()
//        })

//        thisModel.theLatitude.observe(this) { bind.latitudeTextView.text = it.toString() }
        thisModel.theLatitude.observe(this, Observer {
            bind.latitudeTextView.text = it.toString()
        })
        thisModel.theLongitude.observe(this, Observer { bind.longitudeTextView.text = it.toString() })
        thisModel.theAccuracy.observe(this, Observer {bind.accuracyTextView.text = it.toString() })

//        thisModel.theAltitude.observe(this) { bind.altitudeTextView.text = it.toString() }
        thisModel.theAltitude.observe(this, Observer { bind.altitudeTextView.text = it.toString() })

        thisModel.theAltAcc.observe(this, Observer {bind.altAccuracyTextView.text = it.toString() })

        thisModel.messageToDisplay.observe(this, Observer { bind.DisplayFloorTextView.text = it })

        thisModel.rangeArray.observe(this, Observer{
            val topRange = it[1].toString()
            val lowRange = it[0].toString()
            bind.currentRangeTextView.text = "$lowRange - $topRange"
        })

    }

    private fun listeners(){
//        bind.customAltitudeButton.setOnClickListener{
//            val newCustomElevation = bind.CustomAltitudeEditTextNumberDecimal.text.toString()
//            if(newCustomElevation.isNotEmpty()){
//                thisModel.theAltitude.value = newCustomElevation.toDouble()
//            }
//        }

        bind.getLocationButton.setOnClickListener{
            Intent(applicationContext, LocationService::class.java).apply{
                action = LocationService.ACTION_START
                startService(this)
            }

            thisModel.startLatitudeFlow()
            thisModel.startLongitudeFlow()
            thisModel.startAccuracyFlow()
            thisModel.startAltitudeFlow()
            thisModel.startAltAccFlow()

        }

        bind.stopGettingLocationButton.setOnClickListener{
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                startService(this)
            }

//            if(thisModel.startLongitudeFlow().isActive){thisModel.cancelLatitudeFlow()}
//            if(thisModel.startLongitudeFlow().isActive){thisModel.cancelLongitudeFlow()}
//            if(thisModel.startAltitudeFlow().isActive){thisModel.cancelAltitudeFlow()}
            thisModel.cancelLatitudeFlow()
            thisModel.cancelLongitudeFlow()
            thisModel.cancelAccuracyFlow()
            thisModel.cancelAltitudeFlow()
            thisModel.cancelAltAccFlow()
//            FlowKey = false
//            Log.d("FLOW_KEY_FALSE", "Setting Flow key to false")

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