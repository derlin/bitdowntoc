package ch.derlin.bitdowntoc

const val TEST_INPUT_FILE: String = "input.md"
const val TEST_OUTPUT_FILE: String = "output.md"
const val TOC_GITLAB: String = "toc-gitlab.md"
const val TOC_GITHUB: String = "toc-github.md"

internal fun output() = TEST_OUTPUT_FILE.load()
internal fun outputGitlab() = TOC_GITLAB.load() + "\n" + TEST_INPUT_FILE.load()
internal fun outputGithub() = TOC_GITHUB.load() + "\n" + TEST_INPUT_FILE.load()

internal fun String.load(): String =
    GenerateFromFileTest::class.java.classLoader.getResource(this)!!.readText()

internal fun String.getPath(): String =
    GenerateFromFileTest::class.java.classLoader.getResource(this)!!.path
