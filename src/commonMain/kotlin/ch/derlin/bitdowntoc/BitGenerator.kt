package ch.derlin.bitdowntoc

object BitGenerator {

    private const val NL = "\n"

    private const val tocMarker = "[TOC]"

    private val headerRegex = Regex("(#+) +([^ ]+.*)")

    data class Params(
        val indentChars: String = BitOptions.indentChars.default,
        val maxLevel: Int = BitOptions.maxLevel.default,
        val generateAnchors: Boolean = BitOptions.generateAnchors.default,
        val anchorAlgorithm: AnchorAlgorithm = BitOptions.anchorAlgorithm.default,
        val anchorsPrefix: String = BitOptions.anchorsPrefix.default,
        val commentStyle: CommentStyle = BitOptions.commentStyle.default,
        val trimTocIndent: Boolean = BitOptions.trimTocIndent.default,
        val concatSpaces: Boolean = BitOptions.concatSpaces.default,
        val oneShot: Boolean = BitOptions.oneShot.default,
    )

    fun generate(text: String) = generate(text, Params())

    fun generate(text: String, params: Params): String {
        println("Generating TOC with params: $params")

        val levels = if (params.maxLevel > 0) Pair(0, params.maxLevel) else null
        val toc = Toc(
            concatSpaces = params.concatSpaces,
            levelBoundaries = levels,
            anchorsPrefix = params.anchorsPrefix,
            anchorsGenerator = params.anchorAlgorithm
        )
        val commenter: Commenter = if (params.oneShot) NoComment() else params.commentStyle

        val lines = text.lines().let {
            // add toc placeholder if not exist
            if (it.hasToc(commenter)) it else listOf(tocMarker, "") + it
        }.toMutableList()

        val iter = lines.listIterator()

        // consume text up to the toc marker
        loop@ while (iter.hasNext()) {
            val line = iter.next().trim()
            when {
                commenter.isTocStart(line) -> {
                    iter.consumeToc(params.commentStyle)
                    iter.set(tocMarker)
                    break@loop
                }

                tocMarker == line -> {
                    break@loop
                }
            }
        }

        while (iter.hasNext()) {
            val line = iter.next()
            when {
                line.trim().startsWith("```") -> iter.consumeCode("```")
                line.trim().startsWith("~~~") -> iter.consumeCode("~~~")
                commenter.isAnchor(line) -> iter.remove()
                else -> line.parseHeader(toc)?.let {
                    if (params.generateAnchors) {
                        iter.set(commenter.toAnchor(it.link))
                        iter.add(line)
                    }
                }
            }
        }

        val tocString = toc.generateToc(params.indentChars, params.trimTocIndent).let {
            if (params.oneShot) it else commenter.wrapToc(it).asText()
        }

        return lines.asText().replaceFirst(tocMarker, tocString)
    }


    private fun String.parseHeader(toc: Toc) =
        headerRegex.matchEntire(this)?.let {
            val indent = it.groupValues[1].length
            val title = it.groupValues[2]
            toc.addTocEntry(indent, title)
        }

    private fun Iterable<String>.hasToc(commenter: Commenter): Boolean =
        this.map { it.trim() }.any { it == tocMarker || commenter.isTocStart(it) }

    private fun MutableListIterator<String>.consumeToc(commenter: Commenter) {
        do {
            this.remove()
        } while (this.hasNext() && !commenter.isTocEnd(this.next()))
    }

    private fun Iterator<String>.consumeCode(codeMarker: String) {
        while (this.hasNext() && !this.next().trim().startsWith(codeMarker));
    }

    private fun Iterable<String>.asText() = this.joinToString(NL)

}
