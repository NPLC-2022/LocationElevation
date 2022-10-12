package com.ex.locationelevation

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ex.locationelevation.databinding.ActivityQrgeneratorBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

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