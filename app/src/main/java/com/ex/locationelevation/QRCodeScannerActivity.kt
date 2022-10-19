package com.ex.locationelevation

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.ex.locationelevation.databinding.ActivityQrcodeScannerBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.zxing.BarcodeFormat

class QRCodeScannerActivity : AppCompatActivity() {

    private lateinit var bind: ActivityQrcodeScannerBinding
    private lateinit var model: QRCodeScannerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityQrcodeScannerBinding.inflate(layoutInflater)
        setContentView(bind.root)

        setUpPermission()
        model = ViewModelProvider(this)[QRCodeScannerViewModel::class.java]
        model.deployCodeScanner(this, bind.QRCodeScannerView)

        bind.QRCodeScannerView.setOnClickListener { model.startScannerPreview() }
//            codeScanner.startPreview()

        model.qrMessage.observe(this){ bind.QRCodeResultTextView.text = it.toString() }

    }

    override fun onResume() {
        super.onResume()
        if(::model.isInitialized){ model.startScannerPreview() }
//            codeScanner.startPreview()
    }

    override fun onPause() {
        if(::model.isInitialized){ model.releaseResources() }
//            codeScanner.releaseResources()
        super.onPause()
    }

    fun setUpPermission(){
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if(permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 0)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            0 -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "App needs camera permission", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}