package com.taccardi.zak.card_deck.presentation.deal_cards

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.filters.MediumTest
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Test to evaluate quantity strings are rendered correctly.
 */
@RunWith(Parameterized::class)
@MediumTest
class QuantityStringsText(val cardsRemaining: Int, val expected: String) {

    lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getTargetContext()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "remaining cards: {0} | expected: {1}")
        fun data(): Collection<Array<Any>> {
            return listOf(
                    arrayOf(0, "Deck is empty. Build a new one"),
                    arrayOf(1, "Tap deck to deal.\n1 card remains"),
                    arrayOf(2, "Tap deck to deal.\n2 cards remain")
            )
        }
    }

    @Test
    fun test() {
        Assertions.assertThat(
                context.remainingCardsHint(count = cardsRemaining)
        )
                .isEqualTo(expected)
    }
}