package com.fitly.testutil

import com.fitly.domain.model.ClothingItem
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Condition
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.Season

fun testClothingItem(
    type: ClothingType = ClothingType.TOP,
    occasion: Occasion = Occasion.CASUAL,
    season: Season = Season.ALL_YEAR,
    condition: Condition = Condition.NEW,
) = ClothingItem(
    photoPath = "/photos/$type.jpg",
    dominantColor = 0xFFFF0000.toInt(),
    type = type,
    occasion = occasion,
    season = season,
    condition = condition,
    createdAt = 0L,
)
