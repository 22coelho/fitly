package com.fitly.presentation.wardrobe.additem

import com.fitly.domain.util.DataError

sealed interface AddItemEvent {
    data object ItemSaved : AddItemEvent
    data class ShowError(val error: DataError.Local) : AddItemEvent
}
