package ua.nure.nomnomsave.repository.resource

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set

class ResourceRepositoryImpl (
    private val context: Context,

): ResourceRepository {
    override fun getStringByResource(res: Int): String {
        return context.getString(res)
    }

    override suspend fun generateQR(text: String, size: Int): Bitmap {
        val bits = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
        val bitmap = createBitmap(size, size, Bitmap.Config.RGB_565)
        for(x in 0 until size) {
            for(y in 0 until size) {
                bitmap[x, y] = if (bits[x, y]) Color.Black.toArgb() else Color.White.toArgb()
            }
        }
        return bitmap
    }
}