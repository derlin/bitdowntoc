import ch.derlin.bitdowntoc.BitGenerator
import ch.derlin.bitdowntoc.BitOption
import ch.derlin.bitdowntoc.BitOptions
import kotlinx.browser.document
import kotlinx.browser.localStorage
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement

class TocHandler(
    val tocInputElement: HTMLTextAreaElement,
    val tocOutputElement: HTMLTextAreaElement,
    optionsDiv: HTMLElement,
    btnCopy: HTMLElement,
    btnGenerate: HTMLElement,
    btnStoreOptions: HTMLElement,
    btnResetOptions: HTMLElement
) {
    init {
        optionsDiv.innerHTML = generateOptions()
        loadOptions()
        btnCopy.addOnClickListener { copyTocToClipboard() }
        btnGenerate.addOnClickListener { generate() }
        btnStoreOptions.addOnClickListener { storeOptions() }
        btnResetOptions.addOnClickListener { resetOptions() }

        tocInputElement.addEventListener("change", { generate() })
    }

    private fun generate() {
        tocOutputElement.value = generateFromOptions(tocInputElement.value)
    }

    private fun copyTocToClipboard() {
        tocOutputElement.select()
        document.execCommand("copy")
    }
}

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


fun generateOptions(): String =
    BitOptions.list
        .map { it.toHtml() }
        .map { "<div>$it</div>" }
        .joinToString("")


fun storeOptions() {
    BitOptions.list.forEach { bitOption ->
        localStorage.setItem(bitOption.id, bitOption.getValue().toString())
    }
    console.log("options stored")
}

fun resetOptions() {
    BitOptions.list.forEach { bitOption ->
        localStorage.removeItem(bitOption.id)
        bitOption.setValue(bitOption.default.toString())
    }
    console.log("options reset")
}

fun loadOptions() {
    BitOptions.list.forEach { bitOption ->
        (localStorage.getItem(bitOption.id))?.let { value ->
            bitOption.setValue(value)
        }
    }
    console.log("options saved")
}


fun generateFromOptions(text: String) =
    BitGenerator.generate(
        text,
        indentCharacters = BitOptions.indentChars.getValue(),
        generateAnchors = BitOptions.generateAnchors.getValue(),
        trimTocIndent = BitOptions.trimTocIndent.getValue(),
        concatSpaces = BitOptions.concatSpaces.getValue(),
        oneShot = BitOptions.oneShot.getValue(),
        maxLevel = BitOptions.maxLevel.getValue()
    )

fun <T> BitOption<T>.getValue(): T = when (default) {
    is Boolean -> (document.getElementById(id) as HTMLInputElement).checked
    is String, is Int -> (document.getElementById(id) as HTMLInputElement).value
    else -> throw RuntimeException("unsupported bit option type")
} as T

fun <T> BitOption<T>.setValue(value: String) = when (default) {
    is Boolean -> (document.getElementById(id) as? HTMLInputElement)?.checked = value.toBoolean()
    is String, is Int -> (document.getElementById(id) as? HTMLInputElement)?.value = value
    else -> throw RuntimeException("unsupported bit option type")
}

fun BitOption<*>.toHtml(): String {
    val input = when (default) {
        is Boolean -> """<input id ="$id" type="checkbox" ${if (default) "checked=checked" else ""} />"""
        is Int -> """<input type="number" id="$id" value ="$default" step="1" min="-1" max="100"  />"""
        is String -> """<input id="$id" value ="$default" size="6" />"""
        else -> throw RuntimeException("unsupported bit option type")
    }

    return """
        $input
        <label for="$id">$name</label>
        <span class="help">$help</span>
        """.trimIndent()
}
