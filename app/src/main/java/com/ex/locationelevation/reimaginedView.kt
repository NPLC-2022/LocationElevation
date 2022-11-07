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

//        thisModel.theAltitude.observe(this) { bind.altitudeTextView.text = it.toString() }
        thisModel.theAltitude.observe(this, Observer { bind.altitudeTextView.text = it.toString() })

        thisModel.messageToDisplay.observe(this, Observer { bind.DisplayFloorTextView.text = it })

        thisModel.rangeArray.observe(this){
            val topRange = it[1].toString()
            val lowRange = it[0].toString()
            bind.currentRangeTextView.text = "$lowRange - $topRange"
        }
    }

    private fun listeners(){
//        bind.customAltitudeButton.setOnClickListener{
//            val newCustomElevation = bind.CustomAltitudeEditTextNumberDecimal.text.toString()
//            if(newCustomElevation.isNotEmpty()){
//                thisModel.theAltitude.value = newCustomElevation.toDouble()
//            }
//        }

        bind.StartDummyFlowButton.setOnClickListener{
            thisModel.dummyDataStateFlow()

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    thisModel.theDummyState.collectLatest { number ->
                        bind.dummyTextView.text = number.toString()
                    }
                }
            }

        }

        bind.getLocationButton.setOnClickListener{
            Intent(applicationContext, LocationService::class.java).apply{
                action = LocationService.ACTION_START
                startService(this)
            }

//            if(!thisModel.startLatitudeFlow().isActive){thisModel.startLatitudeFlow()}
//            if(!thisModel.startLongitudeFlow().isActive){thisModel.startLongitudeFlow()}
//            if(!thisModel.startAltitudeFlow().isActive){thisModel.startAltitudeFlow()}

            thisModel.startLatitudeFlow()
            thisModel.startLongitudeFlow()
            thisModel.startAltitudeFlow()
//            FlowKey = true
//            Log.d("FLOW_KEY_TRUE", "Setting Flow key to True")


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
            thisModel.cancelAltitudeFlow()
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