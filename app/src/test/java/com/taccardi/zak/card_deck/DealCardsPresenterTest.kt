package com.taccardi.zak.card_deck

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.taccardi.zak.card_deck.presentation.base.StateRenderer
import com.taccardi.zak.card_deck.presentation.deal_cards.DealCardsPresenter
import com.taccardi.zak.card_deck.presentation.deal_cards.DealCardsUi
import com.taccardi.zak.library.pojo.Deck
import com.taccardi.zak.library.model.BuildingDeckOperation
import com.taccardi.zak.library.model.DealOperation
import com.taccardi.zak.library.model.InMemoryDealer
import com.taccardi.zak.library.model.ShuffleOperation
import com.taccardi.zak.library.pojo.Card
import com.taccardi.zak.library.pojo.Rank
import com.taccardi.zak.library.pojo.Suit
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import timber.log.Timber

/**
 * Test for [DealCardsPresenter]
 */
class DealCardsPresenterTest {

    val trampoline = Schedulers.trampoline()!!

    private lateinit var user: FakeIntentions
    private lateinit var renderer: FakeRenderer
    private lateinit var dealer: InMemoryDealer
    private lateinit var presenter: DealCardsPresenter
    private val state get() = renderer.current
    private val states get() = renderer.states

    @Before
    fun setUp() {
        Timber.plant(object : Timber.Tree() {
            override fun log(logLevel: Int, tag: String?, message: String?, throwable: Throwable?) {
                println(message)
            }
        })

        user = FakeIntentions()
        renderer = FakeRenderer()
        dealer = InMemoryDealer(trampoline)
        presenter = DealCardsPresenter(
                intentions = user,
                renderer = renderer,
                dealer = dealer,
                ui = object : DealCardsUi {
                    override var state: DealCardsUi.State = DealCardsUi.State.NO_CARDS_DEALT
                    override fun render(state: DealCardsUi.State) {
                        this.state = state
                    }
                }
        )
        presenter.start()
    }

    @Test
    fun has_initial_state() {
        assertThat(state)
                .isNotNull()
    }

    @Test
    fun deck_is_dealing_when_user_deals_card() {
        user.dealCard()
        state!!.assertIsDealing()
    }

    @Test
    fun is_shuffling_when_user_requests_shuffle() {
        user.shuffleDeck()
        state!!.assertIsShuffling()
    }

    @Test
    fun is_building_new_deck_when_user_requests_a_new_deck() {
        user.newDeck()
        state!!.assertIsBuildingNewDeck()
    }

    @Test
    fun dealing_operation_is_dealing() {
        dealer.dealOperations.accept(DealOperation.Dealing)
        state!!.assertIsDealing()
    }

    @Test
    fun deal_operation_error() {
        val text = "An error occured"
        dealer.dealOperations.accept(DealOperation.Error(text))
        state!!.assertError(text)
    }

    @Test
    fun deal_operation_success() {
        val kingOfHearts = Card(Rank.KING, Suit.HEARTS)
        dealer.dealOperations.accept(DealOperation.TopCard(kingOfHearts))
        state!!.assertIsDealing(false)
    }

    //    deck_is_building_from_disk
    @Test
    fun shuffle_operation_is_shuffling() {
        dealer.shuffleOperations.accept(ShuffleOperation.Shuffling)
        state!!.assertIsShuffling()
    }

    @Test
    fun shuffle_operation_error() {
        val text = "An error occurred while shuffling"
        dealer.shuffleOperations.accept(ShuffleOperation.Error(text))
        state!!.assertError(text)
    }

    @Test
    fun shuffle_operation_success() {
        val deck = Deck(remaining = listOf(Card(Rank.KING, Suit.HEARTS)), dealt = emptyList())
        dealer.shuffleOperations.accept(ShuffleOperation.Shuffled(deck))
        state!!.assertIsShuffling(false)
    }

    @Test
    fun building_deck_operation_is_building() {
        dealer.buildingDeckOperations.accept(BuildingDeckOperation.Building)
        state!!.assertIsBuildingNewDeck(true)
    }

    @Test
    fun building_deck_operation_error() {
        val text = "An error occurred while building the new deck"
        dealer.buildingDeckOperations.accept(BuildingDeckOperation.Error(text))
        state!!.assertError(text)
    }

    @Test
    fun building_deck_operation_success() {
        val deck = Deck.FRESH_DECK
        dealer.buildingDeckOperations.accept(BuildingDeckOperation.Built(deck))
        state!!.assertIsBuildingNewDeck(false)
    }

    private class FakeRenderer : StateRenderer<DealCardsUi.State> {

        var states: MutableList<DealCardsUi.State> = ArrayList()
        val current get() = states.lastOrNull()

        override fun render(state: DealCardsUi.State) {
            states.add(state)
        }
    }

    private class FakeIntentions : DealCardsUi.Intentions {

        private val dealCard: Relay<Unit> = PublishRelay.create()
        private val shuffleDeckRequests: Relay<Unit> = PublishRelay.create()
        private val newDeckRequests: Relay<Unit> = PublishRelay.create()

        override fun dealCardRequests(): Observable<Unit> = dealCard

        override fun shuffleDeckRequests(): Observable<Unit> = shuffleDeckRequests

        override fun newDeckRequests(): Observable<Unit> = newDeckRequests

        fun dealCard() {
            dealCard.accept(Unit)
        }

        fun shuffleDeck() {
            shuffleDeckRequests.accept(Unit)
        }

        fun newDeck() {
            newDeckRequests.accept(Unit)
        }

    }
}

private fun DealCardsUi.State.assertIsShuffling(isShuffling: Boolean = true) {
    if (isShuffling) {
        assertThat(this.isShuffling)
                .describedAs("State should be in shuffling. Was $this")
                .isTrue()
    } else {
        assertThat(this.isShuffling)
                .describedAs("State should not be in shuffling. Was $this")
                .isFalse()
    }
}

private fun DealCardsUi.State.assertIsDealing(expected: Boolean = true) {
    if (expected) {
        assertThat(this.isDealing)
                .describedAs("State should be dealing. Was $this")
                .isTrue()
    } else {
        assertThat(this.isDealing)
                .describedAs("State should NOT be dealing. Was $this")
                .isFalse()
    }
}

private fun DealCardsUi.State.assertIsBuildingNewDeck(isBuilding: Boolean = true) {
    if (isBuilding) {
        assertThat(this.isBuildingNewDeck)
                .describedAs("State should be building new deck. Was $this")
                .isTrue()
    } else {
        assertThat(this.isBuildingNewDeck)
                .describedAs("State should NOT be building new deck. Was $this")
                .isFalse()
    }

}

private fun DealCardsUi.State.assertError(text: String) {
    assertThat(this.error)
            .describedAs("State should have error: $text. Was $this")
            .isEqualTo(text)
}

private fun DealCardsUi.State.assertLastCardDealt(card: Card) {
    assertThat(this.dealt.firstOrNull())
            .describedAs("State's last card should be $card. Was in: $this")
            .isEqualTo(card)
}

private fun DealCardsUi.State.assertDeckSize(expectedSize: Int) {
    assertThat(this.remaining)
            .describedAs("Size should have $expectedSize cards remaining. Was in: $this")
            .isEqualTo(expectedSize)
}

private fun DealCardsUi.State.assertDeckSizeIsCorrect(numberOfCardsDealt: Int) {
    assertThat(this.remaining)
            .describedAs("Size should be the full deck size, minus the number of cards dealt ($numberOfCardsDealt)")
            .isEqualTo(Deck.FRESH_DECK.remaining.size - numberOfCardsDealt)
}
