package ch.derlin.bitdowntoc

import java.io.File

fun main() {
    BitGenerator.generate(File("out.md").readText()).let {
        File("out1.md").writeText(it)
    }
}