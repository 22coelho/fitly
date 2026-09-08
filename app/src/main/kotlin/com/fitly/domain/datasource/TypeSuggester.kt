package com.fitly.domain.datasource

import com.fitly.domain.model.ClothingType

interface TypeSuggester {
    /** Best-effort guess of the garment's ClothingType from its photo. Null means no confident match. */
    suspend fun suggest(photoBytes: ByteArray): ClothingType?
}
