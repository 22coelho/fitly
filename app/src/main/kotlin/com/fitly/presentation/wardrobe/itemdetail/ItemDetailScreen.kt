package com.fitly.presentation.wardrobe.itemdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Condition
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.Season
import com.fitly.presentation.ObserveAsEvents
import com.fitly.presentation.designsystem.ClothingPhoto
import com.fitly.presentation.designsystem.FilterRow
import com.fitly.presentation.designsystem.FitlyTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ItemDetailRoot(
    onNavigateBack: () -> Unit,
    viewModel: ItemDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ItemDetailEvent.Saved, ItemDetailEvent.Deleted -> onNavigateBack()
            is ItemDetailEvent.ShowError -> scope.launch {
                snackbarHostState.showSnackbar(event.error.name)
            }
        }
    }

    ItemDetailScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun ItemDetailScreen(
    state: ItemDetailState,
    onAction: (ItemDetailAction) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        if (state.isNotFound) {
            Text(
                text = "Peça não encontrada.",
                modifier = Modifier.fillMaxSize().padding(padding),
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ClothingPhoto(photoPath = state.photoPath, modifier = Modifier.fillMaxWidth().height(200.dp))

            FilterRow(
                label = "Tipo",
                options = ClothingType.entries,
                selected = state.type,
                optionLabel = { it.name },
                onSelected = { it?.let { onAction(ItemDetailAction.OnTypeChanged(it)) } },
                showAllOption = false,
            )
            FilterRow(
                label = "Ocasião",
                options = Occasion.entries,
                selected = state.occasion,
                optionLabel = { it.name },
                onSelected = { it?.let { onAction(ItemDetailAction.OnOccasionChanged(it)) } },
                showAllOption = false,
            )
            FilterRow(
                label = "Estação",
                options = Season.entries,
                selected = state.season,
                optionLabel = { it.name },
                onSelected = { it?.let { onAction(ItemDetailAction.OnSeasonChanged(it)) } },
                showAllOption = false,
            )
            FilterRow(
                label = "Condição",
                options = Condition.entries,
                selected = state.condition,
                optionLabel = { it.name },
                onSelected = { it?.let { onAction(ItemDetailAction.OnConditionChanged(it)) } },
                showAllOption = false,
            )

            Button(
                onClick = { onAction(ItemDetailAction.OnSaveClick) },
                enabled = state.canSave,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Guardar")
            }
            OutlinedButton(
                onClick = { onAction(ItemDetailAction.OnDeleteClick) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Apagar")
            }
        }

        if (state.isDeleteConfirmationVisible) {
            AlertDialog(
                onDismissRequest = { onAction(ItemDetailAction.OnCancelDeleteClick) },
                title = { Text("Apagar peça?") },
                text = { Text("Esta ação não pode ser desfeita.") },
                confirmButton = {
                    Button(onClick = { onAction(ItemDetailAction.OnConfirmDeleteClick) }) {
                        Text("Apagar")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { onAction(ItemDetailAction.OnCancelDeleteClick) }) {
                        Text("Cancelar")
                    }
                },
            )
        }
    }
}

@Preview
@Composable
private fun ItemDetailScreenPreview() {
    FitlyTheme {
        ItemDetailScreen(
            state = ItemDetailState(
                type = ClothingType.TOP,
                occasion = Occasion.CASUAL,
                season = Season.ALL_YEAR,
                condition = Condition.NEW,
            ),
            onAction = {},
        )
    }
}
