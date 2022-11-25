package com.ex.locationelevation

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.media.session.PlaybackState.ACTION_STOP
import android.os.Build.VERSION_CODES.R
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
//import com.ex.locationelevation.LocationService.Companion.lon
import com.google.android.gms.location.LocationServices
import com.google.zxing.qrcode.encoder.QRCode
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.start
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*


class LocationService:Service() {

    val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    private fun initializeProvider(){
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext))
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate(){
        super.onCreate()
        initializeProvider()
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

                lat = location.latitude
                lon = location.longitude
                alt = location.altitude
                acc = location.accuracy
                alt_acc = location.verticalAccuracyMeters

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

    companion object{
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
//        }

        val dummyFlow = flow {
            var holder = 10
            Log.d("dummy", "Dummy Flow Started")

            while (holder > 0){
                emit(holder--)
                Log.d("dummy", "Dummy Flow is $holder")
                delay(1000)
            }
        }

        private var lat = 0.0
        private var lon = 0.0
        private var alt = 0.0
        private var acc = 0F
        private var alt_acc = 0F

//        var FlowKey = true

        val latestLatitude: Flow<Double> = flow<Double> {
            Log.d("Location_Latitude", "Latitude Flow Started")
            while(true){
                emit(lat); delay(1000)
                Log.d("Location_Latitude", "Latitude: $lat \n Thread: ${Thread.currentThread().name}")
            }
        }

        val latestLongitude: Flow<Double> = flow<Double> {
            Log.d("Location_Longitude", "Longitude Flow Started")
            while(true){
                emit(lon); delay(1000)
                Log.d("Location_Longitude", "Longitude: $lon \n" +
                        " Thread: ${Thread.currentThread().name}")
            }
        }

        val latestAltitude:Flow<Double> = flow {
            Log.d("Location_Altitude", "Altitude Flow Started")
            while(true){
                emit(alt); delay(1000)
                Log.d("Location_Altitude", "Altitude: $alt \n" +
                        " Thread: ${Thread.currentThread().name}")
            }
        }
        val latestAccuracy: Flow<Float> = flow {
            while(true){
                emit(acc); delay(1000)
                Log.d("Location_Accuracy", "Accuracy: $acc \n" +
                        " Thread: ${Thread.currentThread().name}")
            }
        }

        val latestVerticalAccuracy: Flow<Float> = flow {
            while(true){
                emit(alt_acc); delay(1000)
                Log.d("Location_Alt_Acc", "Alt-Acc: $alt_acc \n" +
                        " Thread: ${Thread.currentThread().name}")
            }
        }


    }


}