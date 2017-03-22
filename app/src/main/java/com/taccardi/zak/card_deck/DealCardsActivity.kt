package com.taccardi.zak.card_deck

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.taccardi.zak.library.model.Dealer
import com.taccardi.zak.library.pojo.Card
import com.taccardi.zak.library.pojo.Rank
import com.taccardi.zak.library.pojo.Suit
import io.reactivex.Observable

class DealCardsActivity : AppCompatActivity(), DealCardsUi, DealCardsUi.Actions, DealCardsUi.Intentions {

    override var state = DealCardsUi.State.NO_CARDS_DEALT

    lateinit var renderer: DealCardsUi.Renderer
    lateinit var dealCardClicks: Relay<Unit>
    lateinit var shuffleDeckClicks: Relay<Unit>
    lateinit var newDeckRequests: Relay<Unit>
    lateinit var cards : CardsRecycler
    lateinit var presenter : DealCardsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cards)
        val component: Component = Dependencies(this, Dealer())

        renderer = component.renderer
        dealCardClicks = component.dealCardClicks
        shuffleDeckClicks = component.shuffleDeckClicks
        newDeckRequests = component.newDeckRequests
        cards = component.cards
        presenter = component.presenter

        component.newDeckButton.setOnClickListener { newDeckRequests.accept(Unit) }
        component.shuffleButton.setOnClickListener { shuffleDeckClicks.accept(Unit) }
        component.dealCardButton.setOnClickListener { dealCardClicks.accept(Unit) }
    }

    override fun onStart() {
        super.onStart()

        renderer.render(
                DealCardsUi.State(
                        remainingCards = 50,
                        cardsDealt = listOf(
                                Card(Rank.TWO, Suit.HEARTS),
                                Card(Rank.ACE, Suit.SPADES)
                        )
                )
        )
    }

    override fun render(state: DealCardsUi.State) {
        this.state = state
        renderer.render(state)
    }

    override fun showDealtCards(items: List<CardsRecycler.Item>) {
        cards.showCardsDealt(items)
    }

    override fun showRemainingCards(remainingCards: Int) {
        cards.showRemainingCards(remainingCards)
    }

    override fun dealCardRequests(): Observable<Unit> {
        return dealCardClicks
    }

    override fun shuffleDeckRequests(): Observable<Unit> {
        return shuffleDeckClicks
    }

    override fun newDeckRequests(): Observable<Unit> {
        return newDeckRequests
    }

    class Dependencies(
            val activity: DealCardsActivity,
            val dealer: Dealer
    ) : Component {
        override val dealCardClicks: Relay<Unit> by lazy { PublishRelay.create<Unit>() }
        override val shuffleDeckClicks: Relay<Unit> by lazy { PublishRelay.create<Unit>() }
        override val newDeckRequests: Relay<Unit> by lazy { PublishRelay.create<Unit>() }
        override val renderer by lazy { DealCardsUi.Renderer(activity) }
        override val cards: CardsRecycler by lazy {
            val recycler = activity.findViewById(R.id.cards_recycler) as RecyclerView
            return@lazy CardsRecycler(recycler)
        }
        override val presenter by lazy {
            DealCardsPresenter(activity, renderer, dealer)
        }
        override val shuffleButton: View by lazy {
            activity.findViewById(R.id.button_shuffle)
        }
        override val newDeckButton: View by lazy {
            activity.findViewById(R.id.button_new_deck)
        }
        override val dealCardButton: View by lazy {
            activity.findViewById(R.id.button_deal_card)
        }
    }

    interface Component {
        val dealCardClicks: Relay<Unit>
        val shuffleDeckClicks: Relay<Unit>
        val newDeckRequests: Relay<Unit>
        val renderer: DealCardsUi.Renderer
        val cards: CardsRecycler
        val presenter: DealCardsPresenter
        val newDeckButton: View
        val shuffleButton: View
        val dealCardButton: View
    }
}
