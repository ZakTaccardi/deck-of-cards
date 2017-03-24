package com.taccardi.zak.card_deck

import android.content.Context
import android.support.annotation.IdRes
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.v7.widget.RecyclerView
import com.taccardi.zak.card_deck.MyViewMatchers.withPartialText
import com.taccardi.zak.library.pojo.Card
import org.junit.Rule
import org.junit.Test


/**
 * Test for [DealCardsActivity]
 */
@LargeTest
class DealCardActivityTest {
    @Rule
    @JvmField var activityRule: ActivityTestRule<DealCardsActivity> = ActivityTestRule<DealCardsActivity>(DealCardsActivity::class.java)

    lateinit private var delegate: Delegate

    @Test
    fun no_cards_dealt() {
        delegate = Delegate(activityRule, DealCardsUi.State.NO_CARDS_DEALT)
    }

    @Test
    fun every_card_dealt() {
        delegate = Delegate(activityRule, DealCardsUi.State.EVERY_CARD_DEALT)
    }

    @Test
    fun number_of_cards_remaining() {
        var state = DealCardsUi.State.NO_CARDS_DEALT

        var deck = state.deck.withDealtCard()
        state = state.copy(deck = deck)

        assert(deck.remaining.size == 51)
        delegate = Delegate(activityRule, state)
    }

    /**
     * Runs the test
     * @param state state that will be rendered onto the view, and then verified that it is rendered properly
     */
    private class Delegate(
            private val rule: ActivityTestRule<DealCardsActivity>,
            state: DealCardsUi.State

    ) {
        val activity: DealCardsActivity by lazy { rule.activity }
        val ui by lazy { activity }
        val deckAndCards: ViewInteraction by lazy { onView(withId(R.id.cards_recycler)) }
        val recyclerMatcher by lazy { RecyclerViewMatcher(R.id.cards_recycler) }
        val firstCard: ViewInteraction get() = onView(recyclerMatcher.atPosition(FIRST_CARD_POSITION))
        val deck: ViewInteraction get() = onView(recyclerMatcher.atPosition(DECK_POSITION))
        val remaining: ViewInteraction get() = onView(withId(REMAINING_CARDS_HINT_ID))

        init {
            ui.render(state)
            assert(state)
        }

        private fun assert(state: DealCardsUi.State) {
            //checks cards
            if (state.dealt.isEmpty()) {
                firstCard.check(doesNotExist())
            } else {
                firstCard.isThe(state.dealt.first())
                state.dealt.forEachIndexed { index, card ->
                    getCard(position = index).isThe(card)
                }
            }


            ui.assertFirstItemIsDeck()


            ui.assertRemainingCards(expected = state.remaining)


        }

        private fun ViewInteraction.isThe(card: Card) {
            val rank = card.rank.intDef.toString()
            val suit = card.suit.symbol
            this.check(matches(hasDescendant(withPartialText(rank))))
            this.check(matches(hasDescendant(withPartialText(suit))))
        }

        //1 would be first card, 2 would be second, etc
        fun getCard(position: Int): ViewInteraction {
            val adjustedForDeck = position + FIRST_CARD_POSITION
            //scroll to correct position first (or view won't be visible)
            deckAndCards.perform(scrollToPosition<RecyclerView.ViewHolder>(adjustedForDeck))
            return onView(recyclerMatcher.atPosition(adjustedForDeck))
        }

        fun DealCardsUi.assertRemainingCards(expected: Int) {
            remaining.check(matches(isDisplayed()))
            if (expected != 0) {
                remaining.check(matches(withPartialText(expected.toString())))
            } else {
                remaining.check(matches(withPartialText("empty")))

            }
        }

        fun DealCardsUi.assertFirstItemIsDeck() {
            deckAndCards.perform(scrollToPosition<RecyclerView.ViewHolder>(DECK_POSITION))
            deck.check(matches(isDisplayed()))
            deck.check(matches(
                    hasDescendant(withId(DECK_ICON_ID))
            ))
        }

        companion object {
            val DECK_POSITION = 0
            val FIRST_CARD_POSITION = DECK_POSITION + 1
            @IdRes val DECK_ICON_ID = R.id.dealCardsUi_deck_icon
            @IdRes val REMAINING_CARDS_HINT_ID = R.id.dealCardsUi_cardsRemaining_textView
        }

    }
}
