package com.fitly.presentation.wardrobe

import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.Season

sealed interface WardrobeAction {
    data class OnTypeFilterSelected(val type: ClothingType?) : WardrobeAction
    data class OnOccasionFilterSelected(val occasion: Occasion?) : WardrobeAction
    data class OnSeasonFilterSelected(val season: Season?) : WardrobeAction
    data object OnClearFilters : WardrobeAction
    data class OnItemClick(val id: Long) : WardrobeAction
}
