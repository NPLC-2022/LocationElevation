package com.ex.locationelevation

import android.graphics.Bitmap
import android.graphics.Color
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

class QRGeneratorViewModel: ViewModel() {

    fun generateQRCodeOnImageView(dataForQR: String, image: ImageView) {
        val writer = QRCodeWriter()
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