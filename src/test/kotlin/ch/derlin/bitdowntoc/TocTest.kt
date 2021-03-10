package ch.derlin.bitdowntoc

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
        assertThat(toc.titles).hasSize(1)

        assertThat(toc.entries.map { Pair(it.title, it.link) }).isEqualTo(headingToLinks)
    }

    @Test
    fun `test title to link conversion`() {

    }
}