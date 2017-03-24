package com.taccardi.zak.card_deck

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.matcher.ViewMatchers.hasDescendant
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.v7.widget.RecyclerView
import com.taccardi.zak.card_deck.MyViewMatchers.withPartialText
import com.taccardi.zak.library.pojo.Card
import org.junit.Rule
import org.junit.Test


/**
 * Created by zak.taccardi on 3/21/17.
 */

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

        companion object {
            val FIRST_CARD_POSITION = 0
        }
    }
}