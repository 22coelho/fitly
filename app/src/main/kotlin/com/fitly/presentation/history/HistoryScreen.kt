package com.fitly.presentation.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitly.R
import com.fitly.domain.model.OutfitStatus
import com.fitly.domain.model.ResolvedOutfit
import com.fitly.presentation.ObserveAsEvents
import com.fitly.presentation.designsystem.ClothingPhoto
import com.fitly.presentation.designsystem.EmptyState
import com.fitly.presentation.designsystem.FitlyCard
import com.fitly.presentation.designsystem.FitlyChip
import com.fitly.presentation.designsystem.FitlyIconButton
import com.fitly.presentation.designsystem.FitlyLargeTopAppBar
import com.fitly.presentation.designsystem.FitlyScaffold
import com.fitly.presentation.designsystem.FitlyTheme
import com.fitly.presentation.designsystem.PhotoFit
import com.fitly.presentation.designsystem.TagChip
import com.fitly.presentation.labelRes
import com.fitly.presentation.messageRes
import com.fitly.presentation.preview.previewOutfit
import java.text.DateFormat
import java.util.Date
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
            is HistoryEvent.ShowError ->
                scope.launch { snackbarHostState.showSnackbar(context.getString(event.error.messageRes)) }
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
    FitlyScaffold(
        topBar = {
            FitlyLargeTopAppBar(
                title = stringResource(R.string.history_title),
            )
        },
        snackbarHostState = snackbarHostState,
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // The only place favourites lead anywhere. The flag is on the Outfit, so this is the
            // only screen where filtering by it means anything.
            FitlyChip(
                text = stringResource(R.string.favorites_only),
                selected = state.favoritesOnly,
                onClick = { onAction(HistoryAction.OnFavoritesOnlyToggle) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )

            if (state.visibleOutfits.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.DateRange,
                    title = stringResource(R.string.history_empty_title),
                    supportingText = stringResource(R.string.history_empty_body),
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.visibleOutfits, key = { it.outfitId }) { outfit ->
                        HistoryOutfitCard(outfit = outfit, onAction = onAction)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryOutfitCard(outfit: ResolvedOutfit, onAction: (HistoryAction) -> Unit) {
    FitlyCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOfNotNull(
                outfit.top,
                outfit.bottom.takeIf { it.id != outfit.top.id },
                outfit.shoes,
                outfit.accessory,
            ).forEach { item ->
                ClothingPhoto(
                    photoPath = item.photoPath,
                    dominantColor = item.dominantColor,
                    fit = PhotoFit.Cover,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.width(72.dp),
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 4.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TagChip(text = stringResource(outfit.status.labelRes))
            Text(
                text = formatDate(outfit.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            FitlyIconButton(
                icon = if (outfit.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = stringResource(
                    if (outfit.favorite) R.string.cd_unfavorite else R.string.cd_favorite,
                ),
                onClick = { onAction(HistoryAction.OnFavoriteToggle(outfit.outfitId)) },
            )
        }
    }
}

/** The device's own short date format, so it follows the user's locale rather than ours. */
private fun formatDate(epochMillis: Long): String =
    DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(epochMillis))

@Preview
@Composable
private fun HistoryScreenPreview() {
    FitlyTheme {
        HistoryScreen(
            state = HistoryState(
                outfits = listOf(
                    previewOutfit(favorite = true, status = OutfitStatus.ACCEPTED),
                    previewOutfit(withAccessory = false, status = OutfitStatus.REJECTED)
                        .copy(outfitId = 2L),
                ),
            ),
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun HistoryScreenEmptyPreview() {
    FitlyTheme {
        HistoryScreen(state = HistoryState(), onAction = {})
    }
}
