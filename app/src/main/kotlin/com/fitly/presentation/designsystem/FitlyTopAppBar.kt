package com.fitly.presentation.designsystem

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fitly.R

/**
 * The title on a top-level tab, in the serif display face, collapsing as the content scrolls under
 * it. Pass the [scrollBehavior] from `rememberTopAppBarScrollBehavior` and hand the same one to
 * the screen's `Modifier.nestedScroll`, or it will not collapse.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitlyLargeTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actions: @Composable () -> Unit = {},
) {
    LargeTopAppBar(
        title = { Text(title, style = MaterialTheme.typography.headlineMedium) },
        actions = { actions() },
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}

/**
 * The bar on a pushed screen. Always carries a back affordance - these screens hide the bottom bar,
 * so without it the only way out is the system gesture.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitlyTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit = {},
) {
    TopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            FitlyIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.cd_back),
                onClick = onBackClick,
            )
        },
        actions = { actions() },
        modifier = modifier,
    )
}
