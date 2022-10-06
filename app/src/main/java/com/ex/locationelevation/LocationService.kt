package com.ex.locationelevation

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.session.PlaybackState.ACTION_STOP
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class LocationService:Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate(){
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(){
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location...")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient.getLocationUpdates(1000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->

//                lat.value = location.latitude.toString().takeLast(3)

                lat = location.latitude
                lon = location.longitude
                alt = location.altitude

                val lati = location.latitude.toString().takeLast(3)
                val long = location.longitude.toString().takeLast(3)
                val alti = location.altitude.toString().takeLast(3)

                val updatedNotif = notification.setContentText( "Location: ($lati, $long, $alti)" )
                notificationManager.notify(1, updatedNotif.build())
            }
            .launchIn(serviceScope)

//        serviceScope
        startForeground(1, notification.build())
    }

    private fun stop(){
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

//    private val _lon: MutableLiveData<Double> by lazy { MutableLiveData<Double>() }




    companion object{
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"

        var lat = 0.0
        var lon = 0.0
        var alt = 0.0

        val latestLatitude: Flow<Double> = flow{
            while(true){ emit(lat); delay(1000) }
        }
        val latestLongitude: Flow<Double> = flow {
            while(true){ emit(lon); delay(1000)}
        }
        val latestAltitude:Flow<Double> = flow {
            while(true){ emit(alt); delay(1000)}
        }

    }


}