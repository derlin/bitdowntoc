package ch.derlin.bitdowntoc

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.MordantHelpFormatter
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.eagerOption
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path
import java.io.File
import java.nio.file.Path
import java.util.*

private const val GIT_PROPERTIES_FILE = "git.properties"

internal fun ensure(condition: Boolean, msg: () -> String) {
    if (!condition) throw CliktError(msg())
}

class Cli(
    // easy way to override the behavior during tests
    private val readFromStdin: () -> String = { System.`in`.reader().readText() }
) : CliktCommand(name = "bitdowntoc") {

    init {
        context {
            helpFormatter = { MordantHelpFormatter(it, requiredOptionMarker = "*") }
        }
        eagerOption("--version", help = "Show version and exit") {
            throw PrintMessage(versionMessage())
        }
    }

    // arguments cannot be null, so use an empty string
    // Note: we cannot use an InputStream, because we need the filename for the --inplace option
    private val path: String by argument(help = "Markdown file, or '-' to read from stdin").default("")

    private val indentChars: String by BitOptions.indentChars.cliOption()
    private val indentSpaces: Int by BitOptions.indentSpaces.cliOptionInt()
    private val concatSpaces: Boolean by BitOptions.concatSpaces.cliOptionBool()
    private val anchorsPrefix: String by BitOptions.anchorsPrefix.cliOption()
    private val generateAnchors: Boolean by BitOptions.generateAnchors.cliOptionBool()
    private val anchorAlgorithm: AnchorAlgorithm by BitOptions.anchorAlgorithm.cliAlgoOption()
    private val commentStyle: CommentStyle by BitOptions.commentStyle.cliCommentOption()
    private val trimToIndent: Boolean by BitOptions.trimTocIndent.cliOptionBool()
    private val oneshot: Boolean by BitOptions.oneShot.cliOptionBool()
    private val maxLevel: Int by BitOptions.maxLevel.cliOptionInt()

    private val profile: BitProfiles? by option("-p", "--profile", help = "Load default options for a specific site")
        .enum<BitProfiles>()

    private val inplace: Boolean by option(
        "--inplace",
        "-i",
        help = "Overwrite input file. This is incompatible with reading from stdin"
    )
        .flag(default = false)

    private val outputFile: Path? by option(
        "-o",
        "--output-file",
        help = "Write the output to a file instead of stdout"
    )
        .path(mustExist = false, canBeDir = false)

    override fun run() {
        ensure(path.isNotBlank()) {
            "Missing argument PATH.\n  Use '-' for stdin."
        }

        // empty means stdin
        val inputFile = path
            .let { if (it == "-") null else File(it) }
            ?.also {
                ensure(it.exists()) { "Path $it doesn't exist." }
                ensure(it.isFile()) { "Path $it is not a file." }
            }

        ensure(!(inputFile == null && inplace)) {
            "--inplace is incompatible with reading from stdin"
        }
        ensure(!(inplace && outputFile != null)) {
            "--inplace and --output are mutually exclusive options"
        }

        val inputText = inputFile?.readText() ?: readFromStdin()
        val output = if (inplace) inputFile else outputFile?.toFile()
        val params = BitGenerator.Params(
            indentChars = indentChars,
            indentSpaces = indentSpaces,
            generateAnchors = generateAnchors,
            anchorAlgorithm = anchorAlgorithm,
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

        try {

            BitGenerator.generate(inputText, params).let {
                (output?.writeText(it)) ?: echo(it)
            }
        } catch (e: BitException) {
            throw CliktError("[Error] ${e.message}")
        }
    }

    private fun BitOption<String>.cliOption() = option("--$id", help = "$help (default: '$default')")
        .default(default)

    private fun BitOption<Int>.cliOptionInt() = option("--$id", help = "$help (default: '$default')")
        .int()
        .default(default)

    private fun BitOption<Boolean>.cliOptionBool() = option("--$id", help = "$help (default: $default)")
        .flag("--no-$id", default = default)

    private fun BitOption<CommentStyle>.cliCommentOption() = option("--$id", help = "$help (default: $default)")
        .enum<CommentStyle>(ignoreCase = true)
        .default(CommentStyle.HTML)

    private fun BitOption<AnchorAlgorithm>.cliAlgoOption() = option("--$id", help = "$help (default: $default)")
        .enum<AnchorAlgorithm>(ignoreCase = true)
        .default(AnchorAlgorithm.DEFAULT)

}

internal fun versionMessage(): String =
    with(Properties()) {
        // git.properties is generated on build by gradle (see gradle-git-properties plugin)
        load(Cli::class.java.classLoader.getResourceAsStream(GIT_PROPERTIES_FILE))
        "BitDownToc Version: ${getProperty("git.build.version")} (sha: ${getProperty("git.commit.id")}).\nDetails:\n" +
                this.entries.joinToString("\n") { (k, v) -> "  ${k.toString().substringAfter("git.")}=$v" }
    }


fun main(args: Array<String>) = Cli().main(args)
