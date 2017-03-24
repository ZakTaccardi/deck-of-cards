package com.taccardi.zak.card_deck

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.taccardi.zak.library.model.Dealer
import com.taccardi.zak.library.model.InMemoryDealer
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DealCardsActivity : AppCompatActivity(), DealCardsUi, DealCardsUi.Actions, DealCardsUi.Intentions {

    override var state = DealCardsUi.State.NO_CARDS_DEALT

    lateinit var renderer: DealCardsUi.Renderer
    lateinit var dealCardClicks: Relay<Unit>
    lateinit var shuffleDeckClicks: Relay<Unit>
    lateinit var newDeckRequests: Relay<Unit>
    lateinit var cards: CardsRecycler
    lateinit var presenter: DealCardsPresenter
    lateinit var cardsLeftHint: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cards)
        val component: Component = Dependencies(this, InMemoryDealer(Schedulers.computation()))

        renderer = component.renderer
        dealCardClicks = component.dealCardClicks
        shuffleDeckClicks = component.shuffleDeckClicks
        newDeckRequests = component.newDeckRequests
        cards = component.cards
        cardsLeftHint = component.cardsLeftHint
        presenter = component.presenter

        component.newDeckButton.setOnClickListener { newDeckRequests.accept(Unit) }
        component.shuffleButton.setOnClickListener { shuffleDeckClicks.accept(Unit) }
        component.dealCardButton.setOnClickListener { dealCardClicks.accept(Unit) }
    }

    override fun onStart() {
        super.onStart()
        presenter.start()
    }

    override fun onStop() {
        super.onStop()
        presenter.stop()
    }

    override fun render(state: DealCardsUi.State) {
        this.state = state
        renderer.render(state)
    }

    override fun showDealtCards(items: List<CardsRecycler.Item>) {
        cards.showCardsDealt(items)
    }

    override fun showRemainingCards(remainingCards: Int) {
        cardsLeftHint.text = this.applicationContext.remainingCardsHint(remainingCards)
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

        override val cards: CardsRecycler by lazy {
            val recycler = activity.findViewById(R.id.cards_recycler) as RecyclerView
            return@lazy CardsRecycler(recycler, dealCardClicks)
        }
        override val presenter by lazy {
            DealCardsPresenter(activity, activity, renderer, dealer)
        }
        override val cardsLeftHint: TextView by lazy {
            activity.findViewById(R.id.dealCardsUi_cardsRemaining_textView) as TextView
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
        override val main: Scheduler by lazy { AndroidSchedulers.mainThread() }
        override val disk: Scheduler by lazy { Schedulers.io() }
        override val comp: Scheduler by lazy {
//            Schedulers.computation()
            main
        }
        override val renderer by lazy { DealCardsUi.Renderer(activity, main = main, comp = comp) }

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
        val cardsLeftHint: TextView
        val main: Scheduler
        val disk: Scheduler
        val comp: Scheduler
    }
}

fun Context.remainingCardsHint(count: Int): String {
    if (count == 0) {
        return this.getString(R.string.dealCardsUi_remainingCards_hint_zero)
    }

    return this.resources.getQuantityString(R.plurals.number_of_cards_left, count, count)
}
