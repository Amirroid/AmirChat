package ir.amirroid.amirchat.data.helpers

import android.content.Context
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class QrCodeHelper @Inject constructor(
    @ApplicationContext val context: Context
) {
    private val metrics = context.resources.displayMetrics
    private val defaultWidth = metrics.widthPixels * 0.6f
    fun generateQrCode(
        text: String,
        size: Int = defaultWidth.toInt(),
    ): Bitmap? {
        return try {
            val mfw = MultiFormatWriter()
            val mMatrix = mfw.encode(
                text,
                BarcodeFormat.QR_CODE,
                size,
                size
            )
            val barcodeEncoder = BarcodeEncoder()
            barcodeEncoder.createBitmap(mMatrix)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}