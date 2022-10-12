package com.ex.locationelevation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class reimaginedViewModel : ViewModel() {

    private val _theLatitude: MutableLiveData<Double> by lazy { MutableLiveData<Double>() }
    val theLatitude = _theLatitude

    private val _theLongitude: MutableLiveData<Double> by lazy { MutableLiveData<Double>() }
    val theLongitude = _theLongitude

    private val _theAltitude: MutableLiveData<Double> by lazy { MutableLiveData<Double>() }
    val theAltitude = _theAltitude

    private val _theAccuracy: MutableLiveData<Float> by lazy { MutableLiveData<Float>() }
    val theAccuracy = _theAltitude

    fun requestLocationTrackingData() = viewModelScope.launch(SupervisorJob() + Dispatchers.IO) {

//        val receivingLocation: LiveData<Location> = LocationService().shareLocationFlow().asLiveData()

//        val receivingLocation: LiveData<Location> = liveData {
//            LocationService().shareLocationFlow().collect{ location ->
//                emitSource(location)
//            }
//        }

        val receivingLocation = liveData {
            LocationService().shareLocationFlow().collect{ emit(it) }
        }

        val justLocation = receivingLocation.value

        withContext(Dispatchers.Default){
            _theLatitude.value = justLocation?.latitude ?: 0.00
            _theLongitude.value = justLocation?.longitude ?: 0.00
            _theAltitude.value = justLocation?.altitude ?: 0.00
//            _theAccuracy.value = justLocation?.accuracy ?:
        }

//        LocationService().shareLocationFlow().onEach { location ->
//            val lati = location.latitude
//            val long = location.longitude
//            val alti = location.altitude
//            val accu = location.accuracy
//
//            withContext(Dispatchers.Main){
//                _theLatitude.value = lati
//                _theLongitude.value = long
//                _theAltitude.value = alti
//                _theAccuracy.value = accu
//            }
//        }

    }

//    fun requestLocationTrackingData() {
//
//    }

    fun getAltitudeFlow(){
        viewModelScope.launch(Dispatchers.IO) {
           _theLatitude.value = LocationService.latestLatitude.asLiveData().value

        }
    }

    var rangeArray: LiveData<DoubleArray> = Transformations.map(_theAltitude) {
        when (it) {
            in 90.01..100.00 -> doubleArrayOf(90.01, 100.00)
            in 80.00..90.00 -> doubleArrayOf(80.0, 90.0)
            in 79.61..79.99 -> doubleArrayOf(79.61, 79.99)
            in 77.20..79.60 -> doubleArrayOf(77.2, 79.6)
            in 76.01..77.19 -> doubleArrayOf(76.01, 77.19)
            in 72.90..76.00 -> doubleArrayOf(72.9, 76.0)
            in 60.01..72.80 -> doubleArrayOf(60.01, 72.8)
            in 54.00..64.00 -> doubleArrayOf(54.00, 64.00)
            else -> doubleArrayOf(10.01, 40.00)
        }
    }

    var messageToDisplay: LiveData<String> = Transformations.map(_theAltitude) {
        when (it) {
            in 90.01..100.00 -> "c u in hevannaa"
            in 80.00..90.00 -> "7th floor"
            in 79.61..79.99 -> "6 - 7 floor"
            in 77.20..79.60 -> "6th floor"
            in 76.01..77.19 -> "5 - 6 floor"
            in 72.90..76.00 -> "5th floor"
            in 60.01..72.80 -> "2 - 4 floor"
            in 54.00..64.00 -> "1st floor"
            else -> "r y andegrawun? \nI hef gud inggres"
        }
    }

    fun checkActivityForLocationPermission(activity: Activity){
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED
            && ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){

            ActivityCompat.requestPermissions(activity, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), 0)

        }
    }


}