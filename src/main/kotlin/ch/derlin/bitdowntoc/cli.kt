package ch.derlin.bitdowntoc

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import java.nio.file.Path

class Cli : CliktCommand() {

    init {
        context { helpFormatter = CliktHelpFormatter(showRequiredTag = true) }
    }

    private val inputFile: Path by argument(help = "Input Markdown File")
        .path(mustExist = true, canBeDir = false)

    private val generateAnchors: Boolean by option("--anchors", help = "Whether to generate anchors (<a>)")
        .flag("--no-anchors", default = true)

    private val outputFile: Path? by option("-o", "--output-file")
        .path(mustExist = false, canBeDir = false)

    override fun run() {
        val inputText = inputFile.toFile().readText()
        BitGenerator.generate(inputText, GeneratorOptions(generateAnchors = generateAnchors)).let {
            (outputFile?.toFile()?.writeText(it)) ?: println(it)
        }
    }
}

fun main(args: Array<String>) = Cli().main(args)