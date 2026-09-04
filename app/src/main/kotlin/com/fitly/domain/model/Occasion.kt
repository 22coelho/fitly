package com.fitly.domain.model

/**
 * Shared vocabulary used both as a ClothingItem tag and as the Outfit Generator's filter —
 * the same field in both places, no mapping layer between them.
 */
enum class Occasion {
    CASUAL,
    WORK,
    SPORT,
    FORMAL,
    DATE,
}
