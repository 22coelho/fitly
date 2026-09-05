package com.fitly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fitly.presentation.designsystem.FitlyTheme
import com.fitly.presentation.history.HistoryRoot
import com.fitly.presentation.history.HistoryRoute
import com.fitly.presentation.home.HomeRoot
import com.fitly.presentation.home.HomeRoute
import com.fitly.presentation.wardrobe.WardrobeGraphRoute
import com.fitly.presentation.wardrobe.WardrobeRoute
import com.fitly.presentation.wardrobe.wardrobeGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitlyTheme {
                Surface(modifier = Modifier) {
                    FitlyNavHost()
                }
            }
        }
    }
}

private data class BottomTab(
    val route: Any,
    val label: String,
    val icon: ImageVector,
    val isSelected: (NavDestination?) -> Boolean,
)

private val bottomTabs = listOf(
    BottomTab(HomeRoute, "Home", Icons.Default.Home) { it?.hasRoute(HomeRoute::class) == true },
    BottomTab(WardrobeRoute, "Wardrobe", Icons.AutoMirrored.Filled.List) { it?.hasRoute(WardrobeRoute::class) == true },
    BottomTab(HistoryRoute, "Histórico", Icons.Default.DateRange) { it?.hasRoute(HistoryRoute::class) == true },
)

@Composable
private fun FitlyNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val isOnTopLevelTab = bottomTabs.any { it.isSelected(currentDestination) }

    Scaffold(
        bottomBar = {
            // Hidden on pushed screens (item detail, add item) so it's clear those
            // aren't one of the 3 tabs, and so a stray tab tap can't silently discard
            // an in-progress edit there.
            if (isOnTopLevelTab) {
                NavigationBar {
                    bottomTabs.forEach { tab ->
                        NavigationBarItem(
                            selected = tab.isSelected(currentDestination),
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = WardrobeGraphRoute,
            modifier = Modifier.padding(padding),
        ) {
            composable<HomeRoute> { HomeRoot() }
            composable<HistoryRoute> { HistoryRoot() }
            wardrobeGraph(navController)
        }
    }
}
