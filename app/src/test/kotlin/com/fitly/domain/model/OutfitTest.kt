package com.fitly.domain.model

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import org.junit.jupiter.api.Test

class OutfitTest {

    @Test
    fun `rewear resets id, status and favorite but keeps the same items and occasion`() {
        val worn = Outfit(
            id = 7,
            topItemId = 1,
            bottomItemId = 2,
            shoesItemId = 3,
            accessoryItemId = 4,
            occasion = Occasion.WORK,
            status = OutfitStatus.ACCEPTED,
            favorite = true,
            createdAt = 1_000L,
        )

        val rewearing = worn.rewear(now = 2_000L)

        assertThat(rewearing.id).isEqualTo(0L)
        assertThat(rewearing.status).isEqualTo(OutfitStatus.PENDING)
        assertThat(rewearing.favorite).isFalse()
        assertThat(rewearing.createdAt).isEqualTo(2_000L)
        assertThat(rewearing.topItemId).isEqualTo(1L)
        assertThat(rewearing.bottomItemId).isEqualTo(2L)
        assertThat(rewearing.shoesItemId).isEqualTo(3L)
        assertThat(rewearing.accessoryItemId).isEqualTo(4L)
        assertThat(rewearing.occasion).isEqualTo(Occasion.WORK)
    }
}
