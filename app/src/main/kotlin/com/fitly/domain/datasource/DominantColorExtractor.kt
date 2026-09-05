package com.fitly.domain.datasource

interface DominantColorExtractor {
    /** Packed ARGB color extracted from the given photo bytes. */
    suspend fun extract(photoBytes: ByteArray): Int
}
