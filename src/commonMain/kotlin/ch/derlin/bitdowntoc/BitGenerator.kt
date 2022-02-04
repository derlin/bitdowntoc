package ch.derlin.bitdowntoc

object BitGenerator {

    private const val NL = "\n"

    private const val tocMarker = "[TOC]"
    private const val tocStart = "<!-- TOC start -->"
    private const val tocEnd = "<!-- TOC end -->"

    private const val anchorPrefix = "<!-- TOC -->"
    private const val sectionMarkerFmt = """<a name=".*"></a>"""

    private val headerRegex = Regex("(#+) +([^ ]+.*)")
    private val codeRegex = Regex("^```\\w* *$")
    private val sectionMarkerRegex = Regex("$anchorPrefix *<a name=\".*\"></a> *")

    data class Params(
        val indentChars: String = BitOptions.indentChars.default,
        val maxLevel: Int = BitOptions.maxLevel.default,
        val generateAnchors: Boolean = BitOptions.generateAnchors.default,
        val trimTocIndent: Boolean = BitOptions.trimTocIndent.default,
        val concatSpaces: Boolean = BitOptions.concatSpaces.default,
        val oneShot: Boolean = BitOptions.oneShot.default,
    )

    fun generate(text: String) = generate(text, Params())

    fun generate(text: String, params: Params): String {

        val levels = if (params.maxLevel > 0) Pair(0, params.maxLevel) else null
        val toc = Toc(concatSpaces = params.concatSpaces, levelBoundaries = levels)

        val lines = text.lines().let {
            // add toc placeholder if not exist
            if (it.hasToc()) it else listOf(tocMarker) + it
        }.toMutableList()

        val iter = lines.listIterator()
        val anchorFmt = (if (params.oneShot) "" else anchorPrefix) + sectionMarkerFmt


        // consume text up to the toc marker
        loop@ while (iter.hasNext()) {
            when (iter.next().trim()) {
                tocStart -> {
                    iter.consumeToc()
                    break@loop
                }
                tocMarker -> break@loop

            }
        }

        while (iter.hasNext()) {
            val line = iter.next()
            when {
                codeRegex.matches(line) -> iter.consumeCode()
                sectionMarkerRegex.matches(line) -> iter.remove()
                else -> line.parseHeader(toc)?.let {
                    if (params.generateAnchors) {
                        // use replace here, since String.format is not available
                        iter.set(anchorFmt.replace(".*", it.link))
                        iter.add(line)
                    }
                }
            }
        }

        val tocString = toc.generateToc(params.indentChars, params.trimTocIndent).let {
            if (!params.oneShot) {
                listOf(tocStart, it, tocEnd).asText()
            } else {
                it
            }
        }

        return lines.map {
            if (it == tocMarker) tocString else it
        }.asText()
    }


    private fun String.parseHeader(toc: Toc) =
        headerRegex.matchEntire(this)?.let {
            val indent = it.groupValues[1].length
            val title = it.groupValues[2]
            toc.addTocEntry(indent, title)
        }

    private fun Iterable<String>.hasToc(): Boolean =
        this.map { it.trim() }.any { it in listOf(tocMarker, tocStart) }

    private fun MutableListIterator<String>.consumeToc() {
        do {
            this.remove()
        } while (this.hasNext() && this.next() != tocEnd)
        this.set(tocMarker)
    }

    private fun Iterator<String>.consumeCode() {
        while (this.hasNext() && !codeRegex.matches(this.next()));
    }

    private fun Iterable<String>.asText() = this.joinToString(NL)

}
