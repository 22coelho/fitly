package com.fitly.presentation.designsystem

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

/**
 * A row of chips for picking a value out of [options]. When [showAllOption] is true, an extra
 * "all" chip is shown to clear the selection - only meaningful when [selected] is an optional
 * filter (Wardrobe, Home's occasion filter), never for a field a saved item requires.
 *
 * For a required field with three or four values, [FitlySegmentedRow] says "pick exactly one"
 * more clearly than a chip row can.
 */
@Composable
fun <T> FilterRow(
    allLabel: String,
    options: List<T>,
    selected: T?,
    @StringRes optionLabel: (T) -> Int,
    onSelected: (T?) -> Unit,
    modifier: Modifier = Modifier,
    showAllOption: Boolean = true,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
    ) {
        if (showAllOption) {
            item {
                FitlyChip(
                    text = allLabel,
                    selected = selected == null,
                    onClick = { onSelected(null) },
                )
            }
        }
        items(options) { option ->
            FitlyChip(
                text = stringResource(optionLabel(option)),
                selected = selected == option,
                onClick = { onSelected(option) },
            )
        }
    }
}
