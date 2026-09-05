package com.fitly.presentation.wardrobe.additem

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddItemRoot(
    onNavigateBack: () -> Unit,
    viewModel: AddItemViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val pickPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val bytes = try {
                    withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    null
                }
                if (bytes != null) {
                    viewModel.onAction(AddItemAction.OnPhotoCaptured(bytes))
                } else {
                    snackbarHostState.showSnackbar("Não foi possível ler a foto escolhida.")
                }
            }
        }
    }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            AddItemEvent.ItemSaved -> onNavigateBack()
            is AddItemEvent.ShowError -> scope.launch { snackbarHostState.showSnackbar(event.error.name) }
        }
    }

    AddItemScreen(
        state = state,
        onAction = viewModel::onAction,
        onPickPhotoClick = {
            pickPhotoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun AddItemScreen(
    state: AddItemState,
    onAction: (AddItemAction) -> Unit,
    onPickPhotoClick: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ClothingPhoto(photoPath = state.photoPath, modifier = Modifier.fillMaxWidth().height(200.dp))
            if (state.isProcessingPhoto) {
                CircularProgressIndicator()
            }
            OutlinedButton(onClick = onPickPhotoClick, modifier = Modifier.fillMaxWidth()) {
                Text(if (state.photoPath == null) "Escolher foto" else "Trocar foto")
            }

            FilterRow(
                label = "Tipo",
                options = ClothingType.entries,
                selected = state.type,
                optionLabel = { it.name },
                onSelected = { it?.let { onAction(AddItemAction.OnTypeSelected(it)) } },
                showAllOption = false,
            )
            FilterRow(
                label = "Ocasião",
                options = Occasion.entries,
                selected = state.occasion,
                optionLabel = { it.name },
                onSelected = { it?.let { onAction(AddItemAction.OnOccasionSelected(it)) } },
                showAllOption = false,
            )
            FilterRow(
                label = "Estação",
                options = Season.entries,
                selected = state.season,
                optionLabel = { it.name },
                onSelected = { it?.let { onAction(AddItemAction.OnSeasonSelected(it)) } },
                showAllOption = false,
            )
            FilterRow(
                label = "Condição",
                options = Condition.entries,
                selected = state.condition,
                optionLabel = { it.name },
                onSelected = { it?.let { onAction(AddItemAction.OnConditionSelected(it)) } },
                showAllOption = false,
            )

            Button(
                onClick = { onAction(AddItemAction.OnSaveClick) },
                enabled = state.canSave,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Guardar")
            }
        }
    }
}

@Preview
@Composable
private fun AddItemScreenPreview() {
    FitlyTheme {
        AddItemScreen(state = AddItemState(), onAction = {}, onPickPhotoClick = {})
    }
}
