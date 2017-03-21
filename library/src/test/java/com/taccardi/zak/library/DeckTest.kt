package com.taccardi.zak.library

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Tests for [Deck]
 */
class DeckTest {
    @Test
    fun calculateSizeStandard52Deck() {
        val actualCount = Deck.create()
        val expected = 52

        assertThat(actualCount)
                .describedAs("The number of cards in a standard 52 card deck should be...$expected")
                .isEqualTo(expected)
    }
}