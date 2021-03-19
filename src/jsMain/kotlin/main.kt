import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLTextAreaElement

fun main() {
    window.onload = {
        Toggler(
            wrapperDiv = getById("div-reveal-options"),
            button = getById("btn-toggle-options")
        )
        console.log(getById("md"))
        TocHandler(
            tocInputElement = getById("md") as HTMLTextAreaElement,
            tocOutputElement = getById("toc") as HTMLTextAreaElement,
            btnCopy = getById("btn-copy"),
            optionsDiv = getById("options"),
            btnGenerate = getById("btn-generate"),
            btnStoreOptions = getById("btn-store-options"),
            btnResetOptions = getById("btn-reset-options")
        )
        Modal(
            modal = getById("about-modal"),
            showBtn = getById("btn-show-modal"),
            closeBtn = getById("btn-close-modal")
        )
    }
}

fun getById(id: String): HTMLElement =
    document.getElementById(id) as? HTMLElement ?: throw RuntimeException("No element with '$id' in doc")

fun HTMLElement.addOnClickListener(callback: () -> Unit) =
    this.addEventListener("click", { callback() })

class Toggler(val wrapperDiv: HTMLElement, button: HTMLElement) {
    private val cls = "hide"

    init {
        button.addEventListener("click", { toggle() })
    }

    private fun toggle() {
        with(wrapperDiv.classList) {
            if (contains(cls)) remove(cls) else add(cls)
        }
    }
}

class Modal(val modal: HTMLElement, showBtn: HTMLElement, closeBtn: HTMLElement) {
    init {
        showBtn.addOnClickListener { show() }
        closeBtn.addOnClickListener { hide() }
        window.addEventListener("click", {
            if (it.target == modal) hide()
        })
    }

    fun show() {
        modal.style.display = "block"
    }

    fun hide() {
        modal.style.display = "none"
    }
}

