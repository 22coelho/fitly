package com.fitly.presentation

import assertk.assertThat
import assertk.assertions.isEmpty
import java.io.File
import org.junit.jupiter.api.Test

/**
 * Guards the rule that screens build from the design system, not from Material 3 directly. A line
 * in CLAUDE.md does not survive three months of edits; a red test does.
 *
 * Four primitives stay allowed. They carry no design decision of their own - `MaterialTheme` is
 * how a screen reads the very tokens the design system defines, `Text` and `Icon` are given their
 * look entirely by the theme, and `SnackbarHostState` is a state holder a Route has to construct
 * before it can hand it to `FitlyScaffold`. Everything with a shape, an elevation or a colour role
 * baked in gets a wrapper.
 */
class DesignSystemBoundaryTest {

    private val allowed = setOf("MaterialTheme", "Text", "Icon", "SnackbarHostState")

    @Test
    fun `no screen imports a Material 3 component directly`() {
        val offenders = screenFiles().flatMap { file ->
            file.readLines()
                .mapNotNull { MATERIAL3_IMPORT.find(it)?.groupValues?.get(1) }
                .filterNot { it in allowed }
                .map { "${file.name} imports androidx.compose.material3.$it" }
        }
        assertThat(offenders).isEmpty()
    }

    private fun screenFiles(): List<File> {
        val roots = listOf(File("src/main/kotlin"), File("app/src/main/kotlin"))
        val root = roots.firstOrNull { it.isDirectory }
            ?: error("Could not find the source root from ${File(".").absolutePath}")
        return root.walkTopDown()
            .filter { it.isFile && it.name.endsWith("Screen.kt") }
            .toList()
            .also { check(it.isNotEmpty()) { "Found no *Screen.kt files under $root" } }
    }

    private companion object {
        val MATERIAL3_IMPORT = Regex("""^import androidx\.compose\.material3\.(\w+)""")
    }
}
