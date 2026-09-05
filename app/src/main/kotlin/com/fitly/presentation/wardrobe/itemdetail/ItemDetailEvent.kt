package com.fitly.presentation.wardrobe.itemdetail

import com.fitly.domain.util.DataError

sealed interface ItemDetailEvent {
    data object Saved : ItemDetailEvent
    data object Deleted : ItemDetailEvent
    data class ShowError(val error: DataError.Local) : ItemDetailEvent
}
