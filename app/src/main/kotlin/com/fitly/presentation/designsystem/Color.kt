package com.fitly.presentation.designsystem

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/*
 * Generated tonal palettes, not hand-picked colours. Every value below is a tone (CIE L*) of one
 * of six palettes derived from the terracotta seed #B4522F (L* 46.9, C 54.0, hue 46 deg in Lab):
 *
 *   P   primary          hue 46, full chroma        - the terracotta itself
 *   S   secondary        hue 46, chroma 10          - muted, for quieter containers
 *   T   tertiary         hue 101, chroma 26         - shifted to amber/olive
 *   N   neutral          hue 46, chroma 3.5         - the warm off-whites and charcoals
 *   NV  neutral variant  hue 46, chroma 8           - outlines and variant surfaces
 *   E   error            hue 16, chroma 60          - a cold crimson, 30 deg off the primary so it
 *                                                     can never be mistaken for it (ADR 0007)
 *
 * Two deliberate departures from the Material 3 role mapping, both in ADR 0007:
 *   - light `primary` is tone 48, not the standard tone 40, so the brand still reads as orange
 *     rather than brown. Tone 50 was the intent but fails WCAG AA against white (4.48:1); tone 48
 *     passes at 4.81:1. Dark text is not an option here - tone 10 on tone 50 is only 3.82:1.
 *   - `error` is a different hue from the seed rather than the Material default red, which sits
 *     at hue 37 and is nearly indistinguishable from terracotta.
 *
 * Regenerate rather than edit by hand: see docs/adr/0007-terracotta-palette.md.
 */
internal val FitlyLightColors = lightColorScheme(
    primary = Color(0xFFBA532E),  // P48
    onPrimary = Color(0xFFFFFFFF),  // P100
    primaryContainer = Color(0xFFFFDBCE),  // P90
    onPrimaryContainer = Color(0xFF331200),  // P10
    inversePrimary = Color(0xFFFFB59A),  // P80
    secondary = Color(0xFF6E5A53),  // S40
    onSecondary = Color(0xFFFFFFFF),  // S100
    secondaryContainer = Color(0xFFF6DDD5),  // S90
    onSecondaryContainer = Color(0xFF271812),  // S10
    tertiary = Color(0xFF646034),  // T40
    onTertiary = Color(0xFFFFFFFD),  // T100
    tertiaryContainer = Color(0xFFECE4B2),  // T90
    onTertiaryContainer = Color(0xFF1F1C00),  // T10
    error = Color(0xFFB32346),  // E40
    onError = Color(0xFFFFFFFF),  // E100
    errorContainer = Color(0xFFFFD9DB),  // E90
    onErrorContainer = Color(0xFF400010),  // E10
    background = Color(0xFFFFF8F5),  // N98
    onBackground = Color(0xFF201A18),  // N10
    surface = Color(0xFFFFF8F5),  // N98
    onSurface = Color(0xFF201A18),  // N10
    surfaceVariant = Color(0xFFF2DED8),  // NV90
    onSurfaceVariant = Color(0xFF53433E),  // NV30
    surfaceTint = Color(0xFFBA532E),  // P48
    inverseSurface = Color(0xFF352F2D),  // N20
    inverseOnSurface = Color(0xFFF8EFEC),  // N95
    outline = Color(0xFF84736D),  // NV50
    outlineVariant = Color(0xFFD6C3BC),  // NV80
    scrim = Color(0xFF010000),  // N0
    surfaceBright = Color(0xFFFFF8F5),  // N98
    surfaceDim = Color(0xFFE1D8D5),  // N87
    surfaceContainerLowest = Color(0xFFFFFFFF),  // N100
    surfaceContainerLow = Color(0xFFFBF2EF),  // N96
    surfaceContainer = Color(0xFFF5ECE9),  // N94
    surfaceContainerHigh = Color(0xFFEFE6E3),  // N92
    surfaceContainerHighest = Color(0xFFE9E1DE),  // N90
)

internal val FitlyDarkColors = darkColorScheme(
    primary = Color(0xFFFFB59A),  // P80
    onPrimary = Color(0xFF5C1A00),  // P20
    primaryContainer = Color(0xFF842503),  // P30
    onPrimaryContainer = Color(0xFFFFDBCE),  // P90
    inversePrimary = Color(0xFFBA532E),  // P48
    secondary = Color(0xFFD9C2B9),  // S80
    onSecondary = Color(0xFF3E2C26),  // S20
    secondaryContainer = Color(0xFF55433C),  // S30
    onSecondaryContainer = Color(0xFFF6DDD5),  // S90
    tertiary = Color(0xFFCFC897),  // T80
    onTertiary = Color(0xFF343206),  // T20
    tertiaryContainer = Color(0xFF4C481E),  // T30
    onTertiaryContainer = Color(0xFFECE4B2),  // T90
    error = Color(0xFFFFB2B8),  // E80
    onError = Color(0xFF670021),  // E20
    errorContainer = Color(0xFF910032),  // E30
    onErrorContainer = Color(0xFFFFD9DB),  // E90
    background = Color(0xFF19120F),  // N6
    onBackground = Color(0xFFE9E1DE),  // N90
    surface = Color(0xFF19120F),  // N6
    onSurface = Color(0xFFE9E1DE),  // N90
    surfaceVariant = Color(0xFF53433E),  // NV30
    onSurfaceVariant = Color(0xFFD6C3BC),  // NV80
    surfaceTint = Color(0xFFFFB59A),  // P80
    inverseSurface = Color(0xFFE9E1DE),  // N90
    inverseOnSurface = Color(0xFF352F2D),  // N20
    outline = Color(0xFF9F8D87),  // NV60
    outlineVariant = Color(0xFF53433E),  // NV30
    scrim = Color(0xFF010000),  // N0
    surfaceBright = Color(0xFF3E3835),  // N24
    surfaceDim = Color(0xFF19120F),  // N6
    surfaceContainerLowest = Color(0xFF140C09),  // N4
    surfaceContainerLow = Color(0xFF201A18),  // N10
    surfaceContainer = Color(0xFF241E1C),  // N12
    surfaceContainerHigh = Color(0xFF2F2926),  // N17
    surfaceContainerHighest = Color(0xFF3A3331),  // N22
)
