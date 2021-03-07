package ch.derlin.bitdowntoc

import java.io.File

fun main() {
    BitGenerator.generate("out.md").let {
        File("out1.md").writeText(it)
    }
}