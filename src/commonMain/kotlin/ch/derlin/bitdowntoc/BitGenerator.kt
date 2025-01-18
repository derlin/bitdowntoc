package ch.derlin.bitdowntoc

object BitGenerator {

    private const val NL = "\n"

    private const val tocMarker = "[TOC]"

    private val headerRegex = Regex("(#+) +([^ ]+.*)")
    private val codeStartTrimmer = Regex("^\\s*(?:(\\d+\\.)|[*+-])?\\s*")

    data class Params(
        val indentChars: String = BitOptions.indentChars.default,
        val indentSpaces: Int = BitOptions.indentSpaces.default,
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
        // TODO implement proper logging
        //println("Generating TOC with params: $params")

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

                else -> line.parseHeader(toc, ignore = true)
            }
        }

        while (iter.hasNext()) {
            val line = iter.next()
            val codeMarker = listOf('`', '~').firstNotNullOfOrNull { line.getCodeStart(it) }

            if (!codeMarker.isNullOrBlank()) iter.consumeCode(codeMarker)
            else if (commenter.isAnchor(line)) iter.remove()
            else {
                line.parseHeader(toc)?.let {
                    if (params.generateAnchors) {
                        iter.set(commenter.toAnchor(it.link))
                        iter.add(line)
                    }
                }
            }
        }

        val tocString = toc.generateToc(params.indentChars, params.indentSpaces, params.trimTocIndent).let {
            if (params.oneShot) it else commenter.wrapToc(it).asText()
        }

        return lines.asText().replaceFirst(tocMarker, tocString)
    }


    private fun String.parseHeader(toc: Toc, ignore: Boolean = false) =
        headerRegex.matchEntire(this)?.let {
            val indent = it.groupValues[1].length
            val title = it.groupValues[2]
            toc.addTocEntry(if (ignore) -1 else indent, title)
        }

    private fun Iterable<String>.hasToc(commenter: Commenter): Boolean =
        this.map { it.trim() }.any { it == tocMarker || commenter.isTocStart(it) }

    private fun MutableListIterator<String>.consumeToc(commenter: Commenter) {
        this.remove() // remove toc start
        while (this.hasNext()) {
            if (commenter.isTocEnd(this.next())) return
            this.remove()
        }
        throw MissingTocEndException()
    }

    private fun String.getCodeStart(char: Char): String? =
        codeStartTrimmer.replace(this, "")
            // We expect at least 3 for code start (```), but 4 is also supported.
            // See https://github.com/derlin/bitdowntoc/issues/65
            .takeIf { it.startsWith("$char".repeat(3)) }
            ?.let { trimmedLine -> trimmedLine.takeWhile { it == char } }


    private fun Iterator<String>.consumeCode(codeMarker: String) {
        while (this.hasNext() && !this.next().trim().startsWith(codeMarker));
    }

    private fun Iterable<String>.asText() = this.joinToString(NL)
}
