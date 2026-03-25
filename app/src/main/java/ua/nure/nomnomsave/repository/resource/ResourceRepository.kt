package ua.nure.nomnomsave.repository.resource

import android.graphics.Bitmap

interface ResourceRepository {
    fun getStringByResource(res: Int): String
    suspend fun generateQR(text: String, size: Int): Bitmap
}