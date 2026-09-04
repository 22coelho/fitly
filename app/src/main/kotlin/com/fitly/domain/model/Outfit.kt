package com.fitly.domain.model

enum class OutfitStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
}

/**
 * A combination of ClothingItem proposed by the Outfit Generator. Top, Bottom and Shoes are
 * required slots (a Dress fills Top and Bottom with the same ClothingItem id); Accessory is
 * optional. History is simply the set of Outfits with status != PENDING.
 */
data class Outfit(
    val id: Long = 0,
    val topItemId: Long,
    val bottomItemId: Long,
    val shoesItemId: Long,
    val accessoryItemId: Long?,
    val occasion: Occasion?,
    val status: OutfitStatus,
    val favorite: Boolean,
    val createdAt: Long,
)
