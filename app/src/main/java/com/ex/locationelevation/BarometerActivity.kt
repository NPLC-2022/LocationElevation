package com.ex.locationelevation

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.getAltitude
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.ex.locationelevation.databinding.ActivityBarometerBinding

class BarometerActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    private lateinit var mBarometer: Sensor
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityBarometerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBarometerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mBarometer = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)


        binding.getAltitudeFromBarometerButton.setOnClickListener{
//            theSensors.getAltitudeFromBarometer()
        }

    }

    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager.registerListener(this, mBarometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val value = event?.values
        if(value != null) {
            let {
                val alt = getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, value[0])
                binding.BarometerValueTextView.setText(
                    String.format("%.3f mbar \n" +
                            "alt in meters? $alt\n", value[0])
                )

            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // change the color between green and red if there's a dip
        // need more searching
    }

}
