package com.fitly.presentation.wardrobe

sealed interface WardrobeEvent {
    data class NavigateToItemDetail(val id: Long) : WardrobeEvent
}
