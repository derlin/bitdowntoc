package ch.derlin.bitdowntoc

import java.text.Normalizer

actual fun String.removeDiacritics(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")