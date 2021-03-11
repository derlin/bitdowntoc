package ch.derlin.bitdowntoc

data class TocEntry(val indent: Int, val title: String, val link: String)

class Toc(val concatSpaces: Boolean = false) {
    internal val links: MutableMap<String, Int> = mutableMapOf()
    internal val entries: MutableList<TocEntry> = mutableListOf()

    fun addTocEntry(indent: Int, title: String): TocEntry {
        var link = title.escapeTitle()
        // add numbers at the end of the link if it is a duplicate
        val duplicates = links[link]?.also { link += "-${it}" } ?: 0
        links[link] = duplicates + 1
        // create the entry
        val entry = TocEntry(indent - 1, title, link)
        entries += entry
        return entry
    }

    fun generateToc(indentCharacters: List<Char>, trimTocIndent: Boolean): String {
        val minIndent = if (trimTocIndent) entries.minOf { it.indent } else 0
        return entries.joinToString(System.lineSeparator()) { (indent, text, link) ->
            (indent - minIndent).let {
                " ".repeat(it * 2) + "${indentCharacters[it % indentCharacters.size]} [$text]($link)"
            }
        }
    }

    private fun String.escapeTitle() = this
        .trim()
        .replace("\\s".toRegex(), "-")
        .replace("[^\\p{IsLatin}\\d_-]".toRegex(), "")
        .let { if (concatSpaces) it.replace("--+".toRegex(), "-") else it }
        .toLowerCase()
}