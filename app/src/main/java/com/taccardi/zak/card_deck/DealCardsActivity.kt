package com.taccardi.zak.card_deck

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.evernote.android.state.State
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.taccardi.zak.library.model.Dealer
import com.taccardi.zak.library.model.ForceError
import com.taccardi.zak.library.model.InMemoryDealer
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DealCardsActivity : BaseActivity(), DealCardsUi, DealCardsUi.Actions, DealCardsUi.Intentions, StateRenderer<DealCardsUi.State> {

    @State
    override var state = DealCardsUi.State.NO_CARDS_DEALT

    lateinit var renderer: DealCardsUi.Renderer
    lateinit var dealCardClicks: Relay<Unit>
    lateinit var shuffleDeckClicks: Relay<Unit>
    lateinit var newDeckRequests: Relay<Unit>
    lateinit var cards: CardsRecycler
    lateinit var presenter: DealCardsPresenter
    lateinit var cardsLeftHint: TextView
    lateinit var progressBar: ProgressBar
    lateinit var newDeckButton: View
    lateinit var error: TextView
    lateinit var shuffleButton: View
    lateinit var dealCardsUi: ViewGroup
    private val buttons by lazy {
        arrayOf(newDeckButton, shuffleButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cards)
        val component: Component = Dependencies(this, InMemoryDealer(Schedulers.computation(), forceError = ForceError.SOMETIMES, delayMs = 500))

        renderer = component.renderer
        dealCardClicks = component.dealCardClicks
        shuffleDeckClicks = component.shuffleDeckClicks
        newDeckRequests = component.newDeckRequests
        cards = component.cards
        cardsLeftHint = component.cardsLeftHint
        progressBar = component.progressBar
        presenter = component.presenter
        newDeckButton = component.newDeckButton
        shuffleButton = component.shuffleButton
        dealCardsUi = component.dealCardsUi
        error = component.error

        component.newDeckButton.setOnClickListener { newDeckRequests.accept(Unit) }
        component.shuffleButton.setOnClickListener { shuffleDeckClicks.accept(Unit) }
    }

    override fun onStart() {
        super.onStart()
        presenter.start()
    }

    override fun onStop() {
        presenter.stop()
        super.onStop()
    }

    override fun showError(error: String) {
        val transition = AutoTransition()
        TransitionManager.beginDelayedTransition(dealCardsUi, transition)

        this.error.text = error
        this.error.visibility = View.VISIBLE
    }


    override fun hideError() {
        val transition = AutoTransition()
        TransitionManager.beginDelayedTransition(dealCardsUi, transition)

        this.error.visibility = View.GONE
    }

    override fun render(state: DealCardsUi.State) {
        this.state = state
        renderer.render(state)
    }

    override fun showRemainingCards(remainingCards: Int) {
        cardsLeftHint.text = this.applicationContext.remainingCardsHint(remainingCards)
    }

    override fun dealCardRequests(): Observable<Unit> {
        return dealCardClicks
                .filter { !state.isLoading }
    }

    override fun shuffleDeckRequests(): Observable<Unit> {
        return shuffleDeckClicks
                .filter { !state.isLoading }
    }

    override fun newDeckRequests(): Observable<Unit> {
        return newDeckRequests
                .filter { !state.isLoading }
    }

    override fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            progressBar.isIndeterminate = true
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.INVISIBLE
        }
    }

    override fun showDeck(diff: RecyclerViewBinding<CardsRecycler.Item>) {
        cards.showDeck(diff)
    }

    override fun disableButtons(disable: Boolean) {
        if (disable) {
            buttons.forEach { button -> button.isEnabled = false }
        } else {
            buttons.forEach { button -> button.isEnabled = true }
        }
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
            DealCardsPresenter(activity, activity, activity, dealer)
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

        override val progressBar: ProgressBar by lazy {
            activity.findViewById(R.id.dealCardsUi_progressBar_loading) as ProgressBar
        }

        override val dealCardsUi: ViewGroup by lazy {
            activity.findViewById(R.id.dealCardsUi) as ViewGroup
        }

        override val error: TextView by lazy {
            activity.findViewById(R.id.dealCardsUi_error) as TextView
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
        val cardsLeftHint: TextView
        val progressBar: ProgressBar
        val dealCardsUi: ViewGroup
        val main: Scheduler
        val disk: Scheduler
        val comp: Scheduler
        val error: TextView
    }
}

fun Context.remainingCardsHint(count: Int): String {
    if (count == 0) {
        return this.getString(R.string.dealCardsUi_remainingCards_hint_zero)
    }

    return this.resources.getQuantityString(R.plurals.number_of_cards_left, count, count)
}
