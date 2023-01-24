package ch.derlin.bitdowntoc

import ch.derlin.bitdowntoc.AnchorAlgorithm.DEFAULT
import ch.derlin.bitdowntoc.AnchorAlgorithm.DEVTO
import kotlin.test.Test
import kotlin.test.assertEquals

class AnchorsGeneratorTest {

    @Test
    fun testStripHtml() {
        mapOf(
            "< This is a test" to "< This is a test",
            "< hello world >" to "",
            "<hello> <world>" to " ",
            """<div>x y z</div> <a href="https://hello" style="xxx">link</a> <x>""" to "x y z link "
        ).forEach { (input, expected) ->
            assertEquals(expected, input.stripHtmlTags())
        }
    }

    @Test
    fun testStripMarkdownLinks() {
        mapOf(
            "[ This is a test ](" to "[ This is a test ](",
            "[]()" to "",
            "[hello]()" to "hello",
            "[hello](world!)" to "hello",
            "[h E l l O ](https://whatever.com/eoa#xxx?query=param)" to "h E l l O ",
        ).forEach { (input, expected) ->
            assertEquals(expected, input.stripMarkdownLinks())
        }
    }

    @Test
    fun testAlgorithms() {
        listOf(
            listOf(
                " table-of Content! \uD83D\uDE03 `code` \uD83D\uDE06 \uD83D\uDE0A \uD83D\uDD4A ☮ ✌ ☕",
                "table-of-content-code-",
                "table-of-content--code------",
                "tableof-content-raw-code-endraw-\uD83D\uDD4A-☮-✌",
            ),
            listOf(
                "Check out [bitdowntoc](https://bitdowntoc.ch),   It is *awesome*!",
                "check-out-bitdowntoc-it-is-awesome",
                "check-out-bitdowntoc---it-is-awesome",
                "check-out-bitdowntoc-it-is-awesome",
            ),
            listOf(
                "'Hello' means ˮBonjourˮ en français héhé â",
                "hello-means-ˮbonjourˮ-en-français-héhé-â",
                "hello-means-ˮbonjourˮ-en-français-héhé-â",
                "hello-means-ˮbonjourˮ-en-français-héhé-â"
            ),
            listOf(
                "' ' ʻ ՚ Ꞌ ꞌ ′ ″ ‴ 〃 \" ˮ",
                "-ʻ-ꞌ-ꞌ-ˮ",
                "--ʻ--ꞌ-ꞌ------ˮ",
                "-ʻ-ꞌ-ꞌ-ˮ"
            ),
            listOf(
                "`<hello href=\"test-hello\">` <hello> `&%£`",
                "hello-hreftest-hello-",
                "hello-hreftest-hello--",
                "-raw-lthello-hreftesthellogt-endraw-raw-amp£-endraw-"
            ),
            listOf(
                "Check out [this AWESOME REPO ❣](https://github.com/derlin/bitdowntoc) (@derlin)  ",
                "check-out-this-awesome-repo-derlin",
                "check-out-this-awesome-repo--derlin",
                "check-out-this-awesome-repo-❣-derlin"
            )
        ).forEach { (input, gitlab, github, devto) ->
            assertEquals(gitlab, DEFAULT.toAnchor(input))
            assertEquals(github, DEFAULT.toAnchor(input, concatSpaces = false))
            assertEquals(devto, DEVTO.toAnchor(input))
        }
    }
}
