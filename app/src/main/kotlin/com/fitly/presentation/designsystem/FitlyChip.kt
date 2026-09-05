package com.fitly.presentation.designsystem

import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** A chip that turns a filter or a form field's value on. Use [TagChip] for a value you only show. */
@Composable
fun FitlyChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(selected = selected, onClick = onClick, label = { Text(text) }, modifier = modifier)
}
