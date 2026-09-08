package com.fitly.fakes

import com.fitly.domain.datasource.TypeSuggester
import com.fitly.domain.model.ClothingType

class FakeTypeSuggester(var typeToReturn: ClothingType? = null) : TypeSuggester {
    override suspend fun suggest(photoBytes: ByteArray): ClothingType? = typeToReturn
}
