package com.fitly.presentation.wardrobe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fitly.R
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Condition
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.Season
import com.fitly.presentation.designsystem.ClothingPhoto
import com.fitly.presentation.designsystem.FilterRow
import com.fitly.presentation.designsystem.FitlyLoadingIndicator
import com.fitly.presentation.designsystem.FitlySegmentedRow
import com.fitly.presentation.labelRes

/**
 * The four required tags and the photo. Shared by Add item and Item detail, which are the same
 * form over a blank item and an existing one.
 *
 * Tags with three values are segmented and the five-value ones are chip rows: a segmented control
 * reads as "pick exactly one of these", which is what a required field is, but five segments are
 * too narrow to read on a phone.
 */
@Composable
fun ClothingItemForm(
    photoPath: String?,
    dominantColor: Int?,
    type: ClothingType?,
    occasion: Occasion?,
    season: Season?,
    condition: Condition?,
    isProcessingPhoto: Boolean,
    onPickPhotoClick: (() -> Unit)?,
    onTypeSelected: (ClothingType) -> Unit,
    onOccasionSelected: (Occasion) -> Unit,
    onSeasonSelected: (Season) -> Unit,
    onConditionSelected: (Condition) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        // The photo is the button. A separate "choose photo" button spent a whole row on what
        // tapping the picture already offers.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .then(if (onPickPhotoClick != null) Modifier.clickable(onClick = onPickPhotoClick) else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            ClothingPhoto(
                photoPath = photoPath,
                dominantColor = dominantColor,
                modifier = Modifier.width(220.dp),
            )
            when {
                isProcessingPhoto -> FitlyLoadingIndicator()
                photoPath == null && onPickPhotoClick != null -> Text(
                    text = stringResource(R.string.action_pick_photo),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        FormField(label = stringResource(R.string.filter_type)) {
            FilterRow(
                allLabel = "",
                options = ClothingType.entries,
                selected = type,
                optionLabel = { it.labelRes },
                onSelected = { it?.let(onTypeSelected) },
                showAllOption = false,
            )
        }
        FormField(label = stringResource(R.string.filter_occasion)) {
            FilterRow(
                allLabel = "",
                options = Occasion.entries,
                selected = occasion,
                optionLabel = { it.labelRes },
                onSelected = { it?.let(onOccasionSelected) },
                showAllOption = false,
            )
        }
        FormField(label = stringResource(R.string.filter_season), horizontalPadding = 16.dp) {
            FitlySegmentedRow(
                options = Season.entries,
                selected = season,
                optionLabel = { it.labelRes },
                onSelected = onSeasonSelected,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        FormField(label = stringResource(R.string.filter_condition), horizontalPadding = 16.dp) {
            FitlySegmentedRow(
                options = Condition.entries,
                selected = condition,
                optionLabel = { it.labelRes },
                onSelected = onConditionSelected,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun FormField(
    label: String,
    horizontalPadding: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Box(modifier = Modifier.padding(horizontal = horizontalPadding)) { content() }
    }
}
