package ch.derlin.bitdowntoc

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull


class TocTest {

    @Test
    fun testDuplicateLinkGeneration() {
        val headingToLinks = mutableListOf<Pair<String, String>>()
        val toc = Toc()

        repeat(3) {
            toc.addTocEntry(1, "Heading")
            headingToLinks += Pair("Heading", "heading" + (if (it > 0) "-$it" else ""))
        }
        assertEquals(3, toc.entries.size, "3 entries should be registered")
        assertEquals(1, toc.links.size, "all entries should have the same link")
        assertEquals(headingToLinks, toc.entries.map { Pair(it.title, it.link) })
    }

    @Test
    fun testTitleToLinkConversionGithub() {
        val toc = Toc(concatSpaces = false)

        val expected = listOf(
            "Some '??&%`%\"\\/^' strange :) # characters" to "some--strange---characters",
            "dès la matinée, ça gït" to "dès-la-matinée-ça-gït",
            "  this has  spaces  " to "this-has--spaces",
            " 😋 emojis 📋 and 👌" to "-emojis--and-",
        )

        expected.forEach { (title, expected) ->
            val tocEntry = toc.addTocEntry(1, title)
            assertNotNull(tocEntry)
            assertEquals(expected, tocEntry.link)
        }

    }

    @Test
    fun testTitleToLinkConversionGitlab() {
        val toc = Toc(concatSpaces = true)

        val expected = listOf(
            "Some '??&%`%\"\\/^' strange :) # characters" to "some-strange-characters",
            "dès la matinée, ça gït" to "dès-la-matinée-ça-gït",
            "  this has  spaces  " to "this-has-spaces",
            " 😋 emojis 📋 and 👌" to "-emojis-and-",
        )

        expected.forEach { (title, expected) ->
            val tocEntry = toc.addTocEntry(1, title)
            assertNotNull(tocEntry)
            assertEquals(expected, tocEntry.link)
        }
    }

    @Test
    fun testBoundariesAreRespected() {
        val maxLevel = 3
        val toc = Toc(levelBoundaries = null)
        val tocBoundaries = Toc(levelBoundaries = Pair(1, maxLevel))
        (1..5).forEach { level ->
            assertNotNull(toc.addTocEntry(level, "test"))
            if (level <= 3) {
                assertNotNull(tocBoundaries.addTocEntry(level, "test"))
            } else {
                assertNull(tocBoundaries.addTocEntry(level, "test"))
            }
        }
    }

    @Test
    fun testAnchorPrefixIsAdded() {
        listOf("xxx", "heading-", "").forEach {
            assertEquals(Toc(anchorsPrefix = it).addTocEntry(1, "A test")?.link, "${it}a-test")
        }
    }
}
