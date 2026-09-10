package com.fitly.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitly.domain.datasource.ClothingItemLocalDataSource
import com.fitly.domain.datasource.OutfitLocalDataSource
import com.fitly.domain.model.resolve
import com.fitly.domain.model.rewear
import com.fitly.domain.util.onFailure
import com.fitly.domain.util.onSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val clothingItemLocalDataSource: ClothingItemLocalDataSource,
    private val outfitLocalDataSource: OutfitLocalDataSource,
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state = _state.asStateFlow()

    private val _events = Channel<HistoryEvent>()
    val events = _events.receiveAsFlow()

    init {
        combine(
            clothingItemLocalDataSource.observeAll(),
            outfitLocalDataSource.observeHistory(),
        ) { items, outfits ->
            val itemsById = items.associateBy { it.id }
            outfits.mapNotNull { it.resolve(itemsById) }
        }
            .onEach { outfits -> _state.update { it.copy(outfits = outfits) } }
            .catch { }
            .launchIn(viewModelScope)
    }

    fun onAction(action: HistoryAction) {
        when (action) {
            is HistoryAction.OnFavoriteToggle -> toggleFavorite(action.outfitId)
            HistoryAction.OnFavoritesOnlyToggle -> _state.update { it.copy(favoritesOnly = !it.favoritesOnly) }
            is HistoryAction.OnRewearClick -> rewear(action.outfitId)
        }
    }

    private fun rewear(outfitId: Long) {
        if (_state.value.status != HistoryStatus.IDLE) return
        _state.update { it.copy(status = HistoryStatus.SAVING) }
        viewModelScope.launch {
            outfitLocalDataSource.getById(outfitId)
                .onSuccess { outfit ->
                    outfitLocalDataSource.upsert(outfit.rewear(System.currentTimeMillis()))
                        .onSuccess {
                            _state.update { it.copy(status = HistoryStatus.IDLE) }
                            _events.send(HistoryEvent.NavigateToHome)
                        }
                        .onFailure { error ->
                            _state.update { it.copy(status = HistoryStatus.IDLE) }
                            _events.send(HistoryEvent.ShowError(error))
                        }
                }
                .onFailure { error ->
                    _state.update { it.copy(status = HistoryStatus.IDLE) }
                    _events.send(HistoryEvent.ShowError(error))
                }
        }
    }

    private fun toggleFavorite(outfitId: Long) {
        val outfit = _state.value.outfits.find { it.outfitId == outfitId } ?: return
        viewModelScope.launch {
            outfitLocalDataSource.setFavorite(outfitId, !outfit.favorite)
                .onFailure { error -> _events.send(HistoryEvent.ShowError(error)) }
        }
    }
}
