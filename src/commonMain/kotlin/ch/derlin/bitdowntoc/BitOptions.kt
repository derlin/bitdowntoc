package ch.derlin.bitdowntoc

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
        maxLevel,
        concatSpaces,
        trimTocIndent,
        oneShot
    )
}