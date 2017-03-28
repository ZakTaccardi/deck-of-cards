package com.taccardi.zak.card_deck.presentation.deal_cards

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.VisibleForTesting
import android.support.v7.util.DiffUtil
import com.taccardi.zak.card_deck.presentation.base.StateRenderer
import com.taccardi.zak.card_deck.presentation.deal_cards.CardsRecycler.Item
import com.taccardi.zak.card_deck.presentation.deal_cards.DealCardsUi.State.Change.*
import com.taccardi.zak.card_deck.presentation.deal_cards.DealCardsUi.State.ErrorSource.*
import com.taccardi.zak.library.pojo.Card
import com.taccardi.zak.library.pojo.Deck
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import paperparcel.PaperParcel

/**
 * The user interface for dealing cards.
 *
 * @see DealCardsActivity
 */
interface DealCardsUi : StateRenderer<DealCardsUi.State> {

    val state: State

    override fun render(state: State)

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


        fun showDeck(diff: RecyclerViewBinding<Item>)

        /**
         * Show or hide the loading UI
         * @param isLoading true to show the loading UI, false to hide it
         */
        fun showLoading(isLoading: Boolean = true)

        /**
         * Disable or enable the buttons that allow user input
         * @param disable true if buttons should be disabled, false if they should be enabled
         */
        fun disableButtons(disable: Boolean)

        fun hideError()
        /**
         * @param error text to display to user
         */
        fun showError(error: String)

    }

    /**
     * The view state for [DealCardsUi]
     */
    @PaperParcel
    data class State(
            val deck: Deck,
            val isShuffling: Boolean,
            val isDealing: Boolean,
            val isBuildingNewDeck: Boolean,
            val error: String?
    ) : Parcelable {
        val remaining: Int get() = deck.remaining.size
        val dealt: List<Card> get() = deck.dealt
        @Transient val isLoading = isShuffling || isDealing || isBuildingNewDeck


        fun reduce(change: Change): State = when (change) {
            NoOp -> this
            RequestShuffle -> this.copy(isShuffling = true, error = null)
            RequestTopCard -> this.copy(isDealing = true, error = null)
            RequestNewDeck -> this.copy(isBuildingNewDeck = true, error = null)
            is DeckModified -> this.copy(deck = change.deck) //TODO write test
            is Error -> {
                when (change.source) {
                    SHUFFLING -> this.copy(error = change.description, isShuffling = false)
                    DEALING -> this.copy(error = change.description, isDealing = false)
                    BUILDING_NEW_DECK -> this.copy(error = change.description, isBuildingNewDeck = false)
                    null -> this.copy(error = change.description)
                }
            }
            IsDealing -> this.copy(isDealing = true)
            IsShuffling -> this.copy(isShuffling = true)
            IsBuildingDeck -> this.copy(isBuildingNewDeck = true)
            DealingComplete -> this.copy(isDealing = false)
            ShuffleComplete -> this.copy(isShuffling = false)
            BuildingDeckComplete -> this.copy(isBuildingNewDeck = false)
            DismissedError -> this.copy(error = null)
        }

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = PaperParcelDealCardsUi_State.writeToParcel(this, dest, flags)


        sealed class Change(val logText: String) {
            object RequestShuffle : Change("user -> requested shuffle")
            object RequestTopCard : Change("user -> request top card of deck to be dealt")
            object RequestNewDeck : Change("user -> requesting a new deck")
            class Error(val source: ErrorSource?, val description: String) : Change("error -> $description")
            object NoOp : Change("")
            class DeckModified(val deck: Deck) : Change("disk -> deck changed. ${deck.remaining.size} cards remaining. ${deck.dealt.size} cards dealt.")
            object IsDealing : Change("network -> card is being dealt")
            object IsShuffling : Change("network -> deck is being shuffled")
            object IsBuildingDeck : Change("network -> deck is being build")
            object DealingComplete : Change("network -> card was successfully dealt")
            object ShuffleComplete : Change("network -> deck was successfully shuffled")
            object BuildingDeckComplete : Change("network -> deck was successfully built")
            object DismissedError : Change("user -> dismissedError")
        }

        enum class ErrorSource {
            SHUFFLING,
            DEALING,
            BUILDING_NEW_DECK
        }

        companion object {
            @JvmField val CREATOR = PaperParcelDealCardsUi_State.CREATOR
            //default
            val DEFAULT by lazy { NO_CARDS_DEALT }

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

}

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}

fun <T, R> Observable<T>.scanMap(func2: (T?, T) -> R): Observable<R> {
    return this.startWith(null as T?) //emit a null value first, otherwise the .buffer() below won't emit at first (needs 2 emissions to emit)
            .buffer(2, 1) //buffer the previous and current emission
            .filter { it.size >= 2 } //when the buffer terminates (onCompleted/onError), the remaining buffer is emitted. When don't want those!
            .map { func2.invoke(it[0], it[1]) }
}

fun <T, R> Observable<T>.scanMap(initialValue: T, func2: (T, T) -> R): Observable<R> {
    return this.startWith(initialValue)
            .buffer(2, 1)
            .filter { it.size >= 2 }
            .map { func2.invoke(it[0], it[1]) }
}

data class RecyclerViewBinding<out T>(
        val new: List<T>,
        val diff: DiffUtil.DiffResult
)

data class Nullable<out T> constructor(val value: T?) {
    constructor() : this(null)


    fun isNull(): Boolean {
        return value == null
    }

    fun isNonNull(): Boolean {
        return !isNull()
    }

    override fun toString(): String {
        return value.toString()
    }

    companion object {
        val NULL = Nullable(null)
    }
}

fun <T : Any?> T.toNullable(): Nullable<T> {
    if (this == null) {
        return Nullable.NULL //reuse singleton
    } else {
        return Nullable(this)
    }
}

fun <T : Any, R : Any?> Observable<T>.mapNullable(func: (T) -> R?): Observable<Nullable<R?>> {
    return this.map { Nullable(func.invoke(it)) }
}
