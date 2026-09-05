package com.fitly.presentation.designsystem

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/** The primary action on a screen. There should only ever be one of these in view. */
@Composable
fun FitlyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(onClick = onClick, modifier = modifier, enabled = enabled) {
        Text(text)
    }
}

/** A secondary action, for when it sits beside a [FitlyButton] or carries less weight than one. */
@Composable
fun FitlyOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(onClick = onClick, modifier = modifier, enabled = enabled) {
        Text(text)
    }
}

/**
 * An icon-only action. [contentDescription] is required rather than nullable: an icon button is by
 * definition interactive, so there is no case where it carries no meaning for a screen reader.
 */
@Composable
fun FitlyIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = LocalContentColor.current,
) {
    IconButton(onClick = onClick, modifier = modifier, enabled = enabled) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = tint)
    }
}

/** Escape hatch for the rare button that needs its own layout inside. Prefer [FitlyButton]. */
@Composable
fun FitlyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    Button(onClick = onClick, modifier = modifier, enabled = enabled, content = content)
}

/**
 * A low-emphasis action, for bars and dialogs where a filled button would shout. [destructive]
 * paints it in the error colour, which ADR 0007 keeps 30 degrees of hue away from the primary.
 */
@Composable
fun FitlyTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    destructive: Boolean = false,
) {
    TextButton(onClick = onClick, modifier = modifier, enabled = enabled) {
        Text(
            text = text,
            color = if (destructive) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            },
        )
    }
}
