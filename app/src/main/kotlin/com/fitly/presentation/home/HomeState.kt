package com.fitly.presentation.home

import com.fitly.domain.model.Occasion
import com.fitly.domain.model.ResolvedOutfit

enum class HomeStatus {
    IDLE,
    GENERATING,
    SAVING,
}

data class HomeState(
    val occasionFilter: Occasion? = null,
    val outfit: ResolvedOutfit? = null,
    val status: HomeStatus = HomeStatus.IDLE,
    val noItemsAvailable: Boolean = false,
)
