package ch.derlin.bitdowntoc

import ch.derlin.bitdowntoc.AnchorAlgorithm.DEFAULT


class Toc(
    val concatSpaces: Boolean = false,
    levelBoundaries: Pair<Int, Int>? = null,
    val anchorsGenerator: AnchorAlgorithm = DEFAULT,
    val anchorsPrefix: String = "",
) {

    internal val links: MutableMap<String, Int> = mutableMapOf()
    internal val entries: MutableList<TocEntry> = mutableListOf()
    internal val levelBoundaries = levelBoundaries ?: Pair(0, Int.MAX_VALUE)

    data class TocEntry(val indent: Int, val title: String, val link: String)

    internal fun shouldBeAdded(indent: Int): Boolean =
        levelBoundaries.let { (min, max) -> indent in min..max }

    fun addTocEntry(indent: Int, title: String): TocEntry? {
        return if (!shouldBeAdded(indent)) null else {
            var link = anchorsPrefix + anchorsGenerator.toAnchor(title, concatSpaces = concatSpaces)
            // add numbers at the end of the link if it is a duplicate
            val linkCount = links[link] ?: 0
            links[link] = linkCount + 1
            if (linkCount > 0) {
                link += "-$linkCount"
            }
            // create the entry
            val entry = TocEntry(indent - 1, anchorsGenerator.escapeTitle(title), link)
            entries += entry
            entry
        }
    }

    fun generateToc(indentCharacters: String, trimTocIndent: Boolean): String {
        if (this.entries.isEmpty()) return ""
        val minIndent = if (trimTocIndent) entries.minOf { it.indent } else 0
        return entries.joinToString("\n") { (indent, text, link) ->
            (indent - minIndent).let {
                " ".repeat(it * 3) + "${indentCharacters[it % indentCharacters.length]} [$text](#$link)"
            }
        }
    }
}
