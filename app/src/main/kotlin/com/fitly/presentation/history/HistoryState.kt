package com.fitly.presentation.history

import com.fitly.domain.model.ResolvedOutfit

data class HistoryState(
    val outfits: List<ResolvedOutfit> = emptyList(),
)
