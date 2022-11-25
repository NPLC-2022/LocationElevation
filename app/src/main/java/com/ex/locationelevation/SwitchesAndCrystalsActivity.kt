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

    // Red -> Green // Green -> Blue // Blue -> Red
    private lateinit var bind: ActivitySwitchesAndCrystalsBinding
    private var colorKeyArray = charArrayOf('r', 'g', 'b')
    private var defaultStatusArray = charArrayOf('r', 'g', 'b', 'g', 'b')
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
            toggleCrystal(switchOne1); toggleCrystal(switchOne2)
        }

        val switchTwo1 = 1
        val switchTwo2 = 3

        // Purple Switch // 2 & 4 ~> 1, 3
        bind.switchTwoButton.setOnClickListener{
            toggleCrystal(switchTwo1); toggleCrystal(switchTwo2)
        }

        val switchThree1 = 3
        val switchThree2 = 4

        // Blue Switch // 4 & 5 ~> 3, 4
        bind.switchThreeButton.setOnClickListener{
            toggleCrystal(switchThree1); toggleCrystal(switchThree2)
        }

        val switchFive1 = 2
        val switchFive2 = 4

        // Yellow Switch // 3 & 5 ~> 2, 4
        bind.switchFourButton.setOnClickListener{
            toggleCrystal(switchFive1); toggleCrystal(switchFive2)
        }

        val switchFour1 = 1
        val switchFour2 = 2

        // Pink Switch // 2, 3 ~> 1, 2
        bind.switchFourButton.setOnClickListener{
            toggleCrystal(switchFour1); toggleCrystal(switchFour2)
        }

        // Reset Crystal to Original State
        bind.resetCrystalButton.setOnClickListener{ resetCrystalToOriginal() }

    }

    // What is the most appropriate Resource for the crystal
    // at the moment we just buttons that do nothing when pressed.

    // toggle // Red -> Green // Green -> Blue // Blue -> Red
    // toggle color function - Int
    fun toggleCrystal(index:Int){

        // R -> G
        if(crystalStatusArray[index] == colorKeyArray[0]){
            crystalStatusArray[index] = colorKeyArray[1]
        }
        // G -> B
        if(crystalStatusArray[index] == colorKeyArray[1]){
            crystalStatusArray[index] = colorKeyArray[2]
        }
        // B -> R
        if(crystalStatusArray[index] == colorKeyArray[2]){
            crystalStatusArray[index] = colorKeyArray[0]
        }

        val status = crystalStatusArray[index]
        val view = crystalArray[index]

        val beforeColor = if(status == 'r') { R.color.red_400 }
                            else if (status == 'g') { R.color.green_400 }
                            else if (status == 'b') { R.color.blue_400 }
                            else { R.color.amber_500 }

        // Red -> Green // Green -> Blue // Blue -> Red
        val afterColor = if(status == 'r') { R.color.green_400 }
                            else if (status == 'g') { R.color.blue_400 }
                            else if (status == 'b') { R.color.red_400 }
                            else { R.color.amber_500 }

        val beforeColorReady = ContextCompat.getColor(this, beforeColor)
        val afterColorReady = ContextCompat.getColor(this, afterColor)

        val colorAnimation = ValueAnimator.ofArgb( beforeColorReady, afterColorReady )

        colorAnimation.duration = 500 // milliseconds

        colorAnimation.addUpdateListener { animator -> view.setBackgroundColor(animator.animatedValue as Int) }
        colorAnimation.start()

    }

    private fun setCrystalStatus(crystalArray: Array<Button>) {
        for(i in 0..4){
            if(crystalStatusArray[i] == 'g') crystalArray[i].setBackgroundColor(ContextCompat.getColor(this, R.color.green_400))
            else if(crystalStatusArray[i] == 'r') crystalArray[i].setBackgroundColor(ContextCompat.getColor(this, R.color.red_400))
            else if(crystalStatusArray[i] == 'b') crystalArray[i].setBackgroundColor(ContextCompat.getColor(this, R.color.blue_400))
        }
    }

    fun resetCrystalToOriginal(){
        for (i in 0..4){
            if(defaultStatusArray[i] == crystalStatusArray[i]){ continue }
            toggleCrystal(i)
        }
    }

}