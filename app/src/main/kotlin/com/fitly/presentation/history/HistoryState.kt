package com.fitly.presentation.history

import com.fitly.domain.model.ResolvedOutfit

data class HistoryState(
    val outfits: List<ResolvedOutfit> = emptyList(),
    val favoritesOnly: Boolean = false,
) {
    // A val, not a get(): filtering runs once per state update rather than once per read.
    val visibleOutfits: List<ResolvedOutfit> =
        if (favoritesOnly) outfits.filter { it.favorite } else outfits
}
