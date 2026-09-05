package com.fitly.presentation.wardrobe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitly.R
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.Season
import com.fitly.presentation.ObserveAsEvents
import com.fitly.presentation.designsystem.ClothingCard
import com.fitly.presentation.designsystem.EmptyState
import com.fitly.presentation.designsystem.FilterRow
import com.fitly.presentation.designsystem.FitlyBottomSheet
import com.fitly.presentation.designsystem.FitlyFloatingActionButton
import com.fitly.presentation.designsystem.FitlyLargeTopAppBar
import com.fitly.presentation.designsystem.FitlyScaffold
import com.fitly.presentation.designsystem.FitlyTextButton
import com.fitly.presentation.designsystem.FitlyTheme
import com.fitly.presentation.designsystem.collapsingTopBar
import com.fitly.presentation.designsystem.rememberFitlyScrollBehavior
import com.fitly.presentation.labelRes
import com.fitly.presentation.preview.previewClothingItems
import org.koin.androidx.compose.koinViewModel

@Composable
fun WardrobeRoot(
    onNavigateToItemDetail: (Long) -> Unit,
    onNavigateToAddItem: () -> Unit,
    viewModel: WardrobeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is WardrobeEvent.NavigateToItemDetail -> onNavigateToItemDetail(event.id)
        }
    }

    WardrobeScreen(
        state = state,
        onAction = viewModel::onAction,
        onAddItemClick = onNavigateToAddItem,
    )
}

@Composable
fun WardrobeScreen(
    state: WardrobeState,
    onAction: (WardrobeAction) -> Unit,
    onAddItemClick: () -> Unit,
) {
    val scrollBehavior = rememberFitlyScrollBehavior()

    FitlyScaffold(
        modifier = Modifier.collapsingTopBar(scrollBehavior),
        topBar = {
            FitlyLargeTopAppBar(
                title = stringResource(R.string.wardrobe_title),
                scrollBehavior = scrollBehavior,
                actions = {
                    FitlyTextButton(
                        text = filtersLabel(state.sheetFilterCount),
                        onClick = { onAction(WardrobeAction.OnFiltersClick) },
                    )
                },
            )
        },
        floatingActionButton = {
            FitlyFloatingActionButton(
                icon = Icons.Default.Add,
                contentDescription = stringResource(R.string.cd_add_item),
                onClick = onAddItemClick,
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Type stays on the screen: it is the filter reached for constantly ("show me the
            // shoes"). Occasion and Season are occasional, and can cost a tap.
            FilterRow(
                allLabel = stringResource(R.string.filter_type_all),
                options = ClothingType.entries,
                selected = state.typeFilter,
                optionLabel = { it.labelRes },
                onSelected = { onAction(WardrobeAction.OnTypeFilterSelected(it)) },
            )

            if (state.visibleItems.isEmpty()) {
                EmptyState(
                    icon = Icons.AutoMirrored.Filled.List,
                    title = stringResource(R.string.wardrobe_empty_title),
                    supportingText = stringResource(
                        if (state.items.isEmpty()) {
                            R.string.wardrobe_empty_body
                        } else {
                            R.string.wardrobe_no_matches_body
                        },
                    ),
                    actionText = stringResource(R.string.wardrobe_add_first).takeIf { state.items.isEmpty() },
                    onActionClick = onAddItemClick.takeIf { state.items.isEmpty() },
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.visibleItems, key = { it.id }) { item ->
                        ClothingCard(
                            item = item,
                            onClick = { onAction(WardrobeAction.OnItemClick(item.id)) },
                        )
                    }
                }
            }
        }
    }

    if (state.filtersVisible) {
        FitlyBottomSheet(
            title = stringResource(R.string.filters_title),
            onDismiss = { onAction(WardrobeAction.OnFiltersDismiss) },
        ) {
            SheetSection(label = stringResource(R.string.filter_occasion)) {
                FilterRow(
                    allLabel = stringResource(R.string.filter_occasion_all),
                    options = Occasion.entries,
                    selected = state.occasionFilter,
                    optionLabel = { it.labelRes },
                    onSelected = { onAction(WardrobeAction.OnOccasionFilterSelected(it)) },
                )
            }
            SheetSection(label = stringResource(R.string.filter_season)) {
                FilterRow(
                    allLabel = stringResource(R.string.filter_season_all),
                    options = Season.entries,
                    selected = state.seasonFilter,
                    optionLabel = { it.labelRes },
                    onSelected = { onAction(WardrobeAction.OnSeasonFilterSelected(it)) },
                )
            }
        }
    }
}

@Composable
private fun SheetSection(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        content()
    }
}

@Composable
private fun filtersLabel(activeCount: Int): String = when (activeCount) {
    0 -> stringResource(R.string.filters_title)
    else -> stringResource(R.string.filters_title_with_count, activeCount)
}

@Preview
@Composable
private fun WardrobeScreenPreview() {
    FitlyTheme {
        WardrobeScreen(
            state = WardrobeState(items = previewClothingItems()),
            onAction = {},
            onAddItemClick = {},
        )
    }
}

@Preview
@Composable
private fun WardrobeScreenEmptyPreview() {
    FitlyTheme {
        WardrobeScreen(state = WardrobeState(), onAction = {}, onAddItemClick = {})
    }
}
