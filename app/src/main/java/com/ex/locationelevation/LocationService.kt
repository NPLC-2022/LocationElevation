package com.ex.locationelevation

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.media.session.PlaybackState.ACTION_STOP
import android.os.IBinder
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

                _freshLatitude.postValue(location.latitude)
                _freshLongitude.postValue(location.longitude)
                _freshAltitude.postValue(location.altitude)
                _freshAccuracy.postValue(location.accuracy)

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

    fun shareLocationFlow(contextual:Context): Flow<Location> = channelFlow<Location> {

        DefaultLocationClient(contextual, LocationServices.getFusedLocationProviderClient(contextual))
        .getLocationUpdates(1000L).onEach {
            launch(Dispatchers.IO) { send(it) }
        }
    }
    // this function already knows that it's going to do some degree of suspending

    fun latestLatitudeFlow(){

    }

    companion object{
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
//        }

        suspend fun dummyFlow() = flow {
            var holder = 10
            while (holder > 0){
                emit(--holder)
                delay(1000)
            }
        }

        var lat = 0.0
        var lon = 0.0
        var alt = 0.0

        val latestLatitude: Flow<Double> = flow {
            while(true){ emit(lat); delay(1000) }
        }
        val latestLongitude: Flow<Double> = flow {
            while(true){ emit(lon); delay(1000)}
        }
        val latestAltitude:Flow<Double> = flow<Double> {
            while(true){ emit(alt); delay(1000)}
        }

        private val _freshLatitude: MutableLiveData<Double> by lazy { MutableLiveData<Double>() }
        val freshLatitude get() = _freshLatitude

        private val _freshLongitude: MutableLiveData<Double> by lazy { MutableLiveData<Double>() }
        val freshLongitude get() = _freshLongitude

        private val _freshAltitude: MutableLiveData<Double> by lazy { MutableLiveData<Double>() }
        val freshAltitude get() = _freshAltitude

        private val _freshAccuracy: MutableLiveData<Float> by lazy {MutableLiveData<Float>() }
        val freshAccuracy get() = _freshAccuracy


    }


}