package com.fitly.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import com.fitly.presentation.designsystem.FilterRow
import com.fitly.presentation.designsystem.FitlyTheme
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
            is HomeEvent.ShowError -> scope.launch { snackbarHostState.showSnackbar(context.getString(event.error.messageRes)) }
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
    Scaffold(
        // Insets belong to the host Scaffold in MainActivity; adding them here doubles them.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            FilterRow(
                allLabel = "Ocasião: todos",
                options = Occasion.entries,
                selected = state.occasionFilter,
                optionLabel = { it.labelRes },
                onSelected = { onAction(HomeAction.OnOccasionFilterSelected(it)) },
            )

            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                when {
                    state.outfit != null -> OutfitCard(outfit = state.outfit, onAction = onAction)
                    state.noItemsAvailable -> Text("Sem peças suficientes na wardrobe para gerar um outfit.")
                    else -> Text("Toca em Gerar para veres uma sugestão.")
                }
            }

            Button(
                onClick = { onAction(HomeAction.OnGenerateClick) },
                enabled = state.status == HomeStatus.IDLE,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            ) {
                Text("Gerar outfit")
            }
        }
    }
}

@Composable
private fun OutfitCard(outfit: ResolvedOutfit, onAction: (HomeAction) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            listOfNotNull(outfit.top, outfit.bottom, outfit.shoes, outfit.accessory).forEach { item ->
                ClothingPhoto(photoPath = item.photoPath, dominantColor = item.dominantColor, modifier = Modifier.weight(1f).height(120.dp))
            }
        }
        IconButton(
            onClick = { onAction(HomeAction.OnFavoriteToggle) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = if (outfit.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorito",
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { onAction(HomeAction.OnRejectClick) }, modifier = Modifier.weight(1f)) {
                Text("Rejeitar")
            }
            Button(onClick = { onAction(HomeAction.OnAcceptClick) }, modifier = Modifier.weight(1f)) {
                Text("Aceitar")
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenEmptyPreview() {
    FitlyTheme {
        HomeScreen(state = HomeState(), onAction = {})
    }
}

@Preview
@Composable
private fun HomeScreenWithOutfitPreview() {
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
        HomeScreen(
            state = HomeState(
                outfit = ResolvedOutfit(
                    outfitId = 1L,
                    top = item.copy(type = ClothingType.TOP),
                    bottom = item.copy(type = ClothingType.BOTTOM),
                    shoes = item.copy(type = ClothingType.SHOES),
                    accessory = null,
                    status = OutfitStatus.PENDING,
                    favorite = false,
                ),
            ),
            onAction = {},
        )
    }
}
