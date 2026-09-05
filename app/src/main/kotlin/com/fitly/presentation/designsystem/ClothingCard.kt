package com.fitly.presentation.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fitly.domain.model.ClothingItem
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Condition
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.Season

/**
 * One garment in the Wardrobe grid: the photo, and a thin bar of its extracted dominant colour.
 *
 * No type label. In a two-column grid the photo already says what the garment is, the Type filter
 * above says what is being shown, and a row of words under every cell is what made the old list
 * read as a database table.
 */
@Composable
fun ClothingCard(
    item: ClothingItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.medium
    Column(
        modifier = modifier
            .clip(shape)
            // Outlines the whole cell, colour bar included, rather than letting ClothingPhoto
            // outline only its own half of it.
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .clickable(onClick = onClick),
    ) {
        ClothingPhoto(
            photoPath = item.photoPath,
            dominantColor = item.dominantColor,
            fit = PhotoFit.Cover,
            shape = shape,
            outlined = false,
            modifier = Modifier.fillMaxWidth(),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color(item.dominantColor)),
        )
    }
}

@Preview
@Composable
private fun ClothingCardPreview() {
    FitlyTheme {
        ClothingCard(
            item = ClothingItem(
                photoPath = "",
                dominantColor = 0xFF6C8C5A.toInt(),
                type = ClothingType.TOP,
                occasion = Occasion.CASUAL,
                season = Season.SUMMER,
                condition = Condition.NEW,
                createdAt = 0L,
            ),
            onClick = {},
            modifier = Modifier.width(170.dp),
        )
    }
}
