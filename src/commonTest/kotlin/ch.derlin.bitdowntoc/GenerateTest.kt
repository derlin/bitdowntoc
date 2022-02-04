package ch.derlin.bitdowntoc

import ch.derlin.bitdowntoc.BitGenerator.Params
import kotlin.test.Test
import kotlin.test.assertEquals

class GenerateTest {

    @Test
    fun testImplicitToc() {
        val input = "# heading"

        assertEquals(
            """
            <!-- TOC start -->
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
            
            <!-- TOC start -->
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
            
            <!-- TOC start -->
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
            "<!-- TOC start -->\n\n<!-- TOC end -->\n",
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
            
            <!-- TOC start -->
            
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
            <!-- TOC start -->
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
            <!-- TOC start -->
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
}