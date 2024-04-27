package ch.derlin.bitdowntoc

import ch.derlin.bitdowntoc.AnchorAlgorithm.DEFAULT
import ch.derlin.bitdowntoc.AnchorAlgorithm.DEVTO
import ch.derlin.bitdowntoc.AnchorAlgorithm.HASHNODE
import kotlin.test.Test
import kotlin.test.assertEquals

class AnchorsGeneratorTest {

    @Test
    fun testRemoveBoldsAndItalics() {
        mapOf(
            "__hello__ _world_" to "hello world",
            "__hello__ __monde__" to "hello monde",
            "a __b__ c" to "a b c",
            "_x_ __y__ z" to "x y z",
            "__hello_" to "__hello_",
            "__ test _ x __\t__" to "__ test _ x __\t__",
        ).forEach { (input, expected) ->
            assertEquals(expected, input.removeUnderscoreBoldAndItalics())
        }
    }

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
                "table-of-content-code"
            ),
            listOf(
                "Check out [bitdowntoc](https://bitdowntoc.ch),   It is *awesome*!",
                "check-out-bitdowntoc-it-is-awesome",
                "check-out-bitdowntoc---it-is-awesome",
                "check-out-bitdowntoc-it-is-awesome",
                "check-out-bitdowntochttpsbitdowntocch-it-is-awesome",
            ),
            listOf(
                "'Hello' means ˮBonjourˮ en français héhé â",
                "hello-means-ˮbonjourˮ-en-français-héhé-â",
                "hello-means-ˮbonjourˮ-en-français-héhé-â",
                "hello-means-ˮbonjourˮ-en-français-héhé-â",
                "hello-means-bonjour-en-francais-hehe-a",
            ),
            listOf(
                "' ' ʻ ՚ Ꞌ ꞌ ′ ″ ‴ 〃 \" ˮ",
                "-ʻ-ꞌ-ꞌ-ˮ",
                "--ʻ--ꞌ-ꞌ------ˮ",
                "-ʻ-ꞌ-ꞌ-ˮ",
                "" // NOTE: hashnode will generate random anchors when empty
            ),
            listOf(
                "`<hello href=\"test-hello\">` <hello> `&%£`",
                "hello-hreftest-hello-",
                "hello-hreftest-hello--",
                "-raw-lthello-hreftesthellogt-endraw-raw-amp£-endraw-",
                "amp"
            ),
            listOf(
                "Check out [this AWESOME REPO ❣](https://github.com/derlin/bitdowntoc) (@derlin)  ",
                "check-out-this-awesome-repo-derlin",
                "check-out-this-awesome-repo--derlin",
                "check-out-this-awesome-repo-❣-derlin",
                "check-out-this-awesome-repo-httpsgithubcomderlinbitdowntoc-derlin"
            ),
            listOf(
                "Some __bold__ ? and _i_ t __alic__ _",
                "some-bold-and-i-t-alic-_",
                "some-bold--and-i-t-alic-_",
                "some-bold-and-i-t-alic-",
                "some-bold-and-i-t-alic"
            )
        ).forEach { (input, gitlab, github, devto, hashnode) ->
            assertEquals(gitlab, DEFAULT.toAnchor(input))
            assertEquals(github, DEFAULT.toAnchor(input, concatSpaces = false))
            assertEquals(devto, DEVTO.toAnchor(input))
            // the anchor prefix "heading-" is added after!.
            assertEquals(hashnode, HASHNODE.toAnchor(input))
        }
    }
}
