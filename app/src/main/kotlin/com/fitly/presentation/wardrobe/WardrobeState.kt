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
) {
    // A val (not a get()), so filtering runs once per state update rather than once
    // per read of visibleItems.
    val visibleItems: List<ClothingItem> = items.filter { item ->
        (typeFilter == null || item.type == typeFilter) &&
            (occasionFilter == null || item.occasion == occasionFilter) &&
            (seasonFilter == null || item.season == seasonFilter)
    }
}
