package com.fitly.presentation.wardrobe.itemdetail

import com.fitly.domain.model.ClothingItem
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Condition
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.Season

enum class ItemDetailStatus {
    IDLE,
    SAVING,
    DELETING,
}

data class ItemDetailState(
    val itemId: Long = 0L,
    val createdAt: Long = 0L,
    val photoPath: String? = null,
    val dominantColor: Int? = null,
    val type: ClothingType? = null,
    val occasion: Occasion? = null,
    val season: Season? = null,
    val condition: Condition? = null,
    val isNotFound: Boolean = false,
    val isDeleteConfirmationVisible: Boolean = false,
    val status: ItemDetailStatus = ItemDetailStatus.IDLE,
) {
    val canSave: Boolean get() = status == ItemDetailStatus.IDLE && toClothingItemOrNull() != null

    /** Assembles the edited item once every field has loaded; null while any is missing. */
    fun toClothingItemOrNull(): ClothingItem? {
        val photoPath = photoPath ?: return null
        val dominantColor = dominantColor ?: return null
        val type = type ?: return null
        val occasion = occasion ?: return null
        val season = season ?: return null
        val condition = condition ?: return null
        return ClothingItem(
            id = itemId,
            photoPath = photoPath,
            dominantColor = dominantColor,
            type = type,
            occasion = occasion,
            season = season,
            condition = condition,
            createdAt = createdAt,
        )
    }
}
