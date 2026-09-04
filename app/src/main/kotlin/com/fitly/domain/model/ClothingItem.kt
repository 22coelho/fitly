package com.fitly.domain.model

/**
 * A single tagged piece of clothing in the Wardrobe. Color is extracted automatically from the
 * photo (dominant color, no ML); type, occasion, season and condition are chosen manually.
 */
data class ClothingItem(
    val id: Long = 0,
    val photoPath: String,
    val dominantColor: Int,
    val type: ClothingType,
    val occasion: Occasion,
    val season: Season,
    val condition: Condition,
    val createdAt: Long,
)
