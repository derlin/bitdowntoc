package ch.derlin.bitdowntoc

import ch.derlin.bitdowntoc.BitGenerator.Params
import ch.derlin.bitdowntoc.BitGenerator.getWarnings
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class WarningsTest {

    @Test
    fun testGetWarnings() {
        assertNull(getWarnings(Params(generateAnchors = true), tocOnly = false))
        assertNull(getWarnings(Params(generateAnchors = false), tocOnly = true))

        val warnings = getWarnings(Params(generateAnchors = true), tocOnly = true)
        assertNotNull(warnings)
        assertEquals(warnings.size, 1)
        assertContains(warnings[0], "This TOC requires anchors to exist in the markdown content")
    }
}
