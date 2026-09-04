package com.fitly.domain.model

/** The kind of a ClothingItem. Determines which OutfitSlot(s) it can fill. */
enum class ClothingType {
    TOP,
    BOTTOM,
    SHOES,
    DRESS,
    ACCESSORY,
}

/** Dress fills both TOP and BOTTOM at once; every other type fills exactly one slot. */
fun ClothingType.fillsSlots(): Set<OutfitSlot> = when (this) {
    ClothingType.TOP -> setOf(OutfitSlot.TOP)
    ClothingType.BOTTOM -> setOf(OutfitSlot.BOTTOM)
    ClothingType.SHOES -> setOf(OutfitSlot.SHOES)
    ClothingType.DRESS -> setOf(OutfitSlot.TOP, OutfitSlot.BOTTOM)
    ClothingType.ACCESSORY -> setOf(OutfitSlot.ACCESSORY)
}
