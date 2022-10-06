package com.ex.locationelevation

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class reimaginedViewModel : ViewModel() {

    private val _theLatitude: MutableLiveData<Double> by lazy { MutableLiveData<Double>() }
    val theLatitude = _theLatitude

    private val _theLongitude: MutableLiveData<Double> by lazy { MutableLiveData<Double>() }
    val theLongitude = _theLongitude

    private val _theAltitude: MutableLiveData<Double> by lazy { MutableLiveData<Double>() }
    val theAltitude = _theAltitude

    private val ourLocationClient = LocationService


    init {
        viewModelScope.launch{
            ourLocationClient.latestLatitude.onEach { theLatitude.value = it }
            ourLocationClient.latestLongitude.onEach { theLongitude.value = it }
            ourLocationClient.latestAltitude.onEach { theAltitude.value = it }
        }

    }

    var rangeArray: LiveData<DoubleArray> = Transformations.map(theAltitude) {
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

    var messageToDisplay: LiveData<String> = Transformations.map(theAltitude) {
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


//    val UC_MAP: HashMap<ClosedFloatingPointRange<Double>, DoubleArray> = hashMapOf(
//        90.01..100.00 to doubleArrayOf(90.01, 200.00),
//        80.00..90.00 to doubleArrayOf(80.0, 90.0),
//        79.61..79.99 to doubleArrayOf(79.61, 79.99),
//        77.20..79.60 to doubleArrayOf(77.2, 79.6),
//        76.01..77.19 to doubleArrayOf(76.01, 77.19),
//        72.90..76.00 to doubleArrayOf(72.9, 76.0),
//        60.01..72.80 to doubleArrayOf(60.01, 72.8),
//        54.00..64.00 to doubleArrayOf(54.00, 64.00)
//    )

//    val UC_MAP: HashMap<String, DoubleArray> = hashMapOf(
//        "space" to doubleArrayOf(90.01, 200.00),
//        "7" to doubleArrayOf(80.0, 90.0),
//        "67" to doubleArrayOf(79.61, 79.99),
//        "6" to doubleArrayOf(77.2, 79.6),
//        "56" to doubleArrayOf(76.01, 77.19),
//        "5" to doubleArrayOf(72.9, 76.0),
//        "24" to doubleArrayOf(60.01, 72.8),
//        "1" to doubleArrayOf(54.00, 64.00)
//    )

//    val UC_MAP: HashMap<String, DoubleArray> = hashMapOf(
//        "space" to doubleArrayOf(90.01, 200.00),
//        "7" to doubleArrayOf(80.0, 90.0),
//        "67" to doubleArrayOf(79.61, 79.99),
//        "6" to doubleArrayOf(77.2, 79.6),
//        "56" to doubleArrayOf(76.01, 77.19),
//        "5" to doubleArrayOf(72.9, 76.0),
//        "24" to doubleArrayOf(60.01, 72.8),
//        "1" to doubleArrayOf(54.00, 64.00)
//    )

//    val MESSAGE_MAP: HashMap<String, String> = hashMapOf(
//        "space" to "c u in hevannaa",
//        "under" to "r y andegrawun?",
//        "7" to "7th floor",
//        "67" to "6 - 7 floor",
//        "6" to "6th floor",
//        "56" to "5 - 6 floor",
//        "5" to "5th floor",
//        "24" to "2 - 4 floor",
//        "1" to "1st floor"
//    )

    // reverse this logic
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