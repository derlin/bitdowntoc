package ch.derlin.bitdowntoc

import ch.derlin.bitdowntoc.Commenter.Companion.ANCHOR_FMT
import ch.derlin.bitdowntoc.Commenter.Companion.ANCHOR_LINK_PLACEHOLDER

interface Commenter {

    companion object {
        const val ANCHOR_LINK_PLACEHOLDER = ".*"
        const val ANCHOR_FMT = "<a name=\"$ANCHOR_LINK_PLACEHOLDER\"></a>"
    }

    fun wrapToc(toc: String): List<String>
    fun anchorStart(): String
    fun toAnchor(link: String) =
        // use replace here, since String.format is not available
        anchorStart() + ANCHOR_FMT.replace(ANCHOR_LINK_PLACEHOLDER, link)

    fun isTocStart(line: String): Boolean
    fun isTocEnd(line: String): Boolean
    fun isAnchor(line: String): Boolean

}

class NoComment : Commenter {
    override fun wrapToc(toc: String): List<String> = listOf(toc)
    override fun anchorStart(): String = ""

    override fun isTocStart(line: String): Boolean = false
    override fun isTocEnd(line: String): Boolean = false
    override fun isAnchor(line: String): Boolean = false
}

private const val COMMENT_TOC_START = "TOC start"
private const val COMMENT_TOC_END = "TOC end"
private const val COMMENT_ANCHOR = "TOC"

enum class CommentStyle(
    private val format: (String) -> String,
    private val formatForRegex: (String) -> String = format,
) : Commenter {
    HTML({ "<!-- $it -->" }),
    LIQUID({ "{%- # $it -%}" }, { "\\{%- # $it -%\\}" });

    private val tocEnd = format(COMMENT_TOC_END)
    private val anchorStart = format(COMMENT_ANCHOR)

    override fun wrapToc(toc: String): List<String> =
        listOf(format("$COMMENT_TOC_START (generated with $BITDOWNTOC_URL)"), "", toc, "", tocEnd)

    override fun anchorStart(): String = anchorStart

    override fun isTocStart(line: String): Boolean = values().any {
        Regex(it.formatForRegex("$COMMENT_TOC_START.*")).matches(line)
    }

    override fun isTocEnd(line: String): Boolean = values().any { it.tocEnd == line }

    override fun isAnchor(line: String): Boolean = values().any {
        line.startsWith(it.anchorStart) && line.contains(ANCHOR_FMT.substringBefore(ANCHOR_LINK_PLACEHOLDER))
    }
}
