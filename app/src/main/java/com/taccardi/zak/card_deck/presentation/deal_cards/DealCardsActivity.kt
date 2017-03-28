package com.taccardi.zak.card_deck.presentation.deal_cards

import android.content.Context
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.evernote.android.state.State
import com.jakewharton.rxrelay2.Relay
import com.taccardi.zak.card_deck.R
import com.taccardi.zak.card_deck.app.MyApplication
import com.taccardi.zak.card_deck.presentation.base.BaseActivity
import com.taccardi.zak.card_deck.presentation.base.StateRenderer
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Named

class DealCardsActivity : BaseActivity(), DealCardsUi, DealCardsUi.Actions, DealCardsUi.Intentions, StateRenderer<DealCardsUi.State> {

    @State
    override var state = DealCardsUi.State.NO_CARDS_DEALT

    lateinit var component: DealCardsUiComponent
    @Inject lateinit var cards: CardsRecycler
    @Inject lateinit var renderer: DealCardsUi.Renderer
    @Inject lateinit var presenter: DealCardsPresenter
    @field:[Inject Named(DealCardsUiComponent.DEAL_CARD)] lateinit var dealCardClicks: Relay<Unit>
    @field:[Inject Named(DealCardsUiComponent.SHUFFLE_DECK)] lateinit var shuffleDeckClicks: Relay<Unit>
    @field:[Inject Named(DealCardsUiComponent.NEW_DECK)] lateinit var newDeckRequests: Relay<Unit>
    @field:[Inject Named(DealCardsUiComponent.CARDS_LEFT)] lateinit var cardsLeftHint: TextView
    @field:[Inject Named(DealCardsUiComponent.PROGRESS_BAR)] lateinit var progressBar: ProgressBar
    @field:[Inject Named(DealCardsUiComponent.NEW_DECK)] lateinit var newDeckButton: View
    @field:[Inject Named(DealCardsUiComponent.ERROR)] lateinit var error: TextView
    @field:[Inject Named(DealCardsUiComponent.SHUFFLE_DECK)] lateinit var shuffleButton: View
    @field:[Inject Named(DealCardsUiComponent.DEAL_CARDS_LAYOUT)] lateinit var dealCardsUi: ViewGroup
    private val buttons by lazy {
        arrayOf(newDeckButton, shuffleButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cards)

        component = (application as MyApplication).component
                .plus(DealCardsUiModule(this))

        component.injectMembers(this)

        newDeckButton.setOnClickListener { newDeckRequests.accept(Unit) }
        shuffleButton.setOnClickListener { shuffleDeckClicks.accept(Unit) }
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
}

fun Context.remainingCardsHint(count: Int): String {
    if (count == 0) {
        return this.getString(R.string.dealCardsUi_remainingCards_hint_zero)
    }

    return this.resources.getQuantityString(R.plurals.number_of_cards_left, count, count)
}
