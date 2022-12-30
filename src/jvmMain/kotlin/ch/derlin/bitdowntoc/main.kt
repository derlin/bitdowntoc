package ch.derlin.bitdowntoc

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.eagerOption
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path
import java.nio.file.Path
import java.util.Properties


private const val GIT_PROPERTIES_FILE = "git.properties"

class Cli : CliktCommand() {

    init {
        context { helpFormatter = CliktHelpFormatter(showRequiredTag = true) }
        eagerOption("--version", help = "Show version and exit") {
            throw PrintMessage(versionMessage())
        }
    }

    private val inputFile: Path by argument(help = "Input Markdown File")
        .path(mustExist = true, canBeDir = false)

    private val indentChars: String by BitOptions.indentChars.cliOption()
    private val concatSpaces: Boolean by BitOptions.concatSpaces.cliOption()
    private val anchorsPrefix: String by BitOptions.anchorsPrefix.cliOption()
    private val generateAnchors: Boolean by BitOptions.generateAnchors.cliOption()
    private val commentStyle: CommentStyle by BitOptions.commentStyle.cliEnumOption()
    private val trimToIndent: Boolean by BitOptions.trimTocIndent.cliOption()
    private val oneshot: Boolean by BitOptions.oneShot.cliOption()
    private val maxLevel: Int by BitOptions.maxLevel.cliOptionInt()

    private val profile: BitProfiles? by option("-p", "--profile", help = "Load default options for a specific site")
        .enum<BitProfiles>()

    private val inplace: Boolean by option("--inplace", "-i", help = "Overwrite input file")
        .flag(default = false)

    private val outputFile: Path? by option("-o", "--output-file", help = "Write the output to a file instead of the console")
        .path(mustExist = false, canBeDir = false)

    override fun run() {
        require(!(inplace && outputFile != null)) {
            "--inplace and --output are mutually exclusive options"
        }

        val inputText = inputFile.toFile().readText()
        val output = if (inplace) inputFile else outputFile
        val params = BitGenerator.Params(
            indentChars = indentChars,
            generateAnchors = generateAnchors,
            anchorsPrefix = anchorsPrefix,
            commentStyle = commentStyle,
            concatSpaces = concatSpaces,
            trimTocIndent = trimToIndent,
            oneShot = oneshot,
            maxLevel = maxLevel
        ).let {
            // override with the profile options if any
            profile?.applyToParams(it) ?: it
        }

        BitGenerator.generate(inputText, params).let {
            (output?.toFile()?.writeText(it)) ?: println(it)
        }
    }

    private fun BitOption<String>.cliOption() = option("--$id", help = "$help (default: '$default')")
        .default(default)

    private fun BitOption<Int>.cliOptionInt() = option("--$id", help = "$help (default: '$default')")
        .int()
        .default(default)

    private fun BitOption<Boolean>.cliOption() = option("--$id", help = "$help (default: $default)")
        .flag("--no-$id", default = default)

    private fun BitOption<CommentStyle>.cliEnumOption() = option("--$id", help = "$help (default: $default)")
        .enum<CommentStyle>(ignoreCase = false)
        .default(CommentStyle.HTML)

}

internal fun versionMessage(): String =
    with(Properties()) {
        // git.properties is generated on build by gradle (see gradle-git-properties plugin)
        load(Cli::class.java.classLoader.getResourceAsStream(GIT_PROPERTIES_FILE))
        "BitDownToc Version: ${getProperty("git.build.version")} (sha: ${getProperty("git.commit.id")}).\nDetails:\n" +
                this.entries.joinToString("\n") { (k, v) -> "  ${k.toString().substringAfter("git.")}=$v" }
    }


fun main(args: Array<String>) = Cli().main(args)
