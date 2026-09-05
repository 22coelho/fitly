package com.fitly.presentation.wardrobe

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.fitly.presentation.wardrobe.additem.AddItemRoot
import com.fitly.presentation.wardrobe.additem.AddItemRoute
import com.fitly.presentation.wardrobe.itemdetail.ItemDetailRoot
import com.fitly.presentation.wardrobe.itemdetail.ItemDetailRoute
import kotlinx.serialization.Serializable

/** Identity of the wardrobe nested graph - distinct from [WardrobeRoute] itself, since a
 * graph's route can't also be one of its own child destinations. */
@Serializable
data object WardrobeGraphRoute

/** Wardrobe owns its own intra-feature navigation (item detail, add item) internally. */
fun NavGraphBuilder.wardrobeGraph(navController: NavController) {
    navigation<WardrobeGraphRoute>(startDestination = WardrobeRoute) {
        composable<WardrobeRoute> {
            WardrobeRoot(
                onNavigateToItemDetail = { id -> navController.navigate(ItemDetailRoute(id)) },
                onNavigateToAddItem = { navController.navigate(AddItemRoute) },
            )
        }
        composable<ItemDetailRoute> {
            ItemDetailRoot(onNavigateBack = { navController.popBackStack() })
        }
        composable<AddItemRoute> {
            AddItemRoot(onNavigateBack = { navController.popBackStack() })
        }
    }
}
