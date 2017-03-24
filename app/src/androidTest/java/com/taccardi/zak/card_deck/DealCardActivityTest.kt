package com.taccardi.zak.card_deck

import android.support.annotation.IdRes
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.v7.widget.RecyclerView
import com.taccardi.zak.card_deck.DealCardActivityTest.Delegate.UserIntent.*
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
        delegate.assertState()
    }

    @Test
    fun every_card_dealt() {
        delegate = Delegate(activityRule, DealCardsUi.State.EVERY_CARD_DEALT)
        delegate.assertState()
    }

    @Test
    fun number_of_cards_remaining() {
        var state = DealCardsUi.State.NO_CARDS_DEALT

        var deck = state.deck.withDealtCard()
        state = state.copy(deck = deck)

        assert(deck.remaining.size == 51)
        delegate = Delegate(activityRule, state)
        delegate.assertState()
    }

    @Test
    fun intention_deal_card_clicks() {
        delegate = Delegate(rule = activityRule)
        val subscriber = delegate.intentions.dealCardRequests()
                .test()


        delegate.click(DEAL_CARD)
        delegate.click(DEAL_CARD)
        //two clicks
        subscriber.assertValueCount(2)
    }

    @Test
    fun intention_shuffle_deck_clicks() {
        delegate = Delegate(rule = activityRule)
        val subscriber = delegate.intentions.shuffleDeckRequests()
                .test()


        delegate.click(SHUFFLE_DECK)
        delegate.click(SHUFFLE_DECK)
        delegate.click(SHUFFLE_DECK)
        //three clicks
        subscriber.assertValueCount(3)
    }

    @Test
    fun intention_build_new_deck_clicks() {
        delegate = Delegate(rule = activityRule)
        val subscriber = delegate.intentions.newDeckRequests()
                .test()


        delegate.click(BUILD_NEW_DECK)
        delegate.click(BUILD_NEW_DECK)
        delegate.click(BUILD_NEW_DECK)
        delegate.click(BUILD_NEW_DECK)
        //four clicks
        subscriber.assertValueCount(4)
    }

    /**
     * Runs the test
     * @param state state that will be rendered onto the view, and then verified that it is rendered properly
     */
    class Delegate(
            private val rule: ActivityTestRule<DealCardsActivity>,
            private val state: DealCardsUi.State = DealCardsUi.State.NO_CARDS_DEALT

    ) {
        val activity: DealCardsActivity by lazy { rule.activity }
        val ui by lazy { activity }
        val intentions: DealCardsUi.Intentions by lazy { activity }
        val deckAndCards: ViewInteraction by lazy { onView(withId(R.id.cards_recycler)) }
        val recyclerMatcher by lazy { RecyclerViewMatcher(R.id.cards_recycler) }
        val firstCard: ViewInteraction get() = onView(recyclerMatcher.atPosition(FIRST_CARD_POSITION))
        val deck: ViewInteraction get() = onView(recyclerMatcher.atPosition(DECK_POSITION))
        val remaining: ViewInteraction get() = onView(withId(REMAINING_CARDS_HINT_ID))
        val shuffleDeckButton: ViewInteraction get() = onView(withId(SHUFFLE_BUTTON_ID))
        //        val dealCardButton: ViewInteraction get() = onView(withId(DEAL_CARD_BUTTON_ID))
        val buildNewDeckButton: ViewInteraction get() = onView(withId(NEW_DECK_BUTTON_ID))

        init {
            ui.render(state)
        }

        fun assertState() {
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

        fun DealCardsUi.Intentions.assertClicks() {
            deckAndCards.perform(scrollToPosition<RecyclerView.ViewHolder>(DECK_POSITION))
            deck.check(matches(isDisplayed()))
            deck.check(matches(
                    hasDescendant(withId(DECK_ICON_ID))
            ))
        }

        enum class UserIntent {
            DEAL_CARD,
            SHUFFLE_DECK,
            BUILD_NEW_DECK
        }

        companion object {
            val DECK_POSITION = 0
            val FIRST_CARD_POSITION = DECK_POSITION + 1
            @IdRes val DECK_ICON_ID = R.id.dealCardsUi_deck_icon
            @IdRes val REMAINING_CARDS_HINT_ID = R.id.dealCardsUi_cardsRemaining_textView
            @IdRes val NEW_DECK_BUTTON_ID = R.id.button_new_deck
            @IdRes val DEAL_CARD_BUTTON_ID = R.id.button_deal_card
            @IdRes val SHUFFLE_BUTTON_ID = R.id.button_shuffle
        }

        fun click(userIntent: UserIntent) = when (userIntent) {
            DEAL_CARD -> deck.perform(click())
            SHUFFLE_DECK -> shuffleDeckButton.perform(click())
            BUILD_NEW_DECK -> buildNewDeckButton.perform(click())
        }

    }
}
