package com.fitly.presentation.wardrobe.additem

import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Condition
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.Season

sealed interface AddItemAction {
    class OnPhotoCaptured(val photoBytes: ByteArray) : AddItemAction
    data class OnTypeSelected(val type: ClothingType) : AddItemAction
    data class OnOccasionSelected(val occasion: Occasion) : AddItemAction
    data class OnSeasonSelected(val season: Season) : AddItemAction
    data class OnConditionSelected(val condition: Condition) : AddItemAction
    data object OnSaveClick : AddItemAction
}
