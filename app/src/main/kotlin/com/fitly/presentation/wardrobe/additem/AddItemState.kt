package com.fitly.presentation.wardrobe.additem

import com.fitly.domain.model.ClothingItem
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Condition
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.Season

enum class AddItemStatus {
    IDLE,
    PROCESSING_PHOTO,
    SAVING,
}

data class AddItemState(
    val photoPath: String? = null,
    val dominantColor: Int? = null,
    val type: ClothingType? = null,
    val occasion: Occasion? = null,
    val season: Season? = null,
    val condition: Condition? = null,
    val status: AddItemStatus = AddItemStatus.IDLE,
) {
    val isProcessingPhoto: Boolean get() = status == AddItemStatus.PROCESSING_PHOTO
    val isSaving: Boolean get() = status == AddItemStatus.SAVING

    val canSave: Boolean get() = status == AddItemStatus.IDLE && toClothingItemOrNull() != null

    /** Assembles the item once every required field is chosen; null while any is missing. */
    fun toClothingItemOrNull(): ClothingItem? {
        val photoPath = photoPath ?: return null
        val dominantColor = dominantColor ?: return null
        val type = type ?: return null
        val occasion = occasion ?: return null
        val season = season ?: return null
        val condition = condition ?: return null
        return ClothingItem(
            photoPath = photoPath,
            dominantColor = dominantColor,
            type = type,
            occasion = occasion,
            season = season,
            condition = condition,
            createdAt = System.currentTimeMillis(),
        )
    }
}
