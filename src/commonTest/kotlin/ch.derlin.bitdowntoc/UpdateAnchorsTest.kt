package ch.derlin.bitdowntoc

import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateAnchorsTest {

    @Test
    fun testUpdateAnchors() {

        val input = """
        # Some readme
        
        <!-- TOC start (generated with some-url) -->
        
        - [heading](#heading)
          * [subheading](#subheading)
        - [heading](#heading-1)
        
        <!-- TOC end -->
        
        ## modified heading
        <!-- TOC --><a name="heading"></a>

        ### sub heading different
        <!-- TOC --><a name="subheading"></a>

        ## Hello World !
        <!-- TOC --><a name="heading-1"></a>
        blabla
        """.trimIndent()

        assertEquals(
            """
            # Some readme
            
            <!-- TOC start (generated with $BITDOWNTOC_URL) -->
            
            - [modified heading](#modified-heading)
              * [sub heading different](#sub-heading-different)
            - [Hello World !](#hello-world-)
            
            <!-- TOC end -->
            
            <!-- TOC --><a name="modified-heading"></a>
            ## modified heading
    
            <!-- TOC --><a name="sub-heading-different"></a>
            ### sub heading different
    
            <!-- TOC --><a name="hello-world-"></a>
            ## Hello World !
            blabla
            """.trimIndent(),
            BitGenerator.generate(input)
        )
    }

    @Test
    fun testRemoveAnchors() {

        val input = """
        # Some readme
        
        <!-- TOC start -->
        
        - [heading](#heading)
          * [subheading](#subheading)
        - [heading](#heading-1)
        
        <!-- TOC end -->
        
        <!-- TOC --><a name="heading"></a>
        ## modified heading
        
        <!-- TOC --><a name="subheading"></a>
        ### sub heading different
        
        <!-- TOC --><a name="heading-1"></a>
        ## Hello World !
        """.trimIndent()

        assertEquals(
            """
            # Some readme
            
            <!-- TOC start (generated with $BITDOWNTOC_URL) -->
            
            - [modified heading](#modified-heading)
              * [sub heading different](#sub-heading-different)
            - [Hello World !](#hello-world-)
            
            <!-- TOC end -->
            
            ## modified heading
            
            ### sub heading different
            
            ## Hello World !
            """.trimIndent(),
            BitGenerator.generate(input, BitGenerator.Params(generateAnchors = false))
        )
    }

    @Test
    fun testLeaveNonMarkedAnchors() {

        val input = """
        ## H1
        <a name="heading"></a>
        """.trimIndent()

        assertEquals(
            """
            <!-- TOC start (generated with $BITDOWNTOC_URL) -->
            
            - [H1](#h1)
            
            <!-- TOC end -->
            
            ## H1
            <a name="heading"></a>
            """.trimIndent(),
            BitGenerator.generate(input, BitGenerator.Params(generateAnchors = false))
        )
    }
}
