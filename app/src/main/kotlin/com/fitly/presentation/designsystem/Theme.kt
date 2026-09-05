package com.fitly.presentation.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * The app's only theme. Follows the system's light/dark setting - there is no in-app toggle,
 * because there is no settings screen to put one on.
 *
 * Plain [MaterialTheme], not MaterialExpressiveTheme: at the material3 version this project is
 * pinned to (1.3.2, via the BOM in ADR 0006) the expressive theme and its components are not
 * public API. See ADR 0007.
 */
@Composable
fun FitlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) FitlyDarkColors else FitlyLightColors,
        shapes = FitlyShapes,
        typography = FitlyTypography,
        content = content,
    )
}
