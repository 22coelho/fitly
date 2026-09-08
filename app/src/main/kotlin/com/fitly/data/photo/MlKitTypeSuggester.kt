package com.fitly.data.photo

import android.graphics.BitmapFactory
import android.media.ExifInterface
import com.fitly.domain.classifier.DetectedLabel
import com.fitly.domain.classifier.GarmentTypeMapper
import com.fitly.domain.datasource.TypeSuggester
import com.fitly.domain.model.ClothingType
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.io.ByteArrayInputStream
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * ML Kit's bundled (on-device inference, model shipped in the APK) Image Labeling, narrowed to a
 * garment-type guess via [GarmentTypeMapper]. The photo itself never leaves the device; the
 * library does send its own usage telemetry over the network regardless (no opt-out - see ADR
 * 0008). Failures - undecodable bytes, the labeler itself throwing - are treated the same as "no
 * confident match": this is a best-effort suggestion, never a blocking dependency for saving an
 * item.
 *
 * The labeler client is built once and reused for this instance's lifetime (it's a Koin single) -
 * ML Kit's own docs recommend this over re-creating it per call, which would also reload the model
 * every time.
 */
class MlKitTypeSuggester(
    private val mapper: GarmentTypeMapper = GarmentTypeMapper(),
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : TypeSuggester {

    private val labeler by lazy { ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS) }

    override suspend fun suggest(photoBytes: ByteArray): ClothingType? = withContext(defaultDispatcher) {
        val bitmap = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.size) ?: return@withContext null
        try {
            val image = InputImage.fromBitmap(bitmap, exifRotationDegrees(photoBytes))
            val labels = labeler.process(image)
                .await()
                .map { DetectedLabel(it.text, it.confidence) }
            mapper.map(labels)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        } finally {
            bitmap.recycle()
        }
    }

    /** The Photo Picker hands back a JPEG's raw pixel data, which is very commonly stored
     * sideways relative to how it displays - ML Kit needs the actual upright rotation to label
     * the garment correctly, not the pixel data's own orientation. */
    private fun exifRotationDegrees(photoBytes: ByteArray): Int {
        val orientation = try {
            ExifInterface(ByteArrayInputStream(photoBytes))
                .getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        } catch (e: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }
}
