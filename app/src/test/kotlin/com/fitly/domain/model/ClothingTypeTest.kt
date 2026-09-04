package com.fitly.domain.model

import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import org.junit.jupiter.api.Test

class ClothingTypeTest {

    @Test
    fun `TOP fills only the TOP slot`() {
        assertThat(ClothingType.TOP.fillsSlots()).containsExactlyInAnyOrder(OutfitSlot.TOP)
    }

    @Test
    fun `BOTTOM fills only the BOTTOM slot`() {
        assertThat(ClothingType.BOTTOM.fillsSlots()).containsExactlyInAnyOrder(OutfitSlot.BOTTOM)
    }

    @Test
    fun `SHOES fills only the SHOES slot`() {
        assertThat(ClothingType.SHOES.fillsSlots()).containsExactlyInAnyOrder(OutfitSlot.SHOES)
    }

    @Test
    fun `ACCESSORY fills only the ACCESSORY slot`() {
        assertThat(ClothingType.ACCESSORY.fillsSlots()).containsExactlyInAnyOrder(OutfitSlot.ACCESSORY)
    }

    @Test
    fun `DRESS fills both TOP and BOTTOM slots`() {
        assertThat(ClothingType.DRESS.fillsSlots())
            .containsExactlyInAnyOrder(OutfitSlot.TOP, OutfitSlot.BOTTOM)
    }
}
