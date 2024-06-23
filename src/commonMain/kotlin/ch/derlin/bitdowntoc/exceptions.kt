package ch.derlin.bitdowntoc

open class BitException(override val message: String) : RuntimeException(message)

class MissingTocEndException : BitException("The document has a TOC start, but is missing a TOC end.")
