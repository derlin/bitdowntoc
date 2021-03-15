import ch.derlin.bitdowntoc.BitGenerator
import ch.derlin.bitdowntoc.BitOption
import ch.derlin.bitdowntoc.BitOptions
import org.w3c.dom.Document
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement

@ExperimentalJsExport
@JsExport
fun generateToc(
    text: String,
    indentCharacters: String = BitOptions.indentChars.default,
    generateAnchors: Boolean = BitOptions.generateAnchors.default,
    trimTocIndent: Boolean = BitOptions.trimTocIndent.default,
    concatSpaces: Boolean = BitOptions.concatSpaces.default,
    oneShot: Boolean = BitOptions.oneShot.default
): String =
    BitGenerator.generate(
        text,
        indentCharacters = indentCharacters,
        generateAnchors = generateAnchors,
        trimTocIndent = trimTocIndent,
        concatSpaces = concatSpaces,
        oneShot = oneShot
    )

@ExperimentalJsExport
@JsExport
fun generateOptions(optionsDiv: HTMLElement) {
    optionsDiv.innerHTML = BitOptions.list
        .map { it.toHtml() }
        .map { "<div>$it</div>" }
        .joinToString("")
}

@ExperimentalJsExport
@JsExport
fun readOptions(doc: Document) = """
    indent ${BitOptions.indentChars.getValue(doc)}
    generateAnchors ${BitOptions.generateAnchors.getValue(doc)}
    trimTocIndent ${BitOptions.trimTocIndent.getValue(doc)}
    concatSpaces ${BitOptions.concatSpaces.getValue(doc)}
    oneShot = ${BitOptions.oneShot.getValue(doc)}
    """

@ExperimentalJsExport
@JsExport
fun generateFromOptions(doc: Document, text: String) =
    BitGenerator.generate(
        text,
        indentCharacters = BitOptions.indentChars.getValue(doc),
        generateAnchors = BitOptions.generateAnchors.getValue(doc),
        trimTocIndent = BitOptions.trimTocIndent.getValue(doc),
        concatSpaces = BitOptions.concatSpaces.getValue(doc),
        oneShot = BitOptions.oneShot.getValue(doc)
    )


fun BitOption<Boolean>.getValue(doc: Document): Boolean = (doc.getElementById(id) as HTMLInputElement).checked
fun BitOption<String>.getValue(doc: Document): String = (doc.getElementById(id) as HTMLInputElement).value

fun BitOption<*>.toHtml(): String {
    val input = when (default) {
        true, false -> """<input id ="$id" type="checkbox" ${if (default as Boolean) "checked=checked" else ""} />"""
        else -> """<input id="$id" value ="$default" />"""
    }

    return """
        $input
        <label for="$id">$name</label>
        <span class="help">$help</span>
        """.trimIndent()
}
