package com.fitly.presentation.preview

import com.fitly.domain.model.ClothingItem
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Condition
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.OutfitStatus
import com.fitly.domain.model.ResolvedOutfit
import com.fitly.domain.model.Season

/*
 * Sample data for @Preview only. It lives in main rather than test because previews compile
 * against the main source set - the photos are absent, so previews show each garment as its
 * dominant colour, which is exactly what the real screen falls back to.
 */

internal fun previewClothingItems(): List<ClothingItem> = listOf(
    previewItem(1, 0xFF6C8C5A, ClothingType.TOP, Occasion.CASUAL, Season.SUMMER),
    previewItem(2, 0xFF3B4A6B, ClothingType.BOTTOM, Occasion.WORK, Season.ALL_YEAR),
    previewItem(3, 0xFF8B5E3C, ClothingType.SHOES, Occasion.CASUAL, Season.ALL_YEAR),
    previewItem(4, 0xFFB4522F, ClothingType.TOP, Occasion.DATE, Season.WINTER),
    previewItem(5, 0xFFD8CFC0, ClothingType.DRESS, Occasion.FORMAL, Season.SUMMER),
    previewItem(6, 0xFF2E2A28, ClothingType.ACCESSORY, Occasion.WORK, Season.ALL_YEAR),
)

internal fun previewOutfit(
    favorite: Boolean = false,
    withAccessory: Boolean = true,
    status: OutfitStatus = OutfitStatus.PENDING,
): ResolvedOutfit {
    val items = previewClothingItems()
    return ResolvedOutfit(
        outfitId = 1L,
        top = items[0],
        bottom = items[1],
        shoes = items[2],
        accessory = items[5].takeIf { withAccessory },
        status = status,
        favorite = favorite,
        createdAt = 1_700_000_000_000L,
    )
}

private fun previewItem(
    id: Long,
    color: Long,
    type: ClothingType,
    occasion: Occasion,
    season: Season,
) = ClothingItem(
    id = id,
    photoPath = "",
    dominantColor = color.toInt(),
    type = type,
    occasion = occasion,
    season = season,
    condition = Condition.GOOD,
    createdAt = id,
)
