package com.fitly.presentation.home

import com.fitly.domain.model.Occasion

sealed interface HomeAction {
    data class OnOccasionFilterSelected(val occasion: Occasion?) : HomeAction
    data object OnGenerateClick : HomeAction
    data object OnAcceptClick : HomeAction
    data object OnRejectClick : HomeAction
    data object OnFavoriteToggle : HomeAction
}
