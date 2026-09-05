package com.fitly.domain.generator

import com.fitly.domain.model.ClothingItem
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.Outfit
import com.fitly.domain.model.OutfitStatus
import kotlin.random.Random

class OutfitGenerator {

    /** Randomly assembles a valid Outfit from [items], optionally narrowed to [occasion].
     * Null when there's no way to fill every required slot (Top+Bottom or Dress, and Shoes). */
    fun generate(items: List<ClothingItem>, occasion: Occasion? = null): Outfit? {
        val eligible = items.filter { occasion == null || it.occasion == occasion }

        val dresses = eligible.filter { it.type == ClothingType.DRESS }
        val tops = eligible.filter { it.type == ClothingType.TOP }
        val bottoms = eligible.filter { it.type == ClothingType.BOTTOM }
        val shoes = eligible.filter { it.type == ClothingType.SHOES }
        val accessories = eligible.filter { it.type == ClothingType.ACCESSORY }

        val canUseDress = dresses.isNotEmpty()
        val canUseSeparate = tops.isNotEmpty() && bottoms.isNotEmpty()
        if (!canUseDress && !canUseSeparate) return null
        if (shoes.isEmpty()) return null

        val useDress = if (canUseDress && canUseSeparate) Random.nextBoolean() else canUseDress

        val (topId, bottomId) = if (useDress) {
            val dressId = dresses.random().id
            dressId to dressId
        } else {
            tops.random().id to bottoms.random().id
        }

        return Outfit(
            topItemId = topId,
            bottomItemId = bottomId,
            shoesItemId = shoes.random().id,
            accessoryItemId = accessories.randomOrNull()?.id,
            occasion = occasion,
            status = OutfitStatus.PENDING,
            favorite = false,
            createdAt = System.currentTimeMillis(),
        )
    }
}
