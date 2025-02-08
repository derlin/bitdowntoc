package ch.derlin.bitdowntoc

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.matches
import com.github.ajalt.clikt.testing.test
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files

class CliTest {

    @Test
    fun `version flag works`() {
        assertThat(versionMessage())
            .matches(".*\n  commit\\.id=[\\da-f]{40}\n.*".toRegex(RegexOption.DOT_MATCHES_ALL))
    }

    @Test
    fun `invalid arguments`() {
        assertFailWith("", "Missing argument PATH")
        assertFailWith("-i -", "--inplace", "incompatible", "stdin")
        assertFailWith("${TEST_INPUT_FILE.getPath()} -o output.md -i", "mutually exclusive")
        assertFailWith("input.md", "doesn't exist")
        assertFailWith(".", "not a file")
    }

    @Test
    fun `basic command`() {
        val result = Cli().test(TEST_INPUT_FILE.getPath())
        assertThat(result.statusCode).isEqualTo(0)
        assertThat(result.stdout).isEqualTo(output() + System.lineSeparator())
        assertThat(result.stderr).isEmpty()
    }

    @Test
    fun `use profile`() {
        val result = Cli().test("${TEST_INPUT_FILE.getPath()} -p github")
        assertThat(result.statusCode).isEqualTo(0)
        assertThat(result.stdout).isEqualTo(outputGithub() + System.lineSeparator())
        assertThat(result.stderr).isEmpty()
    }

    @Test
    fun `read from stdin`() {
        val result = Cli(readFromStdin = { TEST_INPUT_FILE.load() }).test("-")
        assertThat(result.statusCode).isEqualTo(0)
        assertThat(result.stdout).isEqualTo(output() + System.lineSeparator())
        assertThat(result.stderr).isEmpty()
    }

    @Test
    fun `TOC only with warnings`() {
        val result = Cli().test("--toc-only --anchors --no-concat-spaces ${TEST_INPUT_FILE.getPath()}")
        assertThat(result.statusCode).isEqualTo(0)
        assertThat(result.stdout).isEqualTo(TOC_GITHUB.load())
        assertThat(result.stderr).contains("This TOC requires anchors to exist in the markdown content")
    }

    @Test
    fun `output to file`() {
        Files.createTempDirectory("bt").let { "$it/output.md" }.let { outFile ->
            val result = Cli().test("${TEST_INPUT_FILE.getPath()} -o $outFile")
            assertThat(result.statusCode).isEqualTo(0)
            assertThat(result.stdout).isEmpty()
            assertThat(result.stderr).isEmpty()
            assertThat(File(outFile).readText()).isEqualTo(output())
        }
    }

    private fun assertFailWith(args: String, vararg msg: String) {
        val result = Cli().test(args)
        assertThat(result.statusCode, name = args).isEqualTo(1)
        assertThat(result.stderr, name = args).contains(*msg)
    }
}
