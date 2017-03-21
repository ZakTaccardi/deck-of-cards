package com.taccardi.zak.card_deck

import com.taccardi.zak.library.model.Dealer
import io.reactivex.disposables.CompositeDisposable

/**
 * Presenter for [DealCardsUi]
 */
class DealCardsPresenter(
        val intentions: DealCardsUi.Intentions,
        val renderer: DealCardsUi.Renderer,
        val dealer: Dealer
) {

    val disposables = CompositeDisposable()

    fun start() {
        disposables += intentions.shuffleDeckRequests()
                .subscribe()
    }

    fun stop() {
        disposables.clear()
    }

}
