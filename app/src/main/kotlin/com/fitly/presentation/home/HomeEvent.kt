package com.fitly.presentation.home

import com.fitly.domain.util.DataError

sealed interface HomeEvent {
    data class ShowError(val error: DataError.Local) : HomeEvent
}
