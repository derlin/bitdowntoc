package ch.derlin.bitdowntoc


class Toc(val concatSpaces: Boolean = false) {
    internal val links: MutableMap<String, Int> = mutableMapOf()
    internal val entries: MutableList<TocEntry> = mutableListOf()

    data class TocEntry(val indent: Int, val title: String, val link: String)

    fun addTocEntry(indent: Int, title: String): TocEntry {
        var link = escapeTitle(title)
        // add numbers at the end of the link if it is a duplicate
        val linkCount = links[link] ?: 0
        links[link] = linkCount + 1
        if (linkCount > 0) {
            link += "-$linkCount"
        }
        // create the entry
        val entry = TocEntry(indent - 1, title, link)
        entries += entry
        return entry
    }

    fun generateToc(indentCharacters: String, trimTocIndent: Boolean): String {
        if (this.entries.isEmpty()) return ""
        val minIndent = if (trimTocIndent) entries.minOf { it.indent } else 0
        return entries.joinToString("\n") { (indent, text, link) ->
            (indent - minIndent).let {
                " ".repeat(it * 2) + "${indentCharacters[it % indentCharacters.length]} [$text](#$link)"
            }
        }
    }

    private fun escapeTitle(title: String) = title
        .trim()
        .replace(spacesRegex, "-")
        .replace(nonLatinCharacters, "")
        .let { if (concatSpaces) it.replace(concatRegex, "-") else it }
        .toLowerCase()

    companion object {
        val spacesRegex = Regex("\\s")
        val concatRegex = Regex("--+")
        val nonLatinCharacters = // this should be [^\\p{IsLatin}\\d_-], but it does only work in JVM
            Regex("[^A-Za-z\\u00C0-\\u00D6\\u00D8-\\u00f6\\u00f8-\\u00ff\\d_-]")
    }
}