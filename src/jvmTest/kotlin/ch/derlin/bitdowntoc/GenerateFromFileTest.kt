package ch.derlin.bitdowntoc

import assertk.assertThat
import assertk.assertions.isEqualTo
import ch.derlin.bitdowntoc.BitGenerator.Params
import org.junit.jupiter.api.Test

class GenerateFromFileTest {

    private val input = TEST_INPUT_FILE.load()

    @Test
    fun `test full with anchors`() {
        assertThat(BitGenerator.generate(input)).isEqualTo(
            output()
        )
    }

    @Test
    fun `test toc gitlab`() {
        assertThat(BitGenerator.generate(input, Params(generateAnchors = false))).isEqualTo(
            outputGitlab()
        )
    }

    @Test
    fun `test toc github`() {
        assertThat(BitGenerator.generate(input, Params(generateAnchors = false, concatSpaces = false))).isEqualTo(
            outputGithub()
        )
    }
}
