import ch.derlin.bitdowntoc.BitGenerator
import ch.derlin.bitdowntoc.BitOption
import ch.derlin.bitdowntoc.BitOptions
import ch.derlin.bitdowntoc.BitProfiles
import kotlinx.browser.document
import kotlinx.browser.localStorage
import org.w3c.dom.*

class TocHandler(
    val tocInputElement: HTMLTextAreaElement,
    val tocOutputElement: HTMLTextAreaElement,
    optionsDiv: HTMLElement,
    btnCopy: HTMLElement,
    btnGenerate: HTMLElement,
    selectProfile: HTMLElement,
    btnStoreOptions: HTMLElement,
    btnResetOptions: HTMLElement
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
    select.innerHTML = BitProfiles.values().joinToString { profile ->
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
    trimTocIndent = BitOptions.trimTocIndent.getValue(),
    concatSpaces = BitOptions.concatSpaces.getValue(),
    oneShot = BitOptions.oneShot.getValue(),
    maxLevel = BitOptions.maxLevel.getValue()
)

fun BitProfiles.apply() {
    this.overriddenBitOptions().forEach { it.setValue(it.default.toString()) }
}

fun HTMLSelectElement.reset() {
    this.value = (options[0] as HTMLOptionElement).value
}

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
