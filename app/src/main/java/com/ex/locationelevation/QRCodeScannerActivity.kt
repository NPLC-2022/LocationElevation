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

    private lateinit var codeScanner: CodeScanner
    private lateinit var bind: ActivityQrcodeScannerBinding
    private lateinit var model: QRCodeScannerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityQrcodeScannerBinding.inflate(layoutInflater)
        setContentView(bind.root)
        setUpPermission()

        model = ViewModelProvider(this)[QRCodeScannerViewModel::class.java]

        codeScanner = CodeScanner(this, bind.QRCodeScannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }

        bind.QRCodeScannerView.setOnClickListener {
            codeScanner.startPreview()
        }

//        listener()
//        setupStandardBottomSheet()
//        setupforqr()
    }

//    private fun setupStandardBottomSheet() {
//        standardBottomSheetBehavior = BottomSheetBehavior.from(sheet)
//        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
//
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//
//
//            }
//
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//
//            }
//        }
//        standardBottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
//        standardBottomSheetBehavior.saveFlags = BottomSheetBehavior.SAVE_ALL
//
//        BottomSheetBehavior.from(sheet).apply {
//            peekHeight = 200
//            this.state = BottomSheetBehavior.STATE_COLLAPSED
//        }
//    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

//    fun listener(){
//        backbutton_scanactivity.setOnClickListener{
//            finish()
//        }
//    }

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

//    fun setupforqr(){
//
//        val text = login.curuser.fullName
//
//        val encoder = BarcodeEncoder()
//        val bitmap = encoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 1000, 1000)
//        my_qr.setImageBitmap(bitmap)
//
//        nama_barcode.text = text
//        card_number_text.text = login.curuser.bank_account
//    }
}