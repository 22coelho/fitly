package com.fitly.presentation.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * The ink to draw on top of [background] - black or white, whichever the WCAG contrast formula
 * favours. Used where the surface is a photo's extracted dominant colour and so cannot be known
 * ahead of time; anywhere the colour comes from the palette, use the matching `on*` role instead.
 */
fun contentColorOn(background: Color): Color {
    val backgroundLuminance = background.luminance()
    val againstWhite = 1.05f / (backgroundLuminance + 0.05f)
    val againstBlack = (backgroundLuminance + 0.05f) / 0.05f
    return if (againstWhite >= againstBlack) Color.White else Color.Black
}
