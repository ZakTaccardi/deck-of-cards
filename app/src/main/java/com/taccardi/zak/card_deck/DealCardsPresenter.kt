package com.taccardi.zak.card_deck

import com.taccardi.zak.card_deck.DealCardsUi.State
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
        private val renderer: StateRenderer<State>,
        private val dealer: Dealer
) {

    val disposables = CompositeDisposable()

    @Suppress("USELESS_CAST")
    fun start() {

        val shuffles = intentions.shuffleDeckRequests()
                .map { State.Change.RequestShuffle as State.Change }
                .doOnNext {
                    dealer.shuffleDeck()
                }
                .share()
                .onErrorReturn(handleUnknownError)


        val newDeckRequests = intentions.newDeckRequests()
                .map { State.Change.RequestNewDeck }
                .doOnNext {
                    dealer.requestNewDeck()
                }
                .map { State.Change.RequestNewDeck as State.Change }
                .onErrorReturn(handleUnknownError)


        val dealCardRequests = intentions.dealCardRequests()
                .map { State.Change.RequestTopCard as State.Change }
                .doOnNext {
                    dealer.dealTopCard()
                }
                .onErrorReturn(handleUnknownError)

        val decks = dealer.decks()
                .map { deck -> State.Change.DeckModified(deck) as State.Change }
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
                .subscribe(renderer::render)
    }

    fun stop() {
        disposables.clear()
    }

    companion object {
        private val TAG = "(${DealCardsUi::class.java.simpleName})"

        private val handleUnknownError: (Throwable) -> State.Change = { t -> State.Change.Error(t.localizedMessage) }

        private fun handle(operation: DealOperation): State.Change = when (operation) {
            DealOperation.Dealing -> State.Change.IsDealing
            is DealOperation.Error -> State.Change.Error(operation.description)
            is DealOperation.TopCard -> State.Change.DealingComplete
        }

        private fun handle(operation: ShuffleOperation): State.Change = when (operation) {
            ShuffleOperation.Shuffling -> State.Change.IsShuffling
            is ShuffleOperation.Error -> State.Change.Error(operation.description)
            is ShuffleOperation.Shuffled -> State.Change.ShuffleComplete
        }

        private fun handle(operation: BuildingDeckOperation): State.Change = when (operation) {
            BuildingDeckOperation.Building -> State.Change.IsBuildingDeck
            is BuildingDeckOperation.Error -> State.Change.Error(operation.description)
            is BuildingDeckOperation.Built -> State.Change.BuildingDeckComplete
        }

    }
}
