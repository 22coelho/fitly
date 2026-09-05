package com.fitly.presentation.history

sealed interface HistoryAction {
    data class OnFavoriteToggle(val outfitId: Long) : HistoryAction
    data object OnFavoritesOnlyToggle : HistoryAction
}
