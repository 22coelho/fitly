package com.fitly.domain.classifier

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.fitly.domain.model.ClothingType
import org.junit.jupiter.api.Test

class GarmentTypeMapperTest {

    private val mapper = GarmentTypeMapper()

    @Test
    fun `a mapped label above the confidence threshold returns its type`() {
        val result = mapper.map(listOf(DetectedLabel("Dress", confidence = 0.8f)))

        assertThat(result).isEqualTo(ClothingType.DRESS)
    }

    @Test
    fun `a mapped label below the confidence threshold returns null`() {
        val result = mapper.map(listOf(DetectedLabel("Dress", confidence = 0.3f)))

        assertThat(result).isNull()
    }

    @Test
    fun `a label at exactly the confidence threshold is accepted`() {
        val result = mapper.map(listOf(DetectedLabel("Dress", confidence = 0.6f)))

        assertThat(result).isEqualTo(ClothingType.DRESS)
    }

    @Test
    fun `a label with no entry in the mapping table returns null, even above threshold`() {
        val result = mapper.map(listOf(DetectedLabel("Selfie", confidence = 0.9f)))

        assertThat(result).isNull()
    }

    @Test
    fun `Gown maps to DRESS, same as Dress`() {
        val result = mapper.map(listOf(DetectedLabel("Gown", confidence = 0.8f)))

        assertThat(result).isEqualTo(ClothingType.DRESS)
    }

    @Test
    fun `Jeans, Shorts, Leggings and Tights all map to BOTTOM`() {
        for (label in listOf("Jeans", "Shorts", "Leggings", "Tights")) {
            assertThat(mapper.map(listOf(DetectedLabel(label, confidence = 0.8f)))).isEqualTo(ClothingType.BOTTOM)
        }
    }

    @Test
    fun `Jacket, Blazer and Jersey all map to TOP`() {
        for (label in listOf("Jacket", "Blazer", "Jersey")) {
            assertThat(mapper.map(listOf(DetectedLabel(label, confidence = 0.8f)))).isEqualTo(ClothingType.TOP)
        }
    }

    @Test
    fun `Shoe and Sneakers both map to SHOES`() {
        for (label in listOf("Shoe", "Sneakers")) {
            assertThat(mapper.map(listOf(DetectedLabel(label, confidence = 0.8f)))).isEqualTo(ClothingType.SHOES)
        }
    }

    @Test
    fun `Necklace, Bracelet, Tie, Scarf, Cap, Hat and Sunglasses all map to ACCESSORY`() {
        for (label in listOf("Necklace", "Bracelet", "Tie", "Scarf", "Cap", "Hat", "Sunglasses")) {
            assertThat(mapper.map(listOf(DetectedLabel(label, confidence = 0.8f)))).isEqualTo(ClothingType.ACCESSORY)
        }
    }

    @Test
    fun `when two mapped candidates are both above threshold, the higher-confidence one wins`() {
        val result = mapper.map(
            listOf(
                DetectedLabel("Dress", confidence = 0.7f),
                DetectedLabel("Necklace", confidence = 0.9f),
            ),
        )

        assertThat(result).isEqualTo(ClothingType.ACCESSORY)
    }

    @Test
    fun `order in the input list doesn't affect which candidate wins`() {
        val result = mapper.map(
            listOf(
                DetectedLabel("Necklace", confidence = 0.9f),
                DetectedLabel("Dress", confidence = 0.7f),
            ),
        )

        assertThat(result).isEqualTo(ClothingType.ACCESSORY)
    }
}
