package com.ex.locationelevation

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.DiscretePathEffect
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.encoder.QRCode
import kotlinx.coroutines.*
//import kotlinx.coroutines.NonCancellable.message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class QRGeneratorViewModel: ViewModel() {

    private val writer = QRCodeWriter()

//    private val _messageToQR: MutableLiveData<String> by lazy {MutableLiveData<String>()}
//    val messageToQR = _messageToQR

    private val _bitmap: MutableLiveData<Bitmap> by lazy { MutableLiveData<Bitmap>() }
    val bitmap = _bitmap

    fun setMessageToQR(message:String){
//        _messageToQR.value = message
//        generateQRCodeOnImageViewWithCallBack()
        collectMessageToQR(message)
    }

    fun collectMessageToQR(message:String) = viewModelScope.launch(Dispatchers.Default) {
        generateQRCodeOnImageViewWithCallBack(message).collectLatest { latestBitmap ->
            Log.d("debug","new Bitmap incoming: $message")
            _bitmap.postValue(latestBitmap)
//            _bitmap.value = it
        }
    }

    // experimental running version
    fun generateQRCodeOnImageViewWithCallBack(message:String) = channelFlow<Bitmap> {
        try {
            val bitMatrix = writer.encode(message, BarcodeFormat.QR_CODE, 256, 256)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            launch{ send(bmp) }
//            send(bmp)
//            image.setImageBitmap(bmp)

        } catch (e: WriterException) { e.printStackTrace() }

        awaitClose{ viewModelScope.cancel() }
    }
//        .launchIn(viewModelScope)





//this works but not good enough.

    fun generateQRCodeOnImageView(dataForQR: String, image: ImageView) {

        try {
            val bitMatrix = writer.encode(dataForQR, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            image.setImageBitmap(bmp)

        } catch (e: WriterException) { e.printStackTrace() }
    }






}



