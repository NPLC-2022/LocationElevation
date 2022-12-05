package com.ex.locationelevation

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.ex.locationelevation.databinding.ActivityOxygenCoinDemoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.timerTask

class OxygenCoinDemoActivity : AppCompatActivity() {

    private lateinit var bind: ActivityOxygenCoinDemoBinding
    private lateinit var oxygenBar: ProgressBar
    private var currentProgress: Int = 1100
    private lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityOxygenCoinDemoBinding.inflate(layoutInflater)
        setContentView(bind.root)

        listeners()

        oxygenBar = bind.OxygenMeterProgressBar
        oxygenBar.max = 1000
        animateChange()
        Thread.sleep(500)

        timer = Timer()
        startLosingOxygen(timer, setTimerTask())

    }

    fun startLosingOxygen(timer: Timer, timerTask: TimerTask){
        timer.scheduleAtFixedRate(timerTask, 500, 2000)
    }

    fun stopLosingOxygen(){ progressIsThisColor(Color.GRAY) }

    fun addOxygen(){
        if(currentProgress+200>1000){ currentProgress = 1000}
        else currentProgress += 200
        progressIsThisColor(Color.GREEN)
    }

    fun cancelTimer(){ timer.cancel() }
    fun resumeTimer(){ timer = Timer() }

    fun setTimerTask(): TimerTask{
        return timerTask {
            progressIsThisColor(ContextCompat.getColor(baseContext, R.color.teal_200))
            if(currentProgress>0){ currentProgress -= 20; animateChange(); }
            else {
                createDeathMessage()
                cancel()
            }
        }
    }

    fun listeners(){
        bind.addOxygenButton.setOnClickListener{
            cancelTimer(); addOxygen(); animateChange()
            Thread.sleep(1000)
            resumeTimer(); startLosingOxygen(timer, setTimerTask()) }

        bind.stopO2Button.setOnClickListener{ cancelTimer(); stopLosingOxygen();  }

        bind.addCoinButton.setOnClickListener{
            var newNumber = bind.coinsValueTextView.text.toString().toInt() + 100
            bind.coinsValueTextView.text = newNumber.toString()
        }
        bind.spendCoinButton.setOnClickListener{
            var newNumber = bind.coinsValueTextView.text.toString().toInt()

            if(newNumber<=0){ newNumber = 0;
                Toast.makeText(this, "You're broke ༼ つ ◕_◕ ༽つ", Toast.LENGTH_SHORT).show()
            } else { newNumber-=100; bind.coinsValueTextView.text = newNumber.toString() }
        }

    }

    fun animateChange(){
        this@OxygenCoinDemoActivity.runOnUiThread{
            ObjectAnimator.ofInt(oxygenBar, "progress", currentProgress).setDuration(2000)
                .start()
        }
    }

    fun progressIsThisColor(color: Int){ oxygenBar.progressTintList = ColorStateList.valueOf(color) }

    fun createDeathMessage(){
        Toast.makeText(this@OxygenCoinDemoActivity, "Ur Dead X_X", Toast.LENGTH_SHORT).show()
    }

}