package ch.derlin.bitdowntoc

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.matchesPredicate
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GenerateTest {

    @Test
    fun `test mandatory toc`() {
        val input = """
            # heading
        """.trimIndent()

        assertThrows<RuntimeException> { BitGenerator.generate(input) }.let { ex ->
            assertThat(ex.message)
                .isNotNull()
                .matchesPredicate { it.contains("This library requires a [TOC] placeholder") }
        }
    }

    @Test
    fun `test basic generation`() {

        val input = """
            # Some readme
            
            [TOC]
            
            ## heading
            
            this is a test
            ```code
            ## comment, not header
            ```
            
            ### subheading
            
            test
            
            ## heading
            duplicate name
        """.trimIndent()

        assertThat(BitGenerator.generate(input)).isEqualTo(
            """
            # Some readme
            
            <!-- TOC start -->
            - [heading](heading)
              * [subheading](subheading)
            - [heading](heading-1)
            <!-- TOC end -->
            
            ## heading
            <!-- TOC --><a name="heading"></a>
            
            this is a test
            ```code
            ## comment, not header
            ```
            
            ### subheading
            <!-- TOC --><a name="subheading"></a>
            
            test
            
            ## heading
            <!-- TOC --><a name="heading-1"></a>
            duplicate name
        """.trimIndent()
        )
    }

    @Test
    fun `test no anchors`() {

        val input = """
            # Some readme
            
            [TOC]
            
            ## heading
            this is a test
            ```code
            ## comment, not header
            ```
            
            ### subheading
            hello
            
            ## heading
            duplicate name
        """.trimIndent()

        assertThat(BitGenerator.generate(input, generateAnchors = false)).isEqualTo(
            """
            # Some readme
            
            <!-- TOC start -->
            - [heading](heading)
              * [subheading](subheading)
            - [heading](heading-1)
            <!-- TOC end -->
            
            ## heading
            this is a test
            ```code
            ## comment, not header
            ```
            
            ### subheading
            hello
            
            ## heading
            duplicate name
        """.trimIndent()
        )
    }


    @Test
    fun `test no heading in document`() {
        val input = """
            # Some readme
            
            [TOC]
            
        """.trimIndent()

        assertThat(BitGenerator.generate(input, generateAnchors = false)).isEqualTo(
            """
            # Some readme
            
            <!-- TOC start -->
            
            <!-- TOC end -->

           """.trimIndent()
        )
    }
}