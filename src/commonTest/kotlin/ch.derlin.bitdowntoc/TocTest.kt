package ch.derlin.bitdowntoc

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull


class TocTest {

    @Test
    fun testDuplicateLinkGeneration() {
        val toc = Toc(levelBoundaries = 1 to 3)

        toc.addTocEntry(1, "Heading")
        toc.addTocEntry(4, "Heading")
        toc.addTocEntry(2, "Heading")
        toc.addTocEntry(5, "Heading")
        toc.addTocEntry(6, "Heading")
        toc.addTocEntry(3, "Heading")

        assertEquals(3, toc.entries.size, "3 entries should be registered")
        assertEquals(1, toc.links.size, "all entries should have the same link")
        assertEquals(mapOf(
            "Heading" to "heading",
            "Heading" to "heading-2",
            "Heading" to "heading-5"
        ), toc.entries.associate { Pair(it.title, it.link) })
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
