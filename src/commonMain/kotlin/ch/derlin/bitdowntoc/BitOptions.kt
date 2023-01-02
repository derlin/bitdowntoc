package ch.derlin.bitdowntoc

import ch.derlin.bitdowntoc.CommentStyle.HTML
import ch.derlin.bitdowntoc.CommentStyle.LIQUID

enum class BitProfiles(val displayName: String) {
    BITBUCKET("BitBucket (server)"),
    GITHUB("GitHub"),
    GITLAB("Gitlab"),
    DEVTO("dev.to");

    fun applyToParams(params: BitGenerator.Params): BitGenerator.Params = when (this) {
        BITBUCKET -> params.copy(generateAnchors = true, concatSpaces = true, commentStyle = HTML)
        GITLAB -> params.copy(generateAnchors = false, concatSpaces = true, commentStyle = HTML)
        GITHUB -> params.copy(generateAnchors = false, concatSpaces = false, commentStyle = HTML)
        DEVTO -> params.copy(generateAnchors = true, concatSpaces = true, commentStyle = LIQUID)
    }

    fun overriddenBitOptions(): List<BitOption<*>> {
        fun optionsList(anchors: Boolean, concatSpaces: Boolean, commentStyle: CommentStyle) =
            listOf(
                BitOptions.generateAnchors.copy(default = anchors),
                BitOptions.concatSpaces.copy(default = concatSpaces),
                BitOptions.commentStyle.copy(default = commentStyle))

        return when (this) {
            BITBUCKET -> optionsList(anchors = true, concatSpaces = true, commentStyle = HTML)
            GITLAB -> optionsList(anchors = false, concatSpaces = true, commentStyle = HTML)
            GITHUB -> optionsList(anchors = false, concatSpaces = false, commentStyle = HTML)
            DEVTO -> optionsList(anchors = false, concatSpaces = true, commentStyle = LIQUID)
        }
    }
}

data class BitOption<T>(val id: String, val name: String, val default: T, val help: String)

object BitOptions {
    val indentChars = BitOption(
        "indent-chars", "indent characters", "-*+",
        "Characters used for indenting the toc"
    )
    val generateAnchors = BitOption(
        "anchors", "generate anchors", true,
        "Whether to generate anchors below headings (BitBucket Server)"
    )
    val anchorsPrefix = BitOption(
        "anchors-prefix", "anchors prefix", "",
        "Prefix added to all anchors and TOC links (e.g. 'heading-')"
    )
    val commentStyle = BitOption(
        "comment-style", "comment style", HTML,
        "Language to use for generating comments around TOC and anchors"
    )

    val maxLevel = BitOption(
        "max-level", "Max indent level", -1,
        "Maximum heading level to include to the toc (< 1 means no limit)."
    )

    val trimTocIndent = BitOption(
        "trim-toc", "trim toc indent", true,
        "Whether to indent TOC based on the registered headings, or based on the actual heading levels"
    )
    val concatSpaces = BitOption(
        "concat-spaces", "concat spaces", true,
        "Whether to trim heading spaces in generated links (GitLab style) or not (GitHub style)"
    )
    val oneShot = BitOption(
        "oneshot", "oneshot", false,
        "Whether to add comments so this tool can regenerate/update the toc and anchors (false) or not (true)."
    )

    val list: Array<BitOption<*>> = arrayOf(
        indentChars,
        generateAnchors,
        anchorsPrefix,
        maxLevel,
        concatSpaces,
        trimTocIndent,
        commentStyle,
        oneShot
    )
}
