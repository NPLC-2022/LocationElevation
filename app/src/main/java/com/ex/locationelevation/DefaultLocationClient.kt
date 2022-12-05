package com.ex.locationelevation

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.widget.Toast
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient,
    ):LocationClient {

    //
    // Permissions First
    // Creating Relevant objects
    // returning it as callbackflor

//    private fun returnActiveClient():FusedLocationProviderClient = client

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {

        return callbackFlow {

//            if(!context.hasLocationPermission()){
//                throw LocationClient.LocationException("Missing location permission")
//            }

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if(!isGPSEnabled && !isNetworkEnabled){
                throw LocationClient.LocationException("GPS is disabled")
            }

            val request = createRequest(interval)

            val locationIsCallingBack = object:LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    p0.locations.lastOrNull()?.let { location ->
                        launch { send(location) }
                    }
                }
            }

            client.requestLocationUpdates(
                request,
                locationIsCallingBack,
                Looper.getMainLooper()
            )

            awaitClose{
                client.removeLocationUpdates(locationIsCallingBack)
            }

        }
    }

    private fun createRequest(interval:Long):LocationRequest{
        val request = LocationRequest.create()
            .setInterval(interval)
            .setFastestInterval(interval)
            .setMaxWaitTime(100)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setWaitForAccurateLocation(true)

        return request
    }




}