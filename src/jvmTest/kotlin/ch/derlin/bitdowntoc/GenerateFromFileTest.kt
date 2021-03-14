package ch.derlin.bitdowntoc

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class GenerateFromFileTest {

    val input = "input.md".load()

    @Test
    fun `test full with anchors`() {
        assertThat(BitGenerator.generate(input)).isEqualTo(
            "output.md".load()
        )
    }

    @Test
    fun `test toc gitlab`() {
        assertThat(BitGenerator.generate(input, generateAnchors = false)).isEqualTo(
            "toc-gitlab.md".load() + "\n" + input
        )
    }

    @Test
    fun `test toc github`() {
        assertThat(BitGenerator.generate(input, generateAnchors = false, concatSpaces = false)).isEqualTo(
            "toc-github.md".load() + "\n" + input
        )
    }

    private fun String.load(): String =
        GenerateFromFileTest::class.java.classLoader.getResource(this)!!.readText()
}