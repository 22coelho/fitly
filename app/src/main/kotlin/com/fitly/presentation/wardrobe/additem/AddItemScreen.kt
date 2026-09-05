package com.fitly.presentation.wardrobe.additem

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitly.R
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Condition
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.Season
import com.fitly.presentation.labelRes
import com.fitly.presentation.messageRes
import com.fitly.presentation.ObserveAsEvents
import com.fitly.presentation.designsystem.FitlyButton
import com.fitly.presentation.designsystem.FitlyScaffold
import com.fitly.presentation.designsystem.FitlyTheme
import com.fitly.presentation.designsystem.FitlyTopAppBar
import com.fitly.presentation.wardrobe.ClothingItemForm
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
            is AddItemEvent.ShowError -> scope.launch { snackbarHostState.showSnackbar(context.getString(event.error.messageRes)) }
        }
    }

    AddItemScreen(
        state = state,
        onAction = viewModel::onAction,
        onPickPhotoClick = {
            pickPhotoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onBackClick = onNavigateBack,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun AddItemScreen(
    state: AddItemState,
    onAction: (AddItemAction) -> Unit,
    onPickPhotoClick: () -> Unit,
    onBackClick: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    FitlyScaffold(
        topBar = {
            FitlyTopAppBar(
                title = stringResource(R.string.add_item_title),
                onBackClick = onBackClick,
            )
        },
        snackbarHostState = snackbarHostState,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp),
        ) {
            ClothingItemForm(
                photoPath = state.photoPath,
                dominantColor = state.dominantColor,
                type = state.type,
                occasion = state.occasion,
                season = state.season,
                condition = state.condition,
                isProcessingPhoto = state.isProcessingPhoto,
                onPickPhotoClick = onPickPhotoClick,
                onTypeSelected = { onAction(AddItemAction.OnTypeSelected(it)) },
                onOccasionSelected = { onAction(AddItemAction.OnOccasionSelected(it)) },
                onSeasonSelected = { onAction(AddItemAction.OnSeasonSelected(it)) },
                onConditionSelected = { onAction(AddItemAction.OnConditionSelected(it)) },
            )

            FitlyButton(
                text = stringResource(R.string.action_save),
                onClick = { onAction(AddItemAction.OnSaveClick) },
                enabled = state.canSave,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            )
        }
    }
}

@Preview
@Composable
private fun AddItemScreenPreview() {
    FitlyTheme {
        AddItemScreen(
            state = AddItemState(
                type = ClothingType.TOP,
                occasion = Occasion.CASUAL,
                season = Season.ALL_YEAR,
                condition = Condition.NEW,
            ),
            onAction = {},
            onPickPhotoClick = {},
            onBackClick = {},
        )
    }
}
