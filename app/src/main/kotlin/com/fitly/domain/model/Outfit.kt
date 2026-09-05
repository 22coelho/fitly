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

/** An [Outfit] with its slot ids resolved to their [ClothingItem]s, for display. */
data class ResolvedOutfit(
    val outfitId: Long,
    val top: ClothingItem,
    val bottom: ClothingItem,
    val shoes: ClothingItem,
    val accessory: ClothingItem?,
    val status: OutfitStatus,
    val favorite: Boolean,
)

/** Null when a required slot's [ClothingItem] can no longer be found in [itemsById]. */
fun Outfit.resolve(itemsById: Map<Long, ClothingItem>): ResolvedOutfit? {
    val top = itemsById[topItemId] ?: return null
    val bottom = itemsById[bottomItemId] ?: return null
    val shoes = itemsById[shoesItemId] ?: return null
    val accessory = accessoryItemId?.let { itemsById[it] }
    return ResolvedOutfit(id, top, bottom, shoes, accessory, status, favorite)
}
