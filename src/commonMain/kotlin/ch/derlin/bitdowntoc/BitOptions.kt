package ch.derlin.bitdowntoc

import ch.derlin.bitdowntoc.CommentStyle.HTML
import ch.derlin.bitdowntoc.CommentStyle.LIQUID

internal const val BITDOWNTOC_URL = "https://github.com/derlin/bitdowntoc"

enum class BitProfiles(val displayName: String) {
    GENERIC("Generic"),
    GITHUB("GitHub"),
    GITLAB17("Gitlab 17+"),
    GITLAB("Gitlab"),
    HASHNODE("hashnode"),
    DEVTO("dev.to");

    companion object {
        fun BitGenerator.Params.defaults() =
            copy(
                generateAnchors = false,
                concatSpaces = true,
                anchorAlgorithm = AnchorAlgorithm.DEFAULT,
                commentStyle = HTML,
                anchorsPrefix = ""
            )
    }

    fun applyToParams(params: BitGenerator.Params): BitGenerator.Params = when (this) {
        GENERIC -> params.defaults().copy(generateAnchors = true)
        GITLAB -> params.defaults()
        GITLAB17, GITHUB -> params.defaults().copy(concatSpaces = false)
        DEVTO -> params.defaults().copy(anchorAlgorithm = AnchorAlgorithm.DEVTO, commentStyle = LIQUID)
        HASHNODE -> params.defaults()
            .copy(anchorAlgorithm = AnchorAlgorithm.HASHNODE, anchorsPrefix = "heading-")
    }

    fun overriddenBitOptions(): List<BitOption<*>> {
        fun optionsList(
            anchors: Boolean = false,
            concatSpaces: Boolean = true,
            anchorAlgorithm: AnchorAlgorithm = AnchorAlgorithm.DEFAULT,
            commentStyle: CommentStyle = HTML,
            anchorsPrefix: String = "",
        ) =
            listOf(
                BitOptions.generateAnchors.copy(default = anchors),
                BitOptions.concatSpaces.copy(default = concatSpaces),
                BitOptions.anchorAlgorithm.copy(default = anchorAlgorithm),
                BitOptions.commentStyle.copy(default = commentStyle),
                BitOptions.anchorsPrefix.copy(default = anchorsPrefix)
            )

        return when (this) {
            GENERIC -> optionsList(anchors = true)
            GITLAB -> optionsList()
            GITLAB17, GITHUB -> optionsList(concatSpaces = false)
            DEVTO -> optionsList(anchorAlgorithm = AnchorAlgorithm.DEVTO, commentStyle = LIQUID)
            HASHNODE -> optionsList(
                anchorAlgorithm = AnchorAlgorithm.HASHNODE,
                concatSpaces = true,
                anchorsPrefix = "heading-"
            )
        }
    }
}

data class BitOption<T>(val id: String, val name: String, val default: T, val help: String)

object BitOptions {
    val indentChars = BitOption(
        "indent-chars", "indent characters", "-*+",
        "Characters used for indenting the toc"
    )
    val indentSpaces = BitOption(
        "indent-spaces", "indent spaces", 3,
        "Number of spaces per indentation level for indenting the toc"
    )
    val generateAnchors = BitOption(
        "anchors", "generate anchors", true,
        "Whether to generate anchors below headings (e.g. BitBucket Server)"
    )
    val anchorAlgorithm = BitOption(
        "anchors-algo", "algorithm used to generate anchors", AnchorAlgorithm.DEFAULT,
        "How handle special chars, links, etc. in titles before generating anchor links"
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
        "Maximum heading level to include to the toc (< 1 means no limit)"
    )

    val trimTocIndent = BitOption(
        "trim-toc", "trim toc indent", true,
        "Whether to indent TOC based on the registered headings, or based on the actual heading levels"
    )
    val concatSpaces = BitOption(
        "concat-spaces", "concat spaces", true,
        "Whether to trim heading spaces in generated links (foo-bar) or not (foo----bar)"
    )
    val oneShot = BitOption(
        "oneshot", "oneshot", false,
        "Whether to add comments so bitdowntoc can regenerate/update the toc and anchors (false) or not (true)"
    )

    val list: Array<BitOption<*>> = arrayOf(
        indentChars,
        indentSpaces,
        generateAnchors,
        anchorAlgorithm,
        anchorsPrefix,
        maxLevel,
        concatSpaces,
        trimTocIndent,
        commentStyle,
        oneShot
    )
}
