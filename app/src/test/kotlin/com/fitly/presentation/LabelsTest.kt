package com.fitly.presentation

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isNotEqualTo
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Condition
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.OutfitStatus
import com.fitly.domain.model.Season
import com.fitly.domain.util.DataError
import org.junit.jupiter.api.Test

/**
 * The mapping from a domain enum to the string the user reads. What can silently go wrong here is
 * a copy-paste: two values pointing at the same resource, so the UI quietly calls every dress a
 * shirt. Resolving the actual Portuguese text needs Robolectric and is not what breaks.
 */
class LabelsTest {

    @Test
    fun `every clothing type has its own label`() {
        assertAllDistinct(ClothingType.entries.map { it.labelRes })
    }

    @Test
    fun `every occasion has its own label`() {
        assertAllDistinct(Occasion.entries.map { it.labelRes })
    }

    @Test
    fun `every season has its own label`() {
        assertAllDistinct(Season.entries.map { it.labelRes })
    }

    @Test
    fun `every condition has its own label`() {
        assertAllDistinct(Condition.entries.map { it.labelRes })
    }

    @Test
    fun `every outfit status has its own label`() {
        assertAllDistinct(OutfitStatus.entries.map { it.labelRes })
    }

    @Test
    fun `every local error has its own message`() {
        assertAllDistinct(DataError.Local.entries.map { it.messageRes })
    }

    private fun assertAllDistinct(resources: List<Int>) {
        assertThat(resources.toSet()).hasSize(resources.size)
        resources.forEach { assertThat(it).isNotEqualTo(0) }
    }
}
