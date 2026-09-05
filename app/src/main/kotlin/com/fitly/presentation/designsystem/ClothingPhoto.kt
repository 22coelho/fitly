package com.fitly.presentation.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import java.io.File

/** Renders the photo at [photoPath], or nothing while there isn't one yet. */
@Composable
fun ClothingPhoto(photoPath: String?, modifier: Modifier = Modifier) {
    if (photoPath == null) return
    AsyncImage(
        model = File(photoPath),
        contentDescription = null,
        modifier = modifier,
    )
}
