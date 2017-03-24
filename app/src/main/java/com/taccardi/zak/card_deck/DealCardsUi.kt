package com.taccardi.zak.card_deck

import android.support.annotation.VisibleForTesting
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.taccardi.zak.card_deck.DealCardsUi.State.Change.*
import com.taccardi.zak.library.Deck
import com.taccardi.zak.library.pojo.Card
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * The user interface for dealing cards.
 *
 * @see DealCardsActivity
 */
interface DealCardsUi {


    fun render(state: State)

    val state: State

    interface Intentions {
        /**
         * When the user requests to deal the top card from the deck
         */
        fun dealCardRequests(): Observable<Unit>

        /**
         * When the user requests to shuffle the remaining cards in the deck
         */
        fun shuffleDeckRequests(): Observable<Unit>

        /**
         * When the user requests a new deck
         */
        fun newDeckRequests(): Observable<Unit>
    }

    interface Actions {
        /**
         * The the number of remaining cards in the deck
         */
        fun showRemainingCards(remainingCards: Int)

        fun showDealtCards(items: List<CardsRecycler.Item>)

    }

    /**
     * The view state for [DealCardsUi]
     */
    data class State(
            val deck: Deck,
            val isShuffling: Boolean,
            val isDealing: Boolean,
            val isBuildingNewDeck: Boolean,
            val error: String?
    ) {
        val remaining: Int get() = deck.remaining.size
        val dealt: List<Card> get() = deck.dealt


        fun reduce(change: Change): State = when (change) {
            NoOp -> this
            RequestShuffle -> this.copy(isShuffling = true)
            RequestTopCard -> this.copy(isDealing = true)
            RequestNewDeck -> this.copy(isBuildingNewDeck = true)
            is DeckModified -> this.copy(deck = deck)
            is Error -> this.copy(error = change.description)
            IsDealing -> this.copy(isDealing = true)
            IsShuffling -> this.copy(isShuffling = true)
            IsBuildingDeck -> this.copy(isBuildingNewDeck = true)
            DealingComplete -> this.copy(isDealing = false)
            ShuffleComplete -> this.copy(isShuffling = false)
            BuildingDeckComplete -> this.copy(isBuildingNewDeck = false)
        }


        sealed class Change(val logText: String) {
            object RequestShuffle : Change("user -> requested shuffle")
            object RequestTopCard : Change("user -> request top card of deck to be dealt")
            object RequestNewDeck : Change("user -> requesting a new deck")
            class Error(val description: String) : Change("error -> $description")
            object NoOp : Change("")
            class DeckModified(val deck: com.taccardi.zak.library.Deck) : Change("disk -> deck changed. ${deck.remaining.size} cards remaining. ${deck.dealt.size} cards dealt.")
            object IsDealing : Change("network -> card is being dealt")
            object IsShuffling : Change("network -> deck is being shuffled")
            object IsBuildingDeck : Change("network -> deck is being build")
            object DealingComplete : Change("network -> card was successfully dealt")
            object ShuffleComplete : Change("network -> deck was successfully shuffled")
            object BuildingDeckComplete : Change("network -> deck was successfully built")
        }

        companion object {
            //default
            val NO_CARDS_DEALT by lazy {
                State(
                        deck = Deck.FRESH_DECK,
                        isShuffling = false,
                        isDealing = false,
                        isBuildingNewDeck = false,
                        error = null
                )
            }

            @VisibleForTesting
            val EVERY_CARD_DEALT by lazy {
                NO_CARDS_DEALT.copy(Deck.EVERY_CARD_DEALT)
            }

        }

    }


    class Renderer(val uiActions: DealCardsUi.Actions) : StateRenderer<DealCardsUi.State> {

        val disposables = CompositeDisposable()

        val state: Relay<State> = PublishRelay.create()

        val remainingCards = state
                .map { it.remaining }
                .distinctUntilChanged()
                .doOnNext { uiActions.showRemainingCards(it) }!!

        init {
            start()
        }

        fun start() {
            disposables += remainingCards
                    .subscribe()

            disposables += state
                    .map { it.dealt }
                    .distinctUntilChanged()
                    .map { it.map { CardsRecycler.Item.UiCard(it) } }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        uiActions.showDealtCards(it)
                    }
        }

        override fun render(viewState: DealCardsUi.State) {
            this.state.accept(viewState)
        }

        fun stop() {
            disposables.clear()
        }
    }

}

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}

fun <T, R> io.reactivex.Observable<T>.scanMap(func2: (T?, T) -> R): io.reactivex.Observable<R> {
    return this.startWith(null as T?) //emit a null value first, otherwise the .buffer() below won't emit at first (needs 2 emissions to emit)
            .buffer(2, 1) //buffer the previous and current emission
            .filter { it.size >= 2 } //when the buffer terminates (onCompleted/onError), the remaining buffer is emitted. When don't want those!
            .map { func2.invoke(it[0], it[1]) }
}

fun <T, R> io.reactivex.Observable<T>.scanMap(initialValue: T, func2: (T, T) -> R): io.reactivex.Observable<R> {
    return this.startWith(initialValue)
            .buffer(2, 1)
            .filter { it.size >= 2 }
            .map { func2.invoke(it[0], it[1]) }
}
