package com.taccardi.zak.card_deck

import com.taccardi.zak.card_deck.presentation.deal_cards.CardsRecycler
import com.taccardi.zak.card_deck.presentation.deal_cards.CardsRecycler.Item.UiCard
import com.taccardi.zak.card_deck.presentation.deal_cards.CardsRecycler.Item.UiDeck
import com.taccardi.zak.library.pojo.Card
import com.taccardi.zak.library.pojo.Rank
import com.taccardi.zak.library.pojo.Suit
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Test for [CardsRecycler.Item]
 */
class ItemTest {

    @Test
    fun runTests() {
        runTest(
                old = Card(Rank.KING, Suit.CLUBS).toUi(),
                new = Card(Rank.KING, Suit.CLUBS).toUi(),
                sameContents = true,
                sameItem = true
        )

        runTest(
                old = Card(Rank.KING, Suit.CLUBS).toUi(),
                new = Card(Rank.QUEEN, Suit.CLUBS).toUi(),
                sameContents = false,
                sameItem = false
        )

        runTest(
                old = UiDeck,
                new = Card(Rank.QUEEN, Suit.CLUBS).toUi(),
                sameContents = false,
                sameItem = false
        )

        runTest(
                old = UiDeck,
                new = UiDeck,
                sameContents = true,
                sameItem = true
        )

    }

    fun runTest(old: CardsRecycler.Item, new: CardsRecycler.Item, sameContents: Boolean, sameItem: Boolean) {
        assertThat(old.isContentSame(new))
                .isEqualTo(sameContents)

        assertThat(old.isItemSame(new))
                .isEqualTo(sameItem)


        //test symmetry
        assertThat(new.isContentSame(old))
                .isEqualTo(sameContents)

        assertThat(new.isItemSame(old))
                .isEqualTo(sameItem)
    }


    fun Card.toUi() = UiCard(this)

}