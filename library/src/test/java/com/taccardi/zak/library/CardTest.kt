package com.taccardi.zak.library

import com.taccardi.zak.library.pojo.Card
import com.taccardi.zak.library.pojo.Rank
import com.taccardi.zak.library.pojo.Suit
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Simple equality tests for [Card]. This could be improved with paramaterized tests
 */
class CardTest {

    @Test
    fun testEquality() {
        val first = Card(Rank.ACE, Suit.HEARTS)
        val second = Card(first.rank, first.suit)

        assertThat(first)
                .isEqualTo(second)
    }

    @Test
    fun testInequalitySuit() {
        val first = Card(Rank.ACE, Suit.HEARTS)
        val second = Card(first.rank, Suit.SPADES)

        assertThat(first)
                .isNotEqualTo(second)
    }

    @Test
    fun testInequalityRank() {
        val first = Card(Rank.ACE, Suit.HEARTS)
        val second = Card(Rank.EIGHT, first.suit)

        assertThat(first)
                .isNotEqualTo(second)
    }

    @Test
    fun testInequalityRankAndSuit() {
        val first = Card(Rank.ACE, Suit.HEARTS)
        val second = Card(Rank.EIGHT, Suit.DIAMONDS)

        assertThat(first)
                .isNotEqualTo(second)
    }

    @Test
    fun testIdIsSame() {
        val first = Card(Rank.ACE, Suit.HEARTS)
        val second = Card(first.rank, first.suit)

        assertThat(first.id)
                .isEqualTo(second.id)
    }
}