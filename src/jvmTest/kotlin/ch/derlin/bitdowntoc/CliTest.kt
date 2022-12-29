package ch.derlin.bitdowntoc

import assertk.assertThat
import assertk.assertions.matches
import org.junit.jupiter.api.Test

class CliTest {

    @Test
    fun `version flag works`() {
        assertThat(versionMessage())
            .matches(".*\n  commit\\.id=[\\da-f]{40}\n.*".toRegex(RegexOption.DOT_MATCHES_ALL))
    }
}
