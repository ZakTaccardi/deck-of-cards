package com.taccardi.zak.card_deck.presentation.base

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.taccardi.zak.card_deck.presentation.deal_cards.DealCardsUi
import com.taccardi.zak.card_deck.presentation.deal_cards.plusAssign
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

/**
 * Renders a [DealCardsUi.State] to the [DealCardsUi]
 */
class DealCardsUiRenderer(
        val uiActions: DealCardsUi.Actions,
        val main: Scheduler,
        val comp: Scheduler
) : StateRenderer<DealCardsUi.State> {
    private val disposables = CompositeDisposable()
    //observable relay that represents the UI's state over time. Each emission
    //represents the latest state.
    private val stateRelay: Relay<DealCardsUi.State> = PublishRelay.create<DealCardsUi.State>()
            .toSerialized()

    /**
     * Binds a state pojo to the UI,
     * @param state a pojo representing the latest state of the UI to be rendered
     */
    override fun render(state: DealCardsUi.State) {
        this.stateRelay.accept(state)
    }

    fun start() {
        //observe loading state
        disposables += stateRelay
                //reduce state to whether it's loading (true/false)
                .map { it.isLoading }
                //only render load to UI
                .distinctUntilChanged()
                //handle all calculations on a background thread pool
                .subscribeOn(comp)
                //hop on the UI thread to render to the view
                .observeOn(main)
                .subscribe { isLoading ->
                    uiActions.showLoading(isLoading)
                    uiActions.disableButtons(disable = isLoading)
                }
    }
}
