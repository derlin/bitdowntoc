package ch.derlin.bitdowntoc

import ch.derlin.bitdowntoc.BitGenerator.Params
import ch.derlin.bitdowntoc.CommentStyle.LIQUID
import kotlin.test.Test
import kotlin.test.assertEquals

class GenerateTest {

    @Test
    fun testImplicitToc() {
        val input = "# heading"

        assertEquals(
            """
            <!-- TOC start (generated with $BITDOWNTOC_URL) -->
            
            - [heading](#heading)
            
            <!-- TOC end -->
            
            # heading
            """.trimIndent(),
            BitGenerator.generate(input, Params(generateAnchors = false))
        )
    }

    @Test
    fun testBasicGeneration() {
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

        assertEquals(
            """
            # Some readme
            
            <!-- TOC start (generated with $BITDOWNTOC_URL) -->
            
            - [heading](#heading)
              * [subheading](#subheading)
            - [heading](#heading-1)
            
            <!-- TOC end -->
            
            <!-- TOC --><a name="heading"></a>
            ## heading
            
            this is a test
            ```code
            ## comment, not header
            ```
            
            <!-- TOC --><a name="subheading"></a>
            ### subheading
            
            test
            
            <!-- TOC --><a name="heading-1"></a>
            ## heading
            duplicate name
            """.trimIndent(),
            BitGenerator.generate(input)
        )
    }

    @Test
    fun testCodeBlocks() {

        assertDoesNotChangeToc(
            """
            * list 1
              ```html
              # not a heading
              ```
            * list 2
                - sublist
                  ```
                  # not a heading either
                  ```
            ```python
            def bar() -> bool:
                return True
            ```
            """
        )

        assertDoesNotChangeToc(
            """
            ~~~
            ## comment, not header
            ~~~
            """.trimIndent()
        )

        assertDoesNotChangeToc(
            """
            ``` Python [title=no]
            ## comment, not header
            ```
            """.trimIndent()
        )
    }

    @Test
    fun testGenerateWithoutAnchors() {

        val input = """
        # Some readme
        
        [TOC]
        
        ## heading
        this is a test [TOC]
        ```code
        ## comment, not header
        ```
        
        ### subheading
        hello
        
        ## heading
        duplicate name
        """.trimIndent()

        assertEquals(
            """
            # Some readme
            
            <!-- TOC start (generated with $BITDOWNTOC_URL) -->
            
            - [heading](#heading)
              * [subheading](#subheading)
            - [heading](#heading-1)
            
            <!-- TOC end -->
            
            ## heading
            this is a test [TOC]
            ```code
            ## comment, not header
            ```
            
            ### subheading
            hello
            
            ## heading
            duplicate name
            """.trimIndent(),
            BitGenerator.generate(input, Params(generateAnchors = false))
        )
    }


    @Test
    fun testNoText() {
        assertEquals(
            "<!-- TOC start (generated with $BITDOWNTOC_URL) -->\n\n\n\n<!-- TOC end -->\n\n",
            BitGenerator.generate("", Params(generateAnchors = false))
        )
    }

    @Test
    fun testNoHeadingUnderPlaceholder() {
        val input = """
        # Some readme
        
        [TOC]
        """.trimIndent()

        assertEquals(
            """
            # Some readme
            
            <!-- TOC start (generated with $BITDOWNTOC_URL) -->
            
            
            
            <!-- TOC end -->
           """.trimIndent(),
            BitGenerator.generate(input)
        )
    }

    @Test
    fun testOneShot() {
        val input = """
        # Some readme
        ## hello
        """.trimIndent()

        assertEquals(
            """
            - [Some readme](#some-readme)
              * [hello](#hello)
            
            <a name="some-readme"></a>
            # Some readme
            <a name="hello"></a>
            ## hello
           """.trimIndent(),
            BitGenerator.generate(input, Params(oneShot = true))
        )
    }

    @Test
    fun testExistingToc() {
        val input = """
            <!-- TOC start (generated with $BITDOWNTOC_URL) -->
            
            - [heading](#heading)
            
            <!-- TOC end -->
            # heading
            """.trimIndent()

        assertEquals(
            input,
            BitGenerator.generate(input, Params(generateAnchors = false))
        )
    }

    @Test
    fun testExistingTocChangeCommentStyle() {
        val input = """
            <!-- TOC start -->
            
            - [heading](#heading)
            
            <!-- TOC end -->
            <!-- TOC --> <a name="heading"></a>
            # heading
            """.trimIndent()

        assertEquals(
            """
            {%- # TOC start (generated with $BITDOWNTOC_URL) -%}
            
            - [heading](#heading)
            
            {%- # TOC end -%}
            {%- # TOC -%}<a name="heading"></a>
            # heading
            """.trimIndent(),
            BitGenerator.generate(input, Params(generateAnchors = true, commentStyle = LIQUID))
        )
    }

    @Test
    fun testMaxLevel() {
        val input = """
            # h1
            ## h2
            ### h3
            #### h4
            ##### h5
            """.trimIndent()

        assertEquals(
            """
            <!-- TOC start (generated with $BITDOWNTOC_URL) -->
            
            - [h1](#h1)
              * [h2](#h2)
            
            <!-- TOC end -->
            
            <!-- TOC --><a name="h1"></a>
            # h1
            <!-- TOC --><a name="h2"></a>
            ## h2
            ### h3
            #### h4
            ##### h5
            """.trimIndent(),
            BitGenerator.generate(input, Params(generateAnchors = true, maxLevel = 2))
        )
    }


    @Test
    fun testOnlyFirstTOCMarkerIsReplaced() {
        val input = """
            [TOC]
            # h1
            Use [TOC] to control the toc:
            ```
            [TOC]
            ```
            """.trimIndent()

        val output = """
            <!-- TOC start (generated with $BITDOWNTOC_URL) -->
            
            - [h1](#h1)
            
            <!-- TOC end -->
            # h1
            Use [TOC] to control the toc:
            ```
            [TOC]
            ```
            """.trimIndent()

        assertEquals(
            output,
            BitGenerator.generate(input, Params(generateAnchors = false))
        )
        assertEquals(
            output,
            BitGenerator.generate(output, Params(generateAnchors = false))
        )
    }

    @Test
    fun testTocMarkerWithExistingTocWorks() {
        val input = """
            <!-- TOC start (generated with $BITDOWNTOC_URL) -->
            
            - [h1](#h1)
            
            <!-- TOC end -->
            # h1
            Use [TOC] to control the toc:
            ```
            [TOC]
            ```
            """.trimIndent()

        assertEquals(
            input,
            BitGenerator.generate(input, Params(generateAnchors = false))
        )
    }


    private fun assertDoesNotChangeToc(body: String) {
        val input = "## heading 1\n${body}\n## heading 2"

        assertEquals(
            """
            <!-- TOC start (generated with $BITDOWNTOC_URL) -->
        
            - [heading 1](#heading-1)
            - [heading 2](#heading-2)
            
            <!-- TOC end -->
            """.trimIndent() + "\n\n" + input,
            BitGenerator.generate(input, Params(generateAnchors = false))
        )
    }
}
