package com.fitly.presentation.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import com.fitly.R

/**
 * Instrument Serif ships a single weight, so hierarchy here comes from size and line height only -
 * never from asking for a bold that does not exist, which would make the platform synthesise a
 * smeared fake bold.
 */
private val InstrumentSerif = FontFamily(Font(R.font.instrument_serif_regular, FontWeight.Normal))

private val Default = Typography()

/**
 * Serif on display, headline and title; Roboto (the platform default) on body and label. A serif
 * of one weight stops reading well at body sizes, and labels sit inside chips and buttons where
 * the extra stroke contrast only costs legibility.
 */
internal val FitlyTypography = Typography(
    displayLarge = Default.displayLarge.serif(lineHeightRatio = 1.08f),
    displayMedium = Default.displayMedium.serif(lineHeightRatio = 1.10f),
    displaySmall = Default.displaySmall.serif(lineHeightRatio = 1.12f),
    headlineLarge = Default.headlineLarge.serif(lineHeightRatio = 1.15f),
    headlineMedium = Default.headlineMedium.serif(lineHeightRatio = 1.18f),
    headlineSmall = Default.headlineSmall.serif(lineHeightRatio = 1.20f),
    titleLarge = Default.titleLarge.serif(lineHeightRatio = 1.25f),
    titleMedium = Default.titleMedium.serif(lineHeightRatio = 1.30f),
    titleSmall = Default.titleSmall.serif(lineHeightRatio = 1.35f),
)

/**
 * Swaps in the serif and retightens the metrics Material sized for Roboto: this face runs narrower
 * and taller, so it wants slightly negative tracking and a line height set from its own size.
 */
private fun TextStyle.serif(lineHeightRatio: Float) = copy(
    fontFamily = InstrumentSerif,
    fontWeight = FontWeight.Normal,
    lineHeight = fontSize * lineHeightRatio,
    letterSpacing = (-0.015f).em,
)
