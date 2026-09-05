package com.fitly.presentation.designsystem

import androidx.compose.ui.graphics.Color
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

/**
 * Picking the ink that goes on top of a photo's extracted dominant colour. This fails silently in
 * exactly one direction - a pale garment gives a pale dominant colour, and white-on-beige is
 * unreadable without ever looking broken - so it is worth pinning down.
 */
class ContentColorTest {

    @Test
    fun `dark ink on a pale surface`() {
        assertThat(contentColorOn(Color.White)).isEqualTo(Color.Black)
        assertThat(contentColorOn(Color(0xFFFFF8F5))).isEqualTo(Color.Black)
    }

    @Test
    fun `light ink on a dark surface`() {
        assertThat(contentColorOn(Color.Black)).isEqualTo(Color.White)
        assertThat(contentColorOn(Color(0xFF19120F))).isEqualTo(Color.White)
    }

    @Test
    fun `the app's own terracotta takes white, as the palette assumes`() {
        assertThat(contentColorOn(Color(0xFFBA532E))).isEqualTo(Color.White)
    }

    @Test
    fun `a pale garment does not get white ink`() {
        // Beige. Luminance sits high enough that white would drop to roughly 1.7:1.
        assertThat(contentColorOn(Color(0xFFE8D8C3))).isEqualTo(Color.Black)
    }
}
