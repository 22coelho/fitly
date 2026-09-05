package com.fitly.data.photo

import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.palette.graphics.Palette
import com.fitly.domain.datasource.DominantColorExtractor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PaletteDominantColorExtractor(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : DominantColorExtractor {

    override suspend fun extract(photoBytes: ByteArray): Int = withContext(defaultDispatcher) {
        val bitmap = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.size)
            ?: return@withContext Color.GRAY
        try {
            Palette.from(bitmap).generate().dominantSwatch?.rgb ?: Color.GRAY
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Color.GRAY
        } finally {
            bitmap.recycle()
        }
    }
}
