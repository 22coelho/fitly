package com.fitly.presentation.wardrobe.itemdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import com.fitly.presentation.ObserveAsEvents
import com.fitly.presentation.designsystem.FitlyAlertDialog
import com.fitly.presentation.designsystem.FitlyButton
import com.fitly.presentation.designsystem.FitlyScaffold
import com.fitly.presentation.designsystem.FitlyTextButton
import com.fitly.presentation.designsystem.FitlyTheme
import com.fitly.presentation.designsystem.FitlyTopAppBar
import com.fitly.presentation.messageRes
import com.fitly.presentation.wardrobe.ClothingItemForm
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ItemDetailRoot(
    onNavigateBack: () -> Unit,
    viewModel: ItemDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ItemDetailEvent.Saved, ItemDetailEvent.Deleted -> onNavigateBack()
            is ItemDetailEvent.ShowError -> scope.launch {
                snackbarHostState.showSnackbar(context.getString(event.error.messageRes))
            }
        }
    }

    ItemDetailScreen(
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onNavigateBack,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun ItemDetailScreen(
    state: ItemDetailState,
    onAction: (ItemDetailAction) -> Unit,
    onBackClick: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    FitlyScaffold(
        topBar = {
            FitlyTopAppBar(
                title = stringResource(R.string.item_detail_title),
                onBackClick = onBackClick,
            )
        },
        snackbarHostState = snackbarHostState,
    ) { padding ->
        if (state.isNotFound) {
            Text(
                text = stringResource(R.string.item_not_found),
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            )
            return@FitlyScaffold
        }

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
                isProcessingPhoto = false,
                // No photo swap here: there is no Action for one. PRODUCT.md says this screen
                // allows it, which the code has never done.
                onPickPhotoClick = null,
                onTypeSelected = { onAction(ItemDetailAction.OnTypeChanged(it)) },
                onOccasionSelected = { onAction(ItemDetailAction.OnOccasionChanged(it)) },
                onSeasonSelected = { onAction(ItemDetailAction.OnSeasonChanged(it)) },
                onConditionSelected = { onAction(ItemDetailAction.OnConditionChanged(it)) },
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                FitlyButton(
                    text = stringResource(R.string.action_save),
                    onClick = { onAction(ItemDetailAction.OnSaveClick) },
                    enabled = state.canSave,
                    modifier = Modifier.fillMaxWidth(),
                )
                FitlyTextButton(
                    text = stringResource(R.string.action_delete),
                    onClick = { onAction(ItemDetailAction.OnDeleteClick) },
                    destructive = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        if (state.isDeleteConfirmationVisible) {
            FitlyAlertDialog(
                title = stringResource(R.string.delete_title),
                text = stringResource(R.string.delete_body),
                confirmText = stringResource(R.string.action_delete),
                dismissText = stringResource(R.string.action_cancel),
                onConfirm = { onAction(ItemDetailAction.OnConfirmDeleteClick) },
                onDismiss = { onAction(ItemDetailAction.OnCancelDeleteClick) },
                destructive = true,
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
                dominantColor = 0xFF6C8C5A.toInt(),
                type = ClothingType.TOP,
                occasion = Occasion.CASUAL,
                season = Season.ALL_YEAR,
                condition = Condition.NEW,
            ),
            onAction = {},
        )
    }
}
