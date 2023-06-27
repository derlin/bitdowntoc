import ch.derlin.bitdowntoc.BitGenerator
import ch.derlin.bitdowntoc.BitOption
import ch.derlin.bitdowntoc.BitOptions
import ch.derlin.bitdowntoc.BitProfiles
import ch.derlin.bitdowntoc.CommentStyle
import ch.derlin.bitdowntoc.AnchorAlgorithm
import kotlinx.browser.document
import kotlinx.browser.localStorage
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLOptionElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.get

class TocHandler(
    val tocInputElement: HTMLTextAreaElement,
    val tocOutputElement: HTMLTextAreaElement,
    optionsDiv: HTMLElement,
    btnCopy: HTMLElement,
    btnGenerate: HTMLElement,
    selectProfile: HTMLElement,
    btnStoreOptions: HTMLElement,
    btnResetOptions: HTMLElement,
) {
    init {
        optionsDiv.innerHTML = generateOptions()
        selectProfile.appendChild(createSelectProfile())
        loadOptions()
        btnCopy.addOnClickListener { copyTocToClipboard() }
        btnGenerate.addOnClickListener { generate() }
        btnStoreOptions.addOnClickListener { storeOptions() }
        btnResetOptions.addOnClickListener { resetOptions() }

        tocInputElement.addEventListener("change", { generate() })
    }

    private fun generate() {
        tocOutputElement.value = generate(tocInputElement.value)
    }

    private fun copyTocToClipboard() {
        tocOutputElement.select()
        document.execCommand("copy")
    }
}

fun createSelectProfile(): HTMLSelectElement {
    val select = (document.createElement("select") as HTMLSelectElement)
    select.id = "profile"
    select.classList.add("ph-no-capture")
    select.innerHTML = BitProfiles.values().joinToString("") { profile ->
        """<option value="${profile.name}">${profile.displayName}</option>"""
    }
    select.addEventListener("change", { BitProfiles.valueOf(select.value).apply() })
    return select
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

fun generate(text: String) =
    BitGenerator.generate(text, getParams())

fun getParams() = BitGenerator.Params(
    indentChars = BitOptions.indentChars.getValue(),
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
