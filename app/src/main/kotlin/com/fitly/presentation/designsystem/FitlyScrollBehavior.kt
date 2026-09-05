package com.fitly.presentation.designsystem

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll

/**
 * Opaque holder for the Material scroll behaviour that makes [FitlyLargeTopAppBar] collapse.
 *
 * It wraps the Material type instead of exposing it so that a screen never has to opt into an
 * experimental Material API - the boundary test would flag the import anyway, and this is the
 * reason that rule is not merely bureaucratic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Stable
class FitlyScrollBehavior internal constructor(
    internal val delegate: TopAppBarScrollBehavior,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberFitlyScrollBehavior(): FitlyScrollBehavior =
    FitlyScrollBehavior(TopAppBarDefaults.exitUntilCollapsedScrollBehavior())

/** Feeds the screen's scrolling to [behavior] so the title collapses with it. */
@OptIn(ExperimentalMaterial3Api::class)
fun Modifier.collapsingTopBar(behavior: FitlyScrollBehavior): Modifier =
    nestedScroll(behavior.delegate.nestedScrollConnection)
