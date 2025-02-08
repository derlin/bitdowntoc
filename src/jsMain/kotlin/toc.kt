import ch.derlin.bitdowntoc.AnchorAlgorithm
import ch.derlin.bitdowntoc.BitException
import ch.derlin.bitdowntoc.BitGenerator
import ch.derlin.bitdowntoc.BitOption
import ch.derlin.bitdowntoc.BitOptions
import ch.derlin.bitdowntoc.BitProfiles
import ch.derlin.bitdowntoc.CommentStyle
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLOptionElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.get

val DEFAULT_INPUT_MARKDOWN = """
# Paste Your Document In Here

[TOC]

## And a table of contents

will be generated

## On   the right

side of this page.

## Use the [TOC]

placeholder to control where the TOC will appear
""".trim()

external class CodeMirror(element: HTMLElement, options: dynamic) {
    fun on(even: String, receiver: (CodeMirror) -> Unit)
    fun setOption(key: String, value: String) // e.g. setOption("theme", "midnight")
    fun getValue(): String
    fun setValue(text: String)
}

fun HTMLElement.initEditor(): CodeMirror = CodeMirror(this, js("({ mode: 'markdown', lineWrapping: true })"))

class TocHandler(
    tocInputElement: HTMLElement,
    tocOutputElement: HTMLElement,
    optionsDiv: HTMLElement,
    btnCopy: HTMLElement,
    btnGenerate: HTMLElement,
    selectProfile: HTMLElement,
    btnStoreOptions: HTMLElement,
    btnResetOptions: HTMLElement,
    val checkbokTocOnly: HTMLInputElement,
    val divWarnings: HTMLElement,
) {

    val tocInput = tocInputElement.initEditor().also { it.setValue(DEFAULT_INPUT_MARKDOWN) }
    val tocOutput = tocOutputElement.initEditor()

    init {
        optionsDiv.innerHTML = generateOptions()
        selectProfile.appendChild(createSelectProfile())
        loadOptions()
        btnCopy.addOnClickListener { copyTocToClipboard() }
        btnGenerate.addOnClickListener { generate() }
        btnStoreOptions.addOnClickListener { storeOptions() }
        btnResetOptions.addOnClickListener { resetOptions() }
        checkbokTocOnly.addOnClickListener { generate() }

        tocInput.on("change") { generate() }
    }

    private fun generate() {
        val params = getParams()
        val tocOnly = checkbokTocOnly.checked

        // show warnings if any param incompatibility
        divWarnings.innerHTML = BitGenerator.getWarnings(params, tocOnly)
            ?.joinToString { """<div class="warn">$it</div>""" } ?: ""

        // generate, or show an error in the output
        val output = try {
            BitGenerator.generate(tocInput.getValue(), params, tocOnly)
        } catch (e: BitException) {
            "\nError!\n${e.message}"
        }.also {
            console.log("returned $it")
        }
        tocOutput.setValue(output)
    }

    private fun copyTocToClipboard() {
        window.navigator.clipboard.writeText(tocOutput.getValue()).then {
            console.log("copied text to clipboard")
        }.catch {
            console.error("Failed to copy to clipboard!", it)
        }
    }

    fun createSelectProfile(): HTMLSelectElement {
        val select = (document.createElement("select") as HTMLSelectElement)
        select.id = "profile"
        select.classList.add("ph-no-capture")
        select.innerHTML = BitProfiles.values().joinToString("") { profile ->
            """<option value="${profile.name}">${profile.displayName}</option>"""
        }
        select.addEventListener("change", { BitProfiles.valueOf(select.value).apply(); generate() })
        return select
    }
}


fun getSelectProfile(): HTMLSelectElement =
    (document.getElementById("profile") as HTMLSelectElement)

fun generateOptions(): String =
    BitOptions.list.map { it.toHtml() }.joinToString("") { "<div>$it</div>" }

fun storeOptions() {
    BitOptions.list.forEach { bitOption ->
        localStorage.setItem(bitOption.id, bitOption.getValue().toString())
    }
    localStorage.setItem("profile", getSelectProfile().value)
    console.log("options stored")
}

fun resetOptions() {
    BitOptions.list.forEach { bitOption ->
        localStorage.removeItem(bitOption.id)
        bitOption.setValue(bitOption.default.toString())
    }
    localStorage.removeItem("profile")
    getSelectProfile().reset()
    console.log("options reset")
}

fun loadOptions() {
    BitOptions.list.forEach { bitOption ->
        (localStorage.getItem(bitOption.id))?.let { value ->
            bitOption.setValue(value)
        }
    }
    localStorage.getItem("profile")?.let { getSelectProfile().value = it }
    console.log("options saved")
}

fun getParams() = BitGenerator.Params(
    indentChars = BitOptions.indentChars.getValue(),
    indentSpaces = BitOptions.indentSpaces.getValue(),
    generateAnchors = BitOptions.generateAnchors.getValue(),
    anchorsPrefix = BitOptions.anchorsPrefix.getValue(),
    commentStyle = BitOptions.commentStyle.getValue(),
    trimTocIndent = BitOptions.trimTocIndent.getValue(),
    concatSpaces = BitOptions.concatSpaces.getValue(),
    anchorAlgorithm = BitOptions.anchorAlgorithm.getValue(),
    oneShot = BitOptions.oneShot.getValue(),
    maxLevel = BitOptions.maxLevel.getValue()
)

fun BitProfiles.apply() {
    this.overriddenBitOptions().forEach { it.setValue(it.default.toString()) }
}

fun HTMLSelectElement.reset() {
    this.value = (options[0] as HTMLOptionElement).value
}

@Suppress("UNCHECKED")
fun <T> BitOption<T>.getValue(): T = when (default) {
    is Boolean -> (document.getElementById(id) as HTMLInputElement).checked
    is String, is Int -> (document.getElementById(id) as HTMLInputElement).value
    is CommentStyle -> (document.getElementById(id) as HTMLSelectElement).value.let { CommentStyle.valueOf(it) }
    is AnchorAlgorithm -> (document.getElementById(id) as HTMLSelectElement).value.let { AnchorAlgorithm.valueOf(it) }
    else -> throw RuntimeException("unsupported bit option type")
} as T

fun <T> BitOption<T>.setValue(value: String) = when (default) {
    is Boolean -> (document.getElementById(id) as? HTMLInputElement)?.checked = value.toBoolean()
    is String, is Int -> (document.getElementById(id) as? HTMLInputElement)?.value = value
    is CommentStyle -> (document.getElementById(id) as? HTMLSelectElement)?.value = value
    is AnchorAlgorithm -> (document.getElementById(id) as? HTMLSelectElement)?.value = value
    else -> throw RuntimeException("unsupported bit option type")
}

fun BitOption<*>.toHtml(): String {
    val input = when (default) {
        is Boolean -> """<input id ="$id" type="checkbox" ${if (default) "checked=checked" else ""} />"""
        is Int -> """<input type="number" id="$id" value ="$default" step="1" min="-1" max="100"  />"""
        is String -> """<input id="$id" value ="$default" size="12" />"""
        is CommentStyle ->
            """<select id="$id">""" + CommentStyle.values().joinToString("") {
                """<option value="${it.name}">${it.name}</option>"""
            } + "</select>"

        is AnchorAlgorithm ->
            """<select id="$id">""" + AnchorAlgorithm.values().joinToString("") {
                """<option value="${it.name}">${it.name}</option>"""
            } + "</select>"

        else -> throw RuntimeException("unsupported bit option type")
    }

    return """
        $input
        <label for="$id">$name</label>
        <span class="help">$help</span>
        """.trimIndent()
}
