package com.fitly.data.photo

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isLessThanOrEqualTo
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import kotlin.math.abs

@Config(application = Application::class)
@RunWith(RobolectricTestRunner::class)
class PaletteDominantColorExtractorTest {

    private val extractor = PaletteDominantColorExtractor()

    @Test
    fun `a solid-color photo extracts that color`() = runTest {
        val photoBytes = solidColorPhoto(Color.BLUE)

        val result = extractor.extract(photoBytes)

        assertColorCloseTo(result, Color.BLUE)
    }

    @Test
    fun `dominant color is the majority color, not a minority patch`() = runTest {
        val photoBytes = majorityColorPhoto(majority = Color.GREEN, minority = Color.RED)

        val result = extractor.extract(photoBytes)

        assertColorCloseTo(result, Color.GREEN)
    }

    @Test
    fun `bytes that aren't a decodable image fall back to a neutral color`() = runTest {
        val result = extractor.extract(byteArrayOf(1, 2, 3))

        assertThat(result).isEqualTo(Color.GRAY)
    }

    private fun solidColorPhoto(color: Int): ByteArray {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(color)
        return bitmap.toLosslessBytes()
    }

    private fun majorityColorPhoto(majority: Int, minority: Int): ByteArray {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(majority)
        for (x in 0 until 10) {
            for (y in 0 until 10) {
                bitmap.setPixel(x, y, minority)
            }
        }
        return bitmap.toLosslessBytes()
    }

    // PNG (lossless) on purpose: this test is about the extraction logic, not
    // compression fidelity, so the fixture must round-trip pixels exactly.
    private fun Bitmap.toLosslessBytes(): ByteArray {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    // Palette quantizes colors into buckets before reporting a swatch, so even an
    // exact-input color comes back slightly shifted per channel - assert closeness,
    // not bit-exact equality.
    private fun assertColorCloseTo(actual: Int, expected: Int, tolerancePerChannel: Int = 16) {
        assertThat(abs(Color.red(actual) - Color.red(expected))).isLessThanOrEqualTo(tolerancePerChannel)
        assertThat(abs(Color.green(actual) - Color.green(expected))).isLessThanOrEqualTo(tolerancePerChannel)
        assertThat(abs(Color.blue(actual) - Color.blue(expected))).isLessThanOrEqualTo(tolerancePerChannel)
    }
}
