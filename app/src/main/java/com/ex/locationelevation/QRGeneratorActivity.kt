package com.ex.locationelevation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.ViewModelProvider
import com.ex.locationelevation.databinding.ActivityQrgeneratorBinding
import java.util.*
import kotlin.concurrent.timerTask

class QRGeneratorActivity : AppCompatActivity() {

    private lateinit var bind: ActivityQrgeneratorBinding
    private lateinit var thisModel: QRGeneratorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityQrgeneratorBinding.inflate(layoutInflater)
        setContentView(bind.root)
        supportActionBar?.title = "QR Code"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        thisModel = ViewModelProvider(this)[QRGeneratorViewModel::class.java]

        setUpListener()

        thisModel.bitmap.observe(this){
            bind.qrCodeImageView.setImageBitmap(it)
        }



    }



    fun setUpListener(){

        var theTimer = Timer()

        bind.dataEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                start + after
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                start + before
            }

            override fun afterTextChanged(s: Editable?) {

                theTimer.cancel();
                theTimer = Timer()
                val setMessageTask = timerTask { thisModel.setMessageToQR(s.toString())}

                if(s.toString().isNotEmpty() && ::thisModel.isInitialized){
//                    thisModel.generateQRCodeOnImageView(dataForQR, bind.qrCodeImageView)
//                    thisModel.setMessageToQR(s.toString())
                    theTimer.schedule(setMessageTask, 500)
                }
            }
        })

        bind.generateQRCodeButton.setOnClickListener{
            val dataForQR = bind.dataEditText.text.toString()
            if(dataForQR.isNotEmpty()){
                thisModel.generateQRCodeOnImageView(dataForQR, bind.qrCodeImageView)
            }
        }

        bind.returnToClientProviderButton.setOnClickListener{
            startActivity(Intent(this, UsingClientProvider::class.java))
            finish()
        }

        bind.ScanQRButton.setOnClickListener{
            startActivity(Intent(this, QRCodeScannerActivity::class.java))
            finish()
        }



    }

}