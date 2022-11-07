package com.ex.locationelevation

import android.animation.ValueAnimator
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ex.locationelevation.databinding.ActivitySwitchesAndCrystalsBinding


class SwitchesAndCrystalsActivity : AppCompatActivity() {

    private lateinit var bind: ActivitySwitchesAndCrystalsBinding
    private var defaultStatusArray = booleanArrayOf(true, false, true, true, false)
    private var crystalStatusArray = defaultStatusArray
    private lateinit var crystalArray: Array<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySwitchesAndCrystalsBinding.inflate(layoutInflater)
        setContentView(bind.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        crystalArray = arrayOf(
            bind.crystalOneButton, bind.crystalTwoButton,
            bind.crystalThreeButton, bind.crystalFourButton,
            bind.crystalFiveButton,
        )
        setCrystalStatus(crystalArray)

        listeners()

    }

    fun listeners() {

        val switchOne1 = 0
        val switchOne2 = 2

        // Green Switch // 1 & 3 ~> 0, 2
        bind.switchOneButton.setOnClickListener{
            toggleCrystal(crystalStatusArray[switchOne1], crystalArray[switchOne1])
            toggleTrueFalse(switchOne1)
            toggleCrystal(crystalStatusArray[switchOne2], crystalArray[switchOne2])
            toggleTrueFalse(switchOne2)

        }

        val switchTwo1 = 1
        val switchTwo2 = 3

        // Purple Switch // 2 & 4 ~> 1, 3
        bind.switchTwoButton.setOnClickListener{
            toggleCrystal(crystalStatusArray[switchTwo1], crystalArray[switchTwo1])
            toggleTrueFalse(switchTwo1)
            toggleCrystal(crystalStatusArray[switchTwo2], crystalArray[switchTwo2])
            toggleTrueFalse(switchTwo2)

        }

        val switchThree1 = 3
        val switchThree2 = 4

        // Blue Switch // 4 & 5 ~> 3, 4
        bind.switchThreeButton.setOnClickListener{
            toggleCrystal(crystalStatusArray[switchThree1], crystalArray[switchThree1])
            toggleTrueFalse(switchThree1)
            toggleCrystal(crystalStatusArray[switchThree2], crystalArray[switchThree2])
            toggleTrueFalse(switchThree2)

        }

        // Reset Crystal to Original State
        bind.resetCrystalButton.setOnClickListener{ resetCrystalToOriginal() }

    }

    // What is the most appropriate Resource for the crystal
    // at the moment we just buttons that do nothing when pressed.

    // toggle true false
    fun toggleTrueFalse(index:Int){ crystalStatusArray[index] = !crystalStatusArray[index] }

    // toggle color function
    fun toggleCrystal(status: Boolean, view:View){

        val beforeColor = if(status) R.color.green_400 else R.color.red_400
        val afterColor = if(status) R.color.red_400 else R.color.green_400

        val beforeColorReady = ContextCompat.getColor(this, beforeColor)
        val afterColorReady = ContextCompat.getColor(this, afterColor)

        val colorAnimation = ValueAnimator.ofArgb( beforeColorReady, afterColorReady )

        colorAnimation.duration = 500 // milliseconds

        colorAnimation.addUpdateListener { animator -> view.setBackgroundColor(animator.animatedValue as Int) }
        colorAnimation.start()

    }

    private fun setCrystalStatus(crystalArray: Array<Button>) {
        for(i in 0..4){
            if(crystalStatusArray[i]) crystalArray[i].setBackgroundColor(ContextCompat.getColor(this, R.color.green_400))
            else crystalArray[i].setBackgroundColor(ContextCompat.getColor(this, R.color.red_400))
        }
    }

    fun resetCrystalToOriginal(){
        crystalStatusArray = booleanArrayOf(true, false, true, true, false)
        for (i in 0..4){
            toggleCrystal(crystalStatusArray[i], crystalArray[i])
        }
    }

}