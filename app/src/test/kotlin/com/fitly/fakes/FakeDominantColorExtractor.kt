package com.fitly.fakes

import com.fitly.domain.datasource.DominantColorExtractor

class FakeDominantColorExtractor(var colorToReturn: Int = 0) : DominantColorExtractor {
    override suspend fun extract(photoBytes: ByteArray): Int = colorToReturn
}
