package com.taccardi.zak.library

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Tests for [Deck]
 */
class DeckTest {
    @Test
    fun calculateSizeStandard52Deck() {
        val actualCount = Deck.FRESH_DECK.remaining.size
        val expected = 52

        assertThat(actualCount)
                .describedAs("The number of cards in a standard 52 card deck should be...$expected")
                .isEqualTo(expected)
    }

    @Test
    fun dealCard() {
        val deck = Deck.FRESH_DECK
        val deckWithDealtCard = deck.withDealtCard()

        assertThat(deckWithDealtCard.lastCardDealt)
                .isNotNull()
                .describedAs("Expected dealt card to be equal to the top card before it was dealt.")
                .isEqualTo(deck.topCard)

        assertThat(deckWithDealtCard.remaining.size)
                .isNotNull()
                .describedAs("Expected dealt deck to have one less card after dealing")
                .isEqualTo(deck.remaining.size - 1)

        assertThat(deckWithDealtCard.dealt.size)
                .isNotNull()
                .describedAs("Expected dealt deck to have more card dealt after dealing")
                .isEqualTo(deck.dealt.size + 1)
    }

    @Test
    fun shuffle() {
        val deck = Deck.FRESH_DECK
        val shuffledDeck = deck.toShuffled()

        assertThat(shuffledDeck.remaining.size)
                .describedAs("Shuffled deck should have same size as previous deck")
                .isEqualTo(deck.remaining.size)

        assertThat(shuffledDeck.dealt)
                .describedAs("Shuffled deck's dealt cards should be no different after shuffling.")
                .isEqualTo(deck.dealt)

        assertThat(shuffledDeck.remaining)
                .describedAs("Shuffled deck's cards should be shuffled")
                .isNotEqualTo(deck.remaining)



    }
}