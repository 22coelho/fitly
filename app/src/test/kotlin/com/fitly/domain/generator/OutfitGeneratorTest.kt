package com.fitly.domain.generator

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Occasion
import com.fitly.testutil.testClothingItem
import org.junit.jupiter.api.Test

class OutfitGeneratorTest {

    private val generator = OutfitGenerator()

    @Test
    fun `generates an outfit using a top, bottom and shoes when all are available`() {
        val top = testClothingItem(ClothingType.TOP).copy(id = 1)
        val bottom = testClothingItem(ClothingType.BOTTOM).copy(id = 2)
        val shoes = testClothingItem(ClothingType.SHOES).copy(id = 3)

        val outfit = generator.generate(items = listOf(top, bottom, shoes))

        assertThat(outfit).isNotNull()
        assertThat(outfit!!.topItemId).isEqualTo(1L)
        assertThat(outfit.bottomItemId).isEqualTo(2L)
        assertThat(outfit.shoesItemId).isEqualTo(3L)
    }

    @Test
    fun `returns null when there are no shoes available`() {
        val top = testClothingItem(ClothingType.TOP).copy(id = 1)
        val bottom = testClothingItem(ClothingType.BOTTOM).copy(id = 2)

        val outfit = generator.generate(items = listOf(top, bottom))

        assertThat(outfit).isNull()
    }

    @Test
    fun `returns null when there is no way to cover top and bottom`() {
        val shoes = testClothingItem(ClothingType.SHOES).copy(id = 1)

        val outfit = generator.generate(items = listOf(shoes))

        assertThat(outfit).isNull()
    }

    @Test
    fun `a dress fills both the top and bottom slot`() {
        val dress = testClothingItem(ClothingType.DRESS).copy(id = 1)
        val shoes = testClothingItem(ClothingType.SHOES).copy(id = 2)

        val outfit = generator.generate(items = listOf(dress, shoes))

        assertThat(outfit).isNotNull()
        assertThat(outfit!!.topItemId).isEqualTo(1L)
        assertThat(outfit.bottomItemId).isEqualTo(1L)
    }

    @Test
    fun `filtering by occasion never picks an item tagged with a different occasion`() {
        val matchingTop = testClothingItem(ClothingType.TOP, occasion = Occasion.CASUAL).copy(id = 1)
        val mismatchedTop = testClothingItem(ClothingType.TOP, occasion = Occasion.WORK).copy(id = 2)
        val bottom = testClothingItem(ClothingType.BOTTOM, occasion = Occasion.CASUAL).copy(id = 3)
        val shoes = testClothingItem(ClothingType.SHOES, occasion = Occasion.CASUAL).copy(id = 4)

        val outfit = generator.generate(items = listOf(matchingTop, mismatchedTop, bottom, shoes), occasion = Occasion.CASUAL)

        assertThat(outfit).isNotNull()
        assertThat(outfit!!.topItemId).isEqualTo(1L)
    }

    @Test
    fun `includes an accessory when one is available`() {
        val top = testClothingItem(ClothingType.TOP).copy(id = 1)
        val bottom = testClothingItem(ClothingType.BOTTOM).copy(id = 2)
        val shoes = testClothingItem(ClothingType.SHOES).copy(id = 3)
        val accessory = testClothingItem(ClothingType.ACCESSORY).copy(id = 4)

        val outfit = generator.generate(items = listOf(top, bottom, shoes, accessory))

        assertThat(outfit).isNotNull()
        assertThat(outfit!!.accessoryItemId).isEqualTo(4L)
    }

    @Test
    fun `omits the accessory when none is available`() {
        val top = testClothingItem(ClothingType.TOP).copy(id = 1)
        val bottom = testClothingItem(ClothingType.BOTTOM).copy(id = 2)
        val shoes = testClothingItem(ClothingType.SHOES).copy(id = 3)

        val outfit = generator.generate(items = listOf(top, bottom, shoes))

        assertThat(outfit).isNotNull()
        assertThat(outfit!!.accessoryItemId).isNull()
    }
}
