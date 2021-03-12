import ch.derlin.bitdowntoc.BitGenerator

@ExperimentalJsExport
@JsExport
fun generateToc(
    text: String,
    indentCharacters: String = "-*+",
    generateAnchors: Boolean = true,
    trimTocIndent: Boolean = true,
    concatSpaces: Boolean = true,
    oneShot: Boolean = false
): String =
    BitGenerator.generate(
        text,
        indentCharacters = indentCharacters,
        generateAnchors = generateAnchors,
        trimTocIndent = trimTocIndent,
        concatSpaces = concatSpaces,
        oneShot = oneShot
    )