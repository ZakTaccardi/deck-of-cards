package com.taccardi.zak.card_deck

import android.support.annotation.VisibleForTesting
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
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
            val remainingCards: Int,
            val cardsDealt: List<Card>
    ) {


        sealed class Mutator {

        }

        companion object {
            //default
            val NO_CARDS_DEALT by lazy {
                State(
                        remainingCards = Deck.SIZE,
                        cardsDealt = emptyList()
                )
            }

            @VisibleForTesting
            val EVERY_CARD_DEALT get() = State(
                    remainingCards = 0,
                    cardsDealt = Deck.create().cards
            )

        }
    }

    class Renderer(val uiActions: DealCardsUi.Actions) {

        val disposables = CompositeDisposable()

        val state: Relay<State> = PublishRelay.create()

        val remainingCards = state
                .map { it.remainingCards }
                .distinctUntilChanged()
                .doOnNext { uiActions.showRemainingCards(it) }!!

        init {
            start()
        }

        fun start() {
            disposables += remainingCards
                    .subscribe()

            disposables += state
                    .map { it.cardsDealt }
                    .distinctUntilChanged()
                    .map { it.map { CardsRecycler.Item.UiCard(it) } }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        uiActions.showDealtCards(it)
                    }
        }

        fun render(state: DealCardsUi.State) {
            this.state.accept(state)
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
