package com.fitly.presentation.wardrobe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitly.domain.model.ClothingItem
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.Season
import com.fitly.presentation.ObserveAsEvents
import com.fitly.presentation.designsystem.ClothingPhoto
import com.fitly.presentation.designsystem.FilterRow
import com.fitly.presentation.designsystem.FitlyTheme
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
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddItemClick) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar peça")
            }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            FilterRow(
                label = "Tipo",
                options = ClothingType.entries,
                selected = state.typeFilter,
                optionLabel = { it.name },
                onSelected = { onAction(WardrobeAction.OnTypeFilterSelected(it)) },
            )
            FilterRow(
                label = "Ocasião",
                options = Occasion.entries,
                selected = state.occasionFilter,
                optionLabel = { it.name },
                onSelected = { onAction(WardrobeAction.OnOccasionFilterSelected(it)) },
            )
            FilterRow(
                label = "Estação",
                options = Season.entries,
                selected = state.seasonFilter,
                optionLabel = { it.name },
                onSelected = { onAction(WardrobeAction.OnSeasonFilterSelected(it)) },
            )

            if (state.visibleItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ainda não há peças na wardrobe.")
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                    items(state.visibleItems, key = { it.id }) { item ->
                        ClothingItemRow(item = item, onClick = { onAction(WardrobeAction.OnItemClick(item.id)) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ClothingItemRow(item: ClothingItem, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        leadingContent = {
            ClothingPhoto(photoPath = item.photoPath, modifier = Modifier.size(56.dp))
        },
        headlineContent = { Text(item.type.name) },
        supportingContent = { Text("${item.occasion.name} • ${item.season.name} • ${item.condition.name}") },
    )
}

@Preview
@Composable
private fun WardrobeScreenPreview() {
    FitlyTheme {
        WardrobeScreen(state = WardrobeState(), onAction = {}, onAddItemClick = {})
    }
}
