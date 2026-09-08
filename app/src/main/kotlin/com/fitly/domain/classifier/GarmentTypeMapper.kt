package com.fitly.domain.classifier

import com.fitly.domain.model.ClothingType

class GarmentTypeMapper {

    fun map(labels: List<DetectedLabel>): ClothingType? {
        return labels
            .filter { it.confidence >= CONFIDENCE_THRESHOLD && LABEL_TO_TYPE.containsKey(it.text) }
            .maxByOrNull { it.confidence }
            ?.let { LABEL_TO_TYPE[it.text] }
    }

    private companion object {
        const val CONFIDENCE_THRESHOLD = 0.6f

        val LABEL_TO_TYPE: Map<String, ClothingType> = mapOf(
            "Dress" to ClothingType.DRESS,
            "Gown" to ClothingType.DRESS,
            "Jeans" to ClothingType.BOTTOM,
            "Shorts" to ClothingType.BOTTOM,
            "Leggings" to ClothingType.BOTTOM,
            "Tights" to ClothingType.BOTTOM,
            "Jacket" to ClothingType.TOP,
            "Blazer" to ClothingType.TOP,
            "Jersey" to ClothingType.TOP,
            "Shoe" to ClothingType.SHOES,
            "Sneakers" to ClothingType.SHOES,
            "Necklace" to ClothingType.ACCESSORY,
            "Bracelet" to ClothingType.ACCESSORY,
            "Tie" to ClothingType.ACCESSORY,
            "Scarf" to ClothingType.ACCESSORY,
            "Cap" to ClothingType.ACCESSORY,
            "Hat" to ClothingType.ACCESSORY,
            "Sunglasses" to ClothingType.ACCESSORY,
        )
    }
}
