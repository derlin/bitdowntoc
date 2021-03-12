package ch.derlin.bitdowntoc

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class TocTest {

    @Test
    fun `test duplicate links generation`() {
        val headingToLinks = mutableListOf<Pair<String, String>>()

        val toc = Toc()
        repeat(3) {
            toc.addTocEntry(1, "Heading")
            headingToLinks += Pair("Heading", "heading" + (if (it > 0) "-$it" else ""))
        }
        assertThat(toc.entries).hasSize(3)
        assertThat(toc.links).hasSize(1)

        assertThat(toc.entries.map { Pair(it.title, it.link) }).isEqualTo(headingToLinks)
    }

    @Test
    fun `test title to link conversion github style`() {
        val toc = Toc(concatSpaces = false)

        val expected = listOf(
            "Some '??&%`%\"\\/^' strange :) # characters" to "some--strange---characters",
            "dès la matinée, ça gït" to "dès-la-matinée-ça-gït",
            "  this has  spaces  " to "this-has--spaces",
            " 😋 emojis 📋 and 👌" to "-emojis--and-",
        )

        assertAll {
            expected.forEach { (title, expected) ->
                assertThat(toc.addTocEntry(1, title).link).isEqualTo(expected)
            }
        }
    }

    @Test
    fun `test title to link conversion gitlab style`() {
        val toc = Toc(concatSpaces = true)

        val expected = listOf(
            "Some '??&%`%\"\\/^' strange :) # characters" to "some-strange-characters",
            "dès la matinée, ça gït" to "dès-la-matinée-ça-gït",
            "  this has  spaces  " to "this-has-spaces",
            " 😋 emojis 📋 and 👌" to "-emojis-and-",
        )

        assertAll {
            expected.forEach { (title, expected) ->
                assertThat(toc.addTocEntry(1, title).link).isEqualTo(expected)
            }
        }
    }
}