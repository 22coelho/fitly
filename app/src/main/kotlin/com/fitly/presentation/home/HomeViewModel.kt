package com.fitly.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitly.domain.datasource.ClothingItemLocalDataSource
import com.fitly.domain.datasource.OutfitLocalDataSource
import com.fitly.domain.generator.OutfitGenerator
import com.fitly.domain.model.ClothingItem
import com.fitly.domain.model.OutfitStatus
import com.fitly.domain.model.resolve
import com.fitly.domain.util.DataError
import com.fitly.domain.util.onFailure
import com.fitly.domain.util.onSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val clothingItemLocalDataSource: ClothingItemLocalDataSource,
    private val outfitLocalDataSource: OutfitLocalDataSource,
    private val outfitGenerator: OutfitGenerator,
) : ViewModel() {

    private var wardrobeItems: List<ClothingItem> = emptyList()

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _events = Channel<HomeEvent>()
    val events = _events.receiveAsFlow()

    init {
        clothingItemLocalDataSource.observeAll()
            .onEach { items -> wardrobeItems = items }
            .catch { }
            .launchIn(viewModelScope)
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnOccasionFilterSelected -> _state.update { it.copy(occasionFilter = action.occasion) }
            HomeAction.OnGenerateClick -> generate()
            HomeAction.OnAcceptClick -> setStatus(OutfitStatus.ACCEPTED)
            HomeAction.OnRejectClick -> setStatus(OutfitStatus.REJECTED)
            HomeAction.OnFavoriteToggle -> toggleFavorite()
        }
    }

    private fun generate() {
        val current = _state.value
        if (current.status != HomeStatus.IDLE) return
        val generated = outfitGenerator.generate(wardrobeItems, current.occasionFilter)
        if (generated == null) {
            _state.update { it.copy(noItemsAvailable = true) }
            return
        }
        _state.update { it.copy(status = HomeStatus.GENERATING, noItemsAvailable = false) }
        viewModelScope.launch {
            outfitLocalDataSource.upsert(generated)
                .onSuccess { id ->
                    val resolved = generated.copy(id = id).resolve(wardrobeItems.associateBy { it.id })
                    _state.update { it.copy(status = HomeStatus.IDLE, outfit = resolved) }
                    if (resolved == null) _events.send(HomeEvent.ShowError(DataError.Local.NOT_FOUND))
                }
                .onFailure { error ->
                    _state.update { it.copy(status = HomeStatus.IDLE) }
                    _events.send(HomeEvent.ShowError(error))
                }
        }
    }

    private fun setStatus(status: OutfitStatus) {
        val current = _state.value
        val outfitId = current.outfit?.outfitId ?: return
        if (current.status != HomeStatus.IDLE) return
        _state.update { it.copy(status = HomeStatus.SAVING) }
        viewModelScope.launch {
            outfitLocalDataSource.setStatus(outfitId, status)
                .onSuccess {
                    _state.update { it.copy(status = HomeStatus.IDLE, outfit = null) }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(status = HomeStatus.IDLE, outfit = if (error == DataError.Local.NOT_FOUND) null else it.outfit)
                    }
                    _events.send(HomeEvent.ShowError(error))
                }
        }
    }

    private fun toggleFavorite() {
        val current = _state.value
        val outfit = current.outfit ?: return
        if (current.status != HomeStatus.IDLE) return
        _state.update { it.copy(status = HomeStatus.SAVING) }
        viewModelScope.launch {
            outfitLocalDataSource.setFavorite(outfit.outfitId, !outfit.favorite)
                .onSuccess {
                    _state.update {
                        it.copy(status = HomeStatus.IDLE, outfit = outfit.copy(favorite = !outfit.favorite))
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(status = HomeStatus.IDLE, outfit = if (error == DataError.Local.NOT_FOUND) null else it.outfit)
                    }
                    _events.send(HomeEvent.ShowError(error))
                }
        }
    }
}
