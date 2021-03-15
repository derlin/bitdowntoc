package ch.derlin.bitdowntoc

data class BitOption<T>(val id: String, val name: String, val default: T, val help: String)

object BitOptions {
    val indentChars = BitOption(
        "indentChars", "indent characters", "-*+",
        "Characters used for indenting the toc"
    )
    val generateAnchors = BitOption(
        "anchors", "generate anchors", true,
        "Whether to generate anchors below headings (BitBucket Server)"
    )
    val trimTocIndent = BitOption(
        "trimToc", "trim toc indent", true,
        "Whether to indent TOC based on the registered headings, or based on the actual heading levels"
    )
    val concatSpaces = BitOption(
        "concatSpaces", "concat spaces", true,
        "Whether to trim heading spaces in generated links. Use true for GitLab style, false for GitHub style"
    )
    val oneShot = BitOption(
        "oneshot", "oneshot", false,
        "Whether to add comments so this tool can regenerate/update the toc and anchors (false) or not (true)."
    )

    val list = arrayOf(
        indentChars,
        generateAnchors,
        trimTocIndent,
        concatSpaces,
        oneShot
    )
}