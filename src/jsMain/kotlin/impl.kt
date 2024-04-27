package ch.derlin.bitdowntoc

actual fun String.removeDiacritics(): String =
    this.normalize().replace("\\p{Diacritic}", "", ignoreCase = true)

fun String.normalize(): String = asDynamic().normalize("NFD")