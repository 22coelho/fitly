package com.fitly.presentation.history

import com.fitly.domain.util.DataError

sealed interface HistoryEvent {
    data class ShowError(val error: DataError.Local) : HistoryEvent
}
