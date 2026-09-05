package com.fitly.data.photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.fitly.data.util.safeLocalCall
import com.fitly.domain.datasource.PhotoLocalDataSource
import com.fitly.domain.util.DataError
import com.fitly.domain.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlin.math.max

class FilePhotoLocalDataSource(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PhotoLocalDataSource {

    override suspend fun save(photoBytes: ByteArray): Result<String, DataError.Local> =
        withContext(ioDispatcher) {
            safeLocalCall {
                val bitmap = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.size)
                    ?: error("Not a decodable image")
                try {
                    val resized = bitmap.downscaleToMax(MAX_DIMENSION_PX)
                    try {
                        val photosDir = File(context.filesDir, "photos").apply { mkdirs() }
                        val file = File(photosDir, "${UUID.randomUUID()}.jpg")
                        FileOutputStream(file).use { out ->
                            // Re-encoding as JPEG never carries over the source's EXIF
                            // data, so this also strips any metadata (GPS, orientation, etc).
                            resized.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
                        }
                        file.absolutePath
                    } finally {
                        if (resized !== bitmap) resized.recycle()
                    }
                } finally {
                    bitmap.recycle()
                }
            }
        }

    private fun Bitmap.downscaleToMax(maxDimensionPx: Int): Bitmap {
        val longestEdge = max(width, height)
        if (longestEdge <= maxDimensionPx) return this
        val scale = maxDimensionPx.toFloat() / longestEdge
        val scaledWidth = (width * scale).toInt().coerceAtLeast(1)
        val scaledHeight = (height * scale).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(this, scaledWidth, scaledHeight, true)
    }

    companion object {
        const val MAX_DIMENSION_PX = 1080
        private const val JPEG_QUALITY = 85
    }
}
