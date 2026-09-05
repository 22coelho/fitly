package com.fitly.presentation.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitly.domain.model.ClothingItem
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Condition
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.OutfitStatus
import com.fitly.domain.model.ResolvedOutfit
import com.fitly.domain.model.Season
import com.fitly.presentation.labelRes
import com.fitly.presentation.messageRes
import com.fitly.presentation.ObserveAsEvents
import com.fitly.presentation.designsystem.ClothingPhoto
import com.fitly.presentation.designsystem.FitlyTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryRoot(viewModel: HistoryViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is HistoryEvent.ShowError -> scope.launch { snackbarHostState.showSnackbar(context.getString(event.error.messageRes)) }
        }
    }

    HistoryScreen(state = state, onAction = viewModel::onAction, snackbarHostState = snackbarHostState)
}

@Composable
fun HistoryScreen(
    state: HistoryState,
    onAction: (HistoryAction) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        // Insets belong to the host Scaffold in MainActivity; adding them here doubles them.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        if (state.outfits.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Ainda não há outfits no histórico.")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            items(state.outfits, key = { it.outfitId }) { outfit ->
                HistoryOutfitRow(outfit = outfit, onAction = onAction)
            }
        }
    }
}

@Composable
private fun HistoryOutfitRow(outfit: ResolvedOutfit, onAction: (HistoryAction) -> Unit) {
    ListItem(
        modifier = Modifier.fillMaxWidth(),
        leadingContent = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOfNotNull(outfit.top, outfit.bottom, outfit.shoes, outfit.accessory).forEach { item ->
                    ClothingPhoto(photoPath = item.photoPath, modifier = Modifier.size(48.dp))
                }
            }
        },
        headlineContent = { Text(stringResource(outfit.status.labelRes)) },
        trailingContent = {
            IconButton(onClick = { onAction(HistoryAction.OnFavoriteToggle(outfit.outfitId)) }) {
                Icon(
                    imageVector = if (outfit.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                )
            }
        },
    )
}

@Preview
@Composable
private fun HistoryScreenEmptyPreview() {
    FitlyTheme {
        HistoryScreen(state = HistoryState(), onAction = {})
    }
}

@Preview
@Composable
private fun HistoryScreenWithOutfitsPreview() {
    val item = ClothingItem(
        photoPath = "",
        dominantColor = 0xFFFF0000.toInt(),
        type = ClothingType.TOP,
        occasion = Occasion.CASUAL,
        season = Season.ALL_YEAR,
        condition = Condition.NEW,
        createdAt = 0L,
    )
    FitlyTheme {
        HistoryScreen(
            state = HistoryState(
                outfits = listOf(
                    ResolvedOutfit(
                        outfitId = 1L,
                        top = item.copy(type = ClothingType.TOP),
                        bottom = item.copy(type = ClothingType.BOTTOM),
                        shoes = item.copy(type = ClothingType.SHOES),
                        accessory = null,
                        status = OutfitStatus.ACCEPTED,
                        favorite = true,
                    ),
                ),
            ),
            onAction = {},
        )
    }
}
