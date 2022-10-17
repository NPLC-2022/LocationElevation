package com.ex.locationelevation

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.*
import com.ex.locationelevation.databinding.ActivityQrcodeScannerBinding

class QRCodeScannerViewModel: ViewModel() {

    private lateinit var codeScanner: CodeScanner
    private val _qrMessage: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val qrMessage = _qrMessage

    fun deployCodeScanner(thisss:Context, scannerView: CodeScannerView){

        codeScanner = CodeScanner(thisss, scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW

        codeScanner.setAutoFocusInterval(2000)
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.scanMode = ScanMode.CONTINUOUS
        codeScanner.scanMode = ScanMode.PREVIEW


        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not


        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            _qrMessage.value = it.toString()
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            _qrMessage.value = "An Error Occured"
        }

    }

    fun startScannerPreview(){
        if(::codeScanner.isInitialized){ codeScanner.startPreview() }
    }

    fun releaseResources(){
        if(::codeScanner.isInitialized){ codeScanner.releaseResources() }
    }


}