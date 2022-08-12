package com.example.cosc345project.scrapertests

import com.example.cosc345.scraper.Matcher
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Matcher tests")
class MatcherUnitTest {
    @Test
    @DisplayName("Test matcher")
    fun `Test matcher`() {
        runBlocking {
            Matcher().run()
        }
    }
}