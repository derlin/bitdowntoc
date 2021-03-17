package ch.derlin.bitdowntoc

import kotlin.test.*

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
            BitGenerator.generate(input, generateAnchors = false)
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
            BitGenerator.generate(input, generateAnchors = false)
        )
    }


    @Test
    fun testNoText() {
        assertEquals(
            "<!-- TOC start -->\n\n<!-- TOC end -->\n",
            BitGenerator.generate("", generateAnchors = false)
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
    fun testExistingToc() {
        val input = """
            <!-- TOC start -->
            - [heading](#heading)
            <!-- TOC end -->
            # heading
            """.trimIndent()

        assertEquals(
            input,
            BitGenerator.generate(input, generateAnchors = false)
        )
    }
}