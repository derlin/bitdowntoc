package ch.derlin.bitdowntoc

import kotlin.test.*

class GenerateTest {

    @Test
    fun testMandatoryToc() {
        val input = """
                # heading
            """.trimIndent()

        val message = assertFails { BitGenerator.generate(input) }.message
        assertNotNull(message)
        assertTrue(message.contains("This library requires a [TOC] placeholder"))
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
            BitGenerator.generate(input),
            """
            # Some readme
            
            <!-- TOC start -->
            - [heading](#heading)
              * [subheading](#subheading)
            - [heading](#heading-1)
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
    fun testGenerateWithoutAnchors() {

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

        assertEquals(
            BitGenerator.generate(input, generateAnchors = false),
            """
            # Some readme
            
            <!-- TOC start -->
            - [heading](#heading)
              * [subheading](#subheading)
            - [heading](#heading-1)
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
    fun testNoHeading() {
        val input = """
        # Some readme
        
        [TOC]
        """.trimIndent()

        assertEquals(
            BitGenerator.generate(input, generateAnchors = false),
            """
            # Some readme
            
            <!-- TOC start -->
            
            <!-- TOC end -->
           """.trimIndent()
        )
    }
}