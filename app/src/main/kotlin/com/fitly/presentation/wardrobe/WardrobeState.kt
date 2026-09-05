package com.fitly.presentation.wardrobe

import com.fitly.domain.model.ClothingItem
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.Season

data class WardrobeState(
    val items: List<ClothingItem> = emptyList(),
    val typeFilter: ClothingType? = null,
    val occasionFilter: Occasion? = null,
    val seasonFilter: Season? = null,
    val filtersVisible: Boolean = false,
) {
    /**
     * How many of the sheet's own filters are on, for the badge on the button that opens it.
     * Type is excluded on purpose: it has its own chips on the screen, so counting it here would
     * report a filter the user can already see.
     */
    val sheetFilterCount: Int = listOfNotNull(occasionFilter, seasonFilter).size

    // A val (not a get()), so filtering runs once per state update rather than once
    // per read of visibleItems.
    val visibleItems: List<ClothingItem> = items.filter { item ->
        (typeFilter == null || item.type == typeFilter) &&
            (occasionFilter == null || item.occasion == occasionFilter) &&
            (seasonFilter == null || item.season == seasonFilter)
    }
}
