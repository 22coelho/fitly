package com.fitly.presentation.wardrobe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitly.domain.datasource.ClothingItemLocalDataSource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WardrobeViewModel(
    private val clothingItemLocalDataSource: ClothingItemLocalDataSource,
) : ViewModel() {

    private val _state = MutableStateFlow(WardrobeState())
    val state = _state.asStateFlow()

    private val _events = Channel<WardrobeEvent>()
    val events = _events.receiveAsFlow()

    init {
        clothingItemLocalDataSource.observeAll()
            .onEach { items -> _state.update { it.copy(items = items) } }
            // Without this, an exception from the Room query/mapping would propagate
            // uncaught and permanently kill this collection for the ViewModel's lifetime.
            .catch { }
            .launchIn(viewModelScope)
    }

    fun onAction(action: WardrobeAction) {
        when (action) {
            is WardrobeAction.OnTypeFilterSelected -> _state.update { it.copy(typeFilter = action.type) }
            is WardrobeAction.OnOccasionFilterSelected -> _state.update { it.copy(occasionFilter = action.occasion) }
            is WardrobeAction.OnSeasonFilterSelected -> _state.update { it.copy(seasonFilter = action.season) }
            WardrobeAction.OnFiltersClick -> _state.update { it.copy(filtersVisible = true) }
            WardrobeAction.OnFiltersDismiss -> _state.update { it.copy(filtersVisible = false) }
            WardrobeAction.OnClearFilters -> _state.update {
                it.copy(typeFilter = null, occasionFilter = null, seasonFilter = null)
            }
            is WardrobeAction.OnItemClick -> viewModelScope.launch {
                _events.send(WardrobeEvent.NavigateToItemDetail(action.id))
            }
        }
    }
}
