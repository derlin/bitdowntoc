import ch.derlin.bitdowntoc.BitGenerator
import ch.derlin.bitdowntoc.BitOption
import ch.derlin.bitdowntoc.BitOptions
import kotlinx.browser.document
import kotlinx.browser.localStorage

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

    loadOptions()
}

@ExperimentalJsExport
@JsExport
fun storeOptions() {
    BitOptions.list.forEach { bitOption ->
        localStorage.setItem(bitOption.id, bitOption.getValue().toString())
    }
    console.log("options stored")
}

@ExperimentalJsExport
@JsExport
fun resetOptions() {
    BitOptions.list.forEach { bitOption ->
        localStorage.removeItem(bitOption.id)
        bitOption.setValue(bitOption.default.toString())
    }
    console.log("options reset")
}

@ExperimentalJsExport
@JsExport
fun loadOptions() {
    BitOptions.list.forEach { bitOption ->
        (localStorage.getItem(bitOption.id))?.let { value ->
            bitOption.setValue(value)
        }
    }
    console.log("options saved")
}

@ExperimentalJsExport
@JsExport
fun readOptions() = """
    indent ${BitOptions.indentChars.getValue()}
    generateAnchors ${BitOptions.generateAnchors.getValue()}
    trimTocIndent ${BitOptions.trimTocIndent.getValue()}
    concatSpaces ${BitOptions.concatSpaces.getValue()}
    oneShot = ${BitOptions.oneShot.getValue()}
    """

@ExperimentalJsExport
@JsExport
fun generateFromOptions(text: String) =
    BitGenerator.generate(
        text,
        indentCharacters = BitOptions.indentChars.getValue(),
        generateAnchors = BitOptions.generateAnchors.getValue(),
        trimTocIndent = BitOptions.trimTocIndent.getValue(),
        concatSpaces = BitOptions.concatSpaces.getValue(),
        oneShot = BitOptions.oneShot.getValue()
    )

fun <T> BitOption<T>.getValue(): T = when (default) {
    is Boolean -> (document.getElementById(id) as HTMLInputElement).checked
    is String -> (document.getElementById(id) as HTMLInputElement).value
    else -> throw RuntimeException("unsupported bit option type")
} as T

fun <T> BitOption<T>.setValue(value: String) = when (default) {
    is Boolean -> (document.getElementById(id) as? HTMLInputElement)?.checked = value.toBoolean()
    is String -> (document.getElementById(id) as? HTMLInputElement)?.value = value
    else -> throw RuntimeException("unsupported bit option type")
}

fun BitOption<*>.toHtml(): String {
    val input = when (default) {
        true, false -> """<input id ="$id" type="checkbox" ${if (default as Boolean) "checked=checked" else ""} />"""
        else -> """<input id="$id" value ="$default" size="6" />"""
    }

    return """
        $input
        <label for="$id">$name</label>
        <span class="help">$help</span>
        """.trimIndent()
}
