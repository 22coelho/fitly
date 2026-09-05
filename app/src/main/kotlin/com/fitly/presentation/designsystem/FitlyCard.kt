package com.fitly.presentation.designsystem

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A surface that groups content. Takes an optional [onClick] rather than exposing two composables,
 * because the only difference between a card and a clickable card is whether it reacts.
 */
@Composable
fun FitlyCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = CardDefaults.cardColors()
    if (onClick == null) {
        Card(modifier = modifier, colors = colors, content = content)
    } else {
        Card(onClick = onClick, modifier = modifier, colors = colors, content = content)
    }
}
