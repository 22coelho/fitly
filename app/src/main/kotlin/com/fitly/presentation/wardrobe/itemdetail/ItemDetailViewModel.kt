package com.fitly.presentation.wardrobe.itemdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitly.domain.datasource.ClothingItemLocalDataSource
import com.fitly.domain.util.onFailure
import com.fitly.domain.util.onSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val clothingItemLocalDataSource: ClothingItemLocalDataSource,
) : ViewModel() {

    // Real navigation always supplies this (a typed nav route guarantees it at compile
    // time); a missing value means the ViewModel was wired up wrong, so fail loudly
    // rather than silently falling back to id 0 (ClothingItem's "not yet saved" sentinel).
    private val itemId: Long = savedStateHandle.get<Long>("itemId")
        ?: error("ItemDetailViewModel requires an \"itemId\" argument")

    private val _state = MutableStateFlow(ItemDetailState(itemId = itemId))
    val state = _state.asStateFlow()

    private val _events = Channel<ItemDetailEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            clothingItemLocalDataSource.getById(itemId)
                .onSuccess { item ->
                    _state.update {
                        it.copy(
                            createdAt = item.createdAt,
                            photoPath = item.photoPath,
                            dominantColor = item.dominantColor,
                            type = item.type,
                            occasion = item.occasion,
                            season = item.season,
                            condition = item.condition,
                        )
                    }
                }
                .onFailure {
                    _state.update { it.copy(isNotFound = true) }
                }
        }
    }

    fun onAction(action: ItemDetailAction) {
        when (action) {
            is ItemDetailAction.OnTypeChanged -> _state.update { it.copy(type = action.type) }
            is ItemDetailAction.OnOccasionChanged -> _state.update { it.copy(occasion = action.occasion) }
            is ItemDetailAction.OnSeasonChanged -> _state.update { it.copy(season = action.season) }
            is ItemDetailAction.OnConditionChanged -> _state.update { it.copy(condition = action.condition) }
            ItemDetailAction.OnSaveClick -> save()
            ItemDetailAction.OnDeleteClick -> _state.update { it.copy(isDeleteConfirmationVisible = true) }
            ItemDetailAction.OnCancelDeleteClick -> _state.update { it.copy(isDeleteConfirmationVisible = false) }
            ItemDetailAction.OnConfirmDeleteClick -> delete()
        }
    }

    private fun save() {
        val current = _state.value
        if (current.status != ItemDetailStatus.IDLE) return
        val item = current.toClothingItemOrNull() ?: return
        // Set synchronously (not inside the launched coroutine) so a second, immediate
        // OnSaveClick sees SAVING and bails out instead of racing this one.
        _state.update { it.copy(status = ItemDetailStatus.SAVING) }
        viewModelScope.launch {
            clothingItemLocalDataSource.upsert(item)
                .onSuccess {
                    _state.update { it.copy(status = ItemDetailStatus.IDLE) }
                    _events.send(ItemDetailEvent.Saved)
                }
                .onFailure { error ->
                    _state.update { it.copy(status = ItemDetailStatus.IDLE) }
                    _events.send(ItemDetailEvent.ShowError(error))
                }
        }
    }

    private fun delete() {
        if (_state.value.status != ItemDetailStatus.IDLE) return
        _state.update { it.copy(status = ItemDetailStatus.DELETING) }
        viewModelScope.launch {
            clothingItemLocalDataSource.delete(itemId)
                .onSuccess {
                    _state.update { it.copy(status = ItemDetailStatus.IDLE, isDeleteConfirmationVisible = false) }
                    _events.send(ItemDetailEvent.Deleted)
                }
                .onFailure { error ->
                    _state.update { it.copy(status = ItemDetailStatus.IDLE, isDeleteConfirmationVisible = false) }
                    _events.send(ItemDetailEvent.ShowError(error))
                }
        }
    }
}
