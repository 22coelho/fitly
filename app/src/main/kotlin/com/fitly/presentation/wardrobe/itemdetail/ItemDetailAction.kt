package com.fitly.presentation.wardrobe.itemdetail

import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Condition
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.Season

sealed interface ItemDetailAction {
    data class OnTypeChanged(val type: ClothingType) : ItemDetailAction
    data class OnOccasionChanged(val occasion: Occasion) : ItemDetailAction
    data class OnSeasonChanged(val season: Season) : ItemDetailAction
    data class OnConditionChanged(val condition: Condition) : ItemDetailAction
    data object OnSaveClick : ItemDetailAction
    data object OnDeleteClick : ItemDetailAction
    data object OnConfirmDeleteClick : ItemDetailAction
    data object OnCancelDeleteClick : ItemDetailAction
}
