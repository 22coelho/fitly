package com.fitly.presentation.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.fitly.R
import java.io.File

/** How a photo fills the space it is given. */
enum class PhotoFit {
    /** Nothing is cropped; the leftover space is filled with the garment's own dominant colour. */
    Contain,

    /** Fills the frame, cropping the overflow. For grids, where ragged edges cost more than a crop. */
    Cover,
}

/**
 * A ClothingItem's photo at a fixed [aspectRatio], so rows and grids line up regardless of what
 * the Photo Picker handed back. Pass `null` to fill whatever bounds the caller gives instead -
 * what a stacked outfit needs, where the three garments have to share one screen's height.
 *
 * With [PhotoFit.Contain] the letterbox is painted with the photo's own extracted dominant colour,
 * pulled most of the way towards the surface: at full strength it competes with the garment, and
 * left transparent it reads as a hole.
 */
@Composable
fun ClothingPhoto(
    photoPath: String?,
    dominantColor: Int?,
    modifier: Modifier = Modifier,
    aspectRatio: Float? = 3f / 4f,
    fit: PhotoFit = PhotoFit.Contain,
    shape: Shape = MaterialTheme.shapes.medium,
) {
    val surface = MaterialTheme.colorScheme.surfaceContainerHigh
    val backdrop = when {
        fit == PhotoFit.Cover || dominantColor == null -> surface
        else -> lerp(Color(dominantColor), surface, 0.55f)
    }
    Box(
        modifier = modifier
            .then(if (aspectRatio != null) Modifier.aspectRatio(aspectRatio) else Modifier)
            .clip(shape)
            .background(backdrop),
    ) {
        if (photoPath != null) {
            AsyncImage(
                model = File(photoPath),
                contentDescription = stringResource(R.string.cd_clothing_photo),
                contentScale = if (fit == PhotoFit.Cover) ContentScale.Crop else ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

/** Mixes [from] towards [to] by [fraction]. Kept local so the component owns its own backdrop rule. */
private fun lerp(from: Color, to: Color, fraction: Float) = Color(
    red = from.red + (to.red - from.red) * fraction,
    green = from.green + (to.green - from.green) * fraction,
    blue = from.blue + (to.blue - from.blue) * fraction,
)

@Preview
@Composable
private fun ClothingPhotoEmptyPreview() {
    FitlyTheme {
        ClothingPhoto(
            photoPath = "",
            dominantColor = 0xFF6C8C5A.toInt(),
            modifier = Modifier.width(160.dp),
        )
    }
}
