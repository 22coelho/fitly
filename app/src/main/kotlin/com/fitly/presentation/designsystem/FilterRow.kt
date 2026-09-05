package com.fitly.presentation.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/**
 * A row of chips for picking a value out of [options]. When [showAllOption] is true, an extra
 * "all" chip is shown to clear the selection - only meaningful when [selected] is an optional
 * filter (Wardrobe, Home's occasion filter), never for a field a saved item requires.
 */
@Composable
fun <T> FilterRow(
    label: String,
    options: List<T>,
    selected: T?,
    optionLabel: (T) -> String,
    onSelected: (T?) -> Unit,
    showAllOption: Boolean = true,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
    ) {
        if (showAllOption) {
            item {
                FilterChip(
                    selected = selected == null,
                    onClick = { onSelected(null) },
                    label = { Text("$label: todos") },
                )
            }
        }
        items(options) { option ->
            FilterChip(
                selected = selected == option,
                onClick = { onSelected(option) },
                label = { Text(optionLabel(option)) },
            )
        }
    }
}
