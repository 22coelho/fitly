package com.fitly.data.datasource

import com.fitly.data.database.ClothingItemEntity
import com.fitly.domain.model.ClothingItem

fun ClothingItemEntity.toClothingItem(): ClothingItem = ClothingItem(
    id = id,
    photoPath = photoPath,
    dominantColor = dominantColor,
    type = type,
    occasion = occasion,
    season = season,
    condition = condition,
    createdAt = createdAt,
)

fun ClothingItem.toEntity(): ClothingItemEntity = ClothingItemEntity(
    id = id,
    photoPath = photoPath,
    dominantColor = dominantColor,
    type = type,
    occasion = occasion,
    season = season,
    condition = condition,
    createdAt = createdAt,
)
