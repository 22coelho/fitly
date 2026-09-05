package com.fitly.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitly.R
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.ResolvedOutfit
import com.fitly.presentation.ObserveAsEvents
import com.fitly.presentation.designsystem.EmptyState
import com.fitly.presentation.designsystem.FilterRow
import com.fitly.presentation.designsystem.FitlyButton
import com.fitly.presentation.designsystem.FitlyIconButton
import com.fitly.presentation.designsystem.FitlyLargeTopAppBar
import com.fitly.presentation.designsystem.FitlyOutlinedButton
import com.fitly.presentation.designsystem.FitlyScaffold
import com.fitly.presentation.designsystem.FitlyTheme
import com.fitly.presentation.designsystem.OutfitPreview
import com.fitly.presentation.designsystem.collapsingTopBar
import com.fitly.presentation.designsystem.contentColorOn
import com.fitly.presentation.designsystem.rememberFitlyScrollBehavior
import com.fitly.presentation.labelRes
import com.fitly.presentation.messageRes
import com.fitly.presentation.preview.previewOutfit
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoot(viewModel: HomeViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is HomeEvent.ShowError ->
                scope.launch { snackbarHostState.showSnackbar(context.getString(event.error.messageRes)) }
        }
    }

    HomeScreen(state = state, onAction = viewModel::onAction, snackbarHostState = snackbarHostState)
}

@Composable
fun HomeScreen(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val scrollBehavior = rememberFitlyScrollBehavior()

    FitlyScaffold(
        modifier = Modifier.collapsingTopBar(scrollBehavior),
        topBar = {
            FitlyLargeTopAppBar(
                title = stringResource(R.string.home_title),
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHostState = snackbarHostState,
        bottomBar = { HomeActionBar(state = state, onAction = onAction) },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            FilterRow(
                allLabel = stringResource(R.string.filter_occasion_all),
                options = Occasion.entries,
                selected = state.occasionFilter,
                optionLabel = { it.labelRes },
                onSelected = { onAction(HomeAction.OnOccasionFilterSelected(it)) },
            )

            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                when {
                    state.outfit != null -> OutfitCard(outfit = state.outfit, onAction = onAction)
                    state.noItemsAvailable -> EmptyState(
                        icon = Icons.AutoMirrored.Filled.List,
                        title = stringResource(R.string.home_not_enough_title),
                        supportingText = stringResource(R.string.home_not_enough_body),
                    )
                    else -> EmptyState(
                        icon = Icons.Default.FavoriteBorder,
                        title = stringResource(R.string.home_empty_title),
                        supportingText = stringResource(R.string.home_empty_body),
                    )
                }
            }
        }
    }
}

/**
 * Read top to bottom, the way the outfit is worn. The favourite toggle sits on the card because it
 * is about this outfit; accept and reject live in the bar below because they are about what happens
 * next, and stay under the thumb however tall the card grows.
 */
@Composable
private fun OutfitCard(outfit: ResolvedOutfit, onAction: (HomeAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            OutfitPreview(outfit = outfit)
            FitlyIconButton(
                icon = if (outfit.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = stringResource(
                    if (outfit.favorite) R.string.cd_unfavorite else R.string.cd_favorite,
                ),
                onClick = { onAction(HomeAction.OnFavoriteToggle) },
                // Tinted against the garment it sits on: a white heart vanishes on a cream shirt.
                tint = contentColorOn(Color(outfit.top.dominantColor)),
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
            )
        }
    }
}

@Composable
private fun HomeActionBar(state: HomeState, onAction: (HomeAction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (state.outfit == null) {
            FitlyButton(
                text = stringResource(R.string.action_generate),
                onClick = { onAction(HomeAction.OnGenerateClick) },
                enabled = state.status == HomeStatus.IDLE,
                modifier = Modifier.weight(1f),
            )
        } else {
            FitlyOutlinedButton(
                text = stringResource(R.string.action_reject),
                onClick = { onAction(HomeAction.OnRejectClick) },
                modifier = Modifier.weight(1f),
            )
            FitlyButton(
                text = stringResource(R.string.action_accept),
                onClick = { onAction(HomeAction.OnAcceptClick) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenWithOutfitPreview() {
    FitlyTheme {
        HomeScreen(state = HomeState(outfit = previewOutfit(favorite = true)), onAction = {})
    }
}

@Preview
@Composable
private fun HomeScreenEmptyPreview() {
    FitlyTheme {
        HomeScreen(state = HomeState(), onAction = {})
    }
}
