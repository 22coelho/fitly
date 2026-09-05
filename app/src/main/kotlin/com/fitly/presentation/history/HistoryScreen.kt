package com.fitly.presentation.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.fitly.presentation.designsystem.FitlyTheme

@Composable
fun HistoryRoot() {
    HistoryScreen()
}

@Composable
fun HistoryScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Histórico de outfits (em breve)",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Preview
@Composable
private fun HistoryScreenPreview() {
    FitlyTheme {
        HistoryScreen()
    }
}
