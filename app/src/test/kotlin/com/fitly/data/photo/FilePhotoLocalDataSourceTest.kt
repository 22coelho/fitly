@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.fitly.data.photo

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.test.core.app.ApplicationProvider
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThanOrEqualTo
import assertk.assertions.isLessThanOrEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.fitly.domain.util.DataError
import com.fitly.domain.util.Result
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowBitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.max

@Config(application = Application::class)
@RunWith(RobolectricTestRunner::class)
class FilePhotoLocalDataSourceTest {

    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val dataSource = FilePhotoLocalDataSource(context, UnconfinedTestDispatcher())

    @Test
    fun `save writes the photo to a file that exists and decodes back`() = runTest {
        val photoBytes = solidColorPhoto(width = 200, height = 200)

        val result = dataSource.save(photoBytes) as Result.Success

        assertThat(File(result.data).exists()).isTrue()
        assertThat(BitmapFactory.decodeFile(result.data)).isNotNull()
    }

    @Test
    fun `save downscales a photo larger than the max dimension`() = runTest {
        val photoBytes = solidColorPhoto(width = 3000, height = 1500)

        val result = dataSource.save(photoBytes) as Result.Success

        val decoded = BitmapFactory.decodeFile(result.data)!!
        assertThat(max(decoded.width, decoded.height)).isLessThanOrEqualTo(FilePhotoLocalDataSource.MAX_DIMENSION_PX)
    }

    @Test
    fun `save on an extreme aspect-ratio photo still succeeds`() = runTest {
        val photoBytes = solidColorPhoto(width = 4000, height = 2)

        val result = dataSource.save(photoBytes) as Result.Success

        val decoded = BitmapFactory.decodeFile(result.data)!!
        assertThat(decoded.width).isGreaterThanOrEqualTo(1)
        assertThat(decoded.height).isGreaterThanOrEqualTo(1)
    }

    @Test
    fun `save on bytes that aren't a decodable image returns an error`() = runTest {
        // Robolectric's shadow otherwise always fakes a successful decode, which
        // real Android's BitmapFactory doesn't do for genuinely invalid bytes.
        ShadowBitmapFactory.setAllowInvalidImageData(false)

        val result = dataSource.save(byteArrayOf(1, 2, 3))

        assertThat(result).isEqualTo(Result.Error(DataError.Local.UNKNOWN))
    }

    private fun solidColorPhoto(width: Int, height: Int): ByteArray {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.BLUE)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }
}
