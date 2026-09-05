package com.fitly.presentation.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fitly.domain.model.ClothingItem
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Condition
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.OutfitStatus
import com.fitly.domain.model.ResolvedOutfit
import com.fitly.domain.model.Season

/**
 * A whole outfit, read top to bottom the way it is worn: top, bottom, shoes.
 *
 * The accessory sits in a separate, smaller row underneath rather than as a fourth item in the
 * stack. That is the layout saying what the model says - three slots are required and one is not.
 *
 * A DRESS fills the top and bottom slots with the same ClothingItem, so it is drawn once.
 */
@Composable
fun OutfitPreview(
    outfit: ResolvedOutfit,
    modifier: Modifier = Modifier,
) {
    val stacked = listOfNotNull(
        outfit.top,
        outfit.bottom.takeIf { it.id != outfit.top.id },
        outfit.shoes,
    )
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        stacked.forEach { item ->
            ClothingPhoto(
                photoPath = item.photoPath,
                dominantColor = item.dominantColor,
                aspectRatio = 4f / 3f,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        outfit.accessory?.let { accessory ->
            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ClothingPhoto(
                    photoPath = accessory.photoPath,
                    dominantColor = accessory.dominantColor,
                    aspectRatio = 1f,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.width(64.dp),
                )
                Text(
                    text = "com acessório",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview
@Composable
private fun OutfitPreviewPreview() {
    val item = ClothingItem(
        id = 1,
        photoPath = "",
        dominantColor = 0xFF6C8C5A.toInt(),
        type = ClothingType.TOP,
        occasion = Occasion.CASUAL,
        season = Season.SUMMER,
        condition = Condition.NEW,
        createdAt = 0L,
    )
    FitlyTheme {
        OutfitPreview(
            outfit = ResolvedOutfit(
                outfitId = 1L,
                top = item,
                bottom = item.copy(id = 2, dominantColor = 0xFF3B4A6B.toInt()),
                shoes = item.copy(id = 3, dominantColor = 0xFF8B5E3C.toInt()),
                accessory = item.copy(id = 4, dominantColor = 0xFFB4522F.toInt()),
                status = OutfitStatus.PENDING,
                favorite = false,
            ),
            modifier = Modifier.width(280.dp),
        )
    }
}
