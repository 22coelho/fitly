package com.fitly.presentation.designsystem

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

/**
 * A required single-choice field, for the tags with only three values (Season, Condition). Being
 * segmented rather than a row of chips is the whole point: it reads as "pick exactly one of these",
 * which a chip row - where picking nothing looks equally valid - does not.
 *
 * Stays at three or four options. Beyond that the segments get too narrow to read and a
 * [FilterRow] is the better shape.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> FitlySegmentedRow(
    options: List<T>,
    selected: T?,
    optionLabel: (T) -> Int,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = selected == option,
                onClick = { onSelected(option) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
            ) {
                Text(stringResource(optionLabel(option)))
            }
        }
    }
}
