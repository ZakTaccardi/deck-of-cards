package com.taccardi.zak.library

import com.taccardi.zak.library.pojo.Deck
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
    fun dealt_card_is_equal_to_previous_top_card() {
        val deck = Deck.FRESH_DECK
        val deckWithDealtCard = deck.withDealtCard()

        assertThat(deckWithDealtCard.lastCardDealt)
                .isNotNull()
                .describedAs("Expected dealt card to be equal to the top card before it was dealt.")
                .isEqualTo(deck.topCard)
    }

    @Test
    fun deck_has_one_less_card_after_dealing() {
        val deck = Deck.FRESH_DECK
        val deckWithDealtCard = deck.withDealtCard()

        assertThat(deckWithDealtCard.remaining.size)
                .isNotNull()
                .describedAs("Expected dealt deck to have one less card after dealing")
                .isEqualTo(deck.remaining.size - 1)
    }

    @Test
    fun deck_has_one_more_card_after_dealing() {
        val deck = Deck.FRESH_DECK
        val deckWithDealtCard = deck.withDealtCard()

        assertThat(deckWithDealtCard.dealt.size)
                .isNotNull()
                .describedAs("Expected dealt deck to have more card dealt after dealing")
                .isEqualTo(deck.dealt.size + 1)
    }


    @Test
    fun deal_card_is_top_card_of_dealt_cards() {
        val deck = Deck.FRESH_DECK.withDealtCard()
        //deal 2 cards
        val deckWithDealtCard = deck.withDealtCard()

        assertThat(deckWithDealtCard.lastCardDealt)
                .isNotNull()
                .describedAs("Expected dealt card to be inserted at the top of the list of dealt cards")
                .isEqualTo(deck.topCard)
    }

    @Test
    fun empty_deck_deals_no_card_and_same_instance_is_returned() {
        val deck = Deck.EVERY_CARD_DEALT
        //deal 1 card
        val deckWithDealtCard = deck.withDealtCard()

        assertThat(deckWithDealtCard)
                .describedAs("Expected same instance of empty deck returned")
                .isSameAs(deck)
    }

    @Test
    fun shuffled_deck_has_same_size_as_previous_deck() {
        val deck = Deck.FRESH_DECK.dealCards(20)
        val shuffledDeck = deck.toShuffled()

        assertThat(shuffledDeck.remaining.size)
                .describedAs("Shuffled deck should have same size as previous deck")
                .isEqualTo(deck.remaining.size)

    }

    @Test
    fun shuffled_deck_should_have_same_remaining_cards_after_shuffling() {
        val deck = Deck.FRESH_DECK.dealCards(20)
        val shuffledDeck = deck.toShuffled()

        assertThat(shuffledDeck.dealt)
                .describedAs("Shuffled deck's dealt cards should be no different after shuffling.")
                .isEqualTo(deck.dealt)
    }

    @Test
    fun shuffled_deck_should_have_same_dealt_cards_after_shuffling() {
        val deck = Deck.FRESH_DECK.dealCards(20)
        val shuffledDeck = deck.toShuffled()

        assertThat(shuffledDeck.dealt)
                .describedAs("Shuffled deck's dealt cards should be no different after shuffling.")
                .isEqualTo(deck.dealt)
    }

    @Test
    fun shuffled_deck_should_have_different_order_after_shuffling() {
        val deck = Deck.FRESH_DECK.dealCards(20)
        val shuffledDeck = deck.toShuffled()

        assertThat(shuffledDeck.remaining)
                .describedAs("Shuffled deck's remaining cards should be in a different order after shuffling.")
                .isNotEqualTo(deck.remaining)

        assertThat(shuffledDeck.remaining.size)
                .describedAs("Shuffled deck's remaining cards should have the same size after shuffling.")
                .isEqualTo(deck.remaining.size)
    }

    @Test
    fun shuffle() {
        val deck = Deck.FRESH_DECK
        val shuffledDeck = deck.toShuffled()

        assertThat(shuffledDeck.remaining)
                .describedAs("Shuffled deck's cards should be shuffled")
                .isNotEqualTo(deck.remaining)

    }


    fun Deck.dealCards(number: Int): Deck {
        var deck: Deck = this
        for (i in 1..number) {
            deck = deck.withDealtCard()
        }

        return deck

    }

}