package com.fitly.presentation.wardrobe.additem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitly.domain.datasource.ClothingItemLocalDataSource
import com.fitly.domain.datasource.DominantColorExtractor
import com.fitly.domain.datasource.PhotoLocalDataSource
import com.fitly.domain.util.onFailure
import com.fitly.domain.util.onSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddItemViewModel(
    private val colorExtractor: DominantColorExtractor,
    private val photoLocalDataSource: PhotoLocalDataSource,
    private val clothingItemLocalDataSource: ClothingItemLocalDataSource,
) : ViewModel() {

    private val _state = MutableStateFlow(AddItemState())
    val state = _state.asStateFlow()

    private val _events = Channel<AddItemEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: AddItemAction) {
        when (action) {
            is AddItemAction.OnPhotoCaptured -> processPhoto(action.photoBytes)
            is AddItemAction.OnTypeSelected -> _state.update { it.copy(type = action.type) }
            is AddItemAction.OnOccasionSelected -> _state.update { it.copy(occasion = action.occasion) }
            is AddItemAction.OnSeasonSelected -> _state.update { it.copy(season = action.season) }
            is AddItemAction.OnConditionSelected -> _state.update { it.copy(condition = action.condition) }
            AddItemAction.OnSaveClick -> save()
        }
    }

    private fun processPhoto(photoBytes: ByteArray) {
        if (_state.value.status != AddItemStatus.IDLE) return
        _state.update { it.copy(status = AddItemStatus.PROCESSING_PHOTO) }
        viewModelScope.launch {
            val dominantColor = colorExtractor.extract(photoBytes)
            photoLocalDataSource.save(photoBytes)
                .onSuccess { path ->
                    _state.update { it.copy(photoPath = path, dominantColor = dominantColor, status = AddItemStatus.IDLE) }
                }
                .onFailure { error ->
                    _state.update { it.copy(status = AddItemStatus.IDLE) }
                    _events.send(AddItemEvent.ShowError(error))
                }
        }
    }

    private fun save() {
        val current = _state.value
        val item = current.toClothingItemOrNull() ?: return
        if (current.status != AddItemStatus.IDLE) return
        // Set synchronously (not inside the launched coroutine) so a second, immediate
        // OnSaveClick sees SAVING and bails out instead of racing this one.
        _state.update { it.copy(status = AddItemStatus.SAVING) }
        viewModelScope.launch {
            clothingItemLocalDataSource.upsert(item)
                .onSuccess {
                    _state.update { it.copy(status = AddItemStatus.IDLE) }
                    _events.send(AddItemEvent.ItemSaved)
                }
                .onFailure { error ->
                    _state.update { it.copy(status = AddItemStatus.IDLE) }
                    _events.send(AddItemEvent.ShowError(error))
                }
        }
    }
}
