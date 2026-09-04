package com.fitly.data.datasource

import com.fitly.data.database.OutfitEntity
import com.fitly.domain.model.Outfit

fun OutfitEntity.toOutfit(): Outfit = Outfit(
    id = id,
    topItemId = topItemId,
    bottomItemId = bottomItemId,
    shoesItemId = shoesItemId,
    accessoryItemId = accessoryItemId,
    occasion = occasion,
    status = status,
    favorite = favorite,
    createdAt = createdAt,
)

fun Outfit.toEntity(): OutfitEntity = OutfitEntity(
    id = id,
    topItemId = topItemId,
    bottomItemId = bottomItemId,
    shoesItemId = shoesItemId,
    accessoryItemId = accessoryItemId,
    occasion = occasion,
    status = status,
    favorite = favorite,
    createdAt = createdAt,
)
