package com.taccardi.zak.card_deck.presentation.deal_cards

import com.taccardi.zak.card_deck.app.MyApplication
import com.taccardi.zak.card_deck.presentation.base.StateRenderer
import com.taccardi.zak.card_deck.presentation.deal_cards.DealCardsUi.State
import com.taccardi.zak.card_deck.presentation.deal_cards.DealCardsUi.State.Change
import com.taccardi.zak.card_deck.presentation.deal_cards.DealCardsUi.State.Change.Error
import com.taccardi.zak.card_deck.presentation.deal_cards.DealCardsUi.State.ErrorSource
import com.taccardi.zak.library.model.BuildingDeckOperation
import com.taccardi.zak.library.model.DealOperation
import com.taccardi.zak.library.model.Dealer
import com.taccardi.zak.library.model.ShuffleOperation
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

/**
 * Presenter for [DealCardsUi]
 *
 * @property intentions user input events
 * @property renderer how we output state to the user
 * @property dealer our "data" layer.
 */
class DealCardsPresenter(
        private val ui: DealCardsUi,
        private val intentions: DealCardsUi.Intentions,
        private val dealer: Dealer
) {

    val disposables = CompositeDisposable()

    @Suppress("USELESS_CAST")
    fun start() {
        if (!MyApplication.ACTIVATE_PRESENTERS) return

        val shuffles = intentions.shuffleDeckRequests()
                .map { Change.RequestShuffle as Change }
                .doOnNext {
                    dealer.shuffleDeck()
                }
                .share()
                .onErrorReturn(handleUnknownError)

        val newDeckRequests = intentions.newDeckRequests()
                .map { Change.RequestNewDeck }
                .doOnNext {
                    dealer.requestNewDeck()
                }
                .map { Change.RequestNewDeck as Change }
                .onErrorReturn(handleUnknownError)

        val dealCardRequests = intentions.dealCardRequests()
                .map { Change.RequestTopCard as Change }
                .doOnNext {
                    dealer.dealTopCard()
                }
                .onErrorReturn(handleUnknownError)

        val decks = dealer.decks()
                .map { deck -> Change.DeckModified(deck) as Change }
                .onErrorReturn(handleUnknownError)

        val dealOperations = dealer.dealOperations()
                .map { operation -> handle(operation) }
                .onErrorReturn(handleUnknownError)

        val shuffleOperations = dealer.shuffleOperations()
                .map { operation -> handle(operation) }
                .onErrorReturn(handleUnknownError)

        val deckBuildingOperations = dealer.buildingDeckOperations()
                .map { operation -> handle(operation) }
                .onErrorReturn(handleUnknownError)

        val merged = shuffles.mergeWith(newDeckRequests)
                .mergeWith(dealCardRequests)
                .mergeWith(decks)
                .mergeWith(dealOperations)
                .mergeWith(shuffleOperations)
                .mergeWith(deckBuildingOperations)
                .doOnNext { change -> Timber.tag(TAG); Timber.d(change.logText) }


        disposables += merged.scan(ui.state, State::reduce)
                .doOnNext { state -> Timber.tag(TAG); Timber.v("    --- $state") }
                .subscribe(ui::render)
    }

    fun stop() {
        disposables.clear()
    }

    companion object {
        private val TAG = "(${DealCardsUi::class.java.simpleName})"

        private val handleUnknownError: (Throwable) -> Change = { t -> Error(null, t.localizedMessage) }

        private fun handle(operation: DealOperation): Change = when (operation) {
            DealOperation.Dealing -> Change.IsDealing
            is DealOperation.Error -> Error(ErrorSource.DEALING, operation.description)
            is DealOperation.TopCard -> Change.DealingComplete
        }

        private fun handle(operation: ShuffleOperation): Change = when (operation) {
            ShuffleOperation.Shuffling -> Change.IsShuffling
            is ShuffleOperation.Error -> Error(ErrorSource.SHUFFLING, operation.description)
            is ShuffleOperation.Shuffled -> Change.ShuffleComplete
        }

        private fun handle(operation: BuildingDeckOperation): Change = when (operation) {
            BuildingDeckOperation.Building -> Change.IsBuildingDeck
            is BuildingDeckOperation.Error -> Error(ErrorSource.BUILDING_NEW_DECK, operation.description)
            is BuildingDeckOperation.Built -> Change.BuildingDeckComplete
        }

    }
}
