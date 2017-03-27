package com.taccardi.zak.library.model

import android.support.annotation.VisibleForTesting
import com.jakewharton.rxrelay2.BehaviorRelay
import com.taccardi.zak.library.Deck
import com.taccardi.zak.library.model.ForceError.*
import com.taccardi.zak.library.pojo.Card
import io.reactivex.Observable
import io.reactivex.Scheduler
import java.util.concurrent.TimeUnit

/**
 * Interacts with the deck of cards
 */
interface Dealer {


    fun dealTopCard()

    fun shuffleDeck()

    fun requestNewDeck()

    fun decks(): Observable<Deck>

    fun dealOperations(): Observable<DealOperation>

    fun buildingDeckOperations(): Observable<BuildingDeckOperation>

    fun shuffleOperations(): Observable<ShuffleOperation>

}

sealed class DealOperation {
    //when card is in the process of being dealt
    object Dealing : DealOperation()

    //an error happened when dealing the card
    class Error(val description: String) : DealOperation()

    //successfully dealt card
    class TopCard(val card: Card) : DealOperation()
}

sealed class ShuffleOperation {
    //when deck is in the process of being shuffled
    object Shuffling : ShuffleOperation()

    //an error happened when shuffling the deck
    class Error(val description: String) : ShuffleOperation()

    //successfully shuffled deck
    class Shuffled(val deck: Deck) : ShuffleOperation()
}

sealed class BuildingDeckOperation {
    //when the deck is in the process of being built
    object Building : BuildingDeckOperation()

    //an error happened while building the deck
    class Error(val description: String) : BuildingDeckOperation()

    //successfully built deck
    class Built(val deck: Deck) : BuildingDeckOperation()
}


class InMemoryDealer(
        val comp: Scheduler,
        val forceError: ForceError = NEVER,
        val delayMs: Long = 0
) : Dealer {

    private var deck: Deck = Deck.FRESH_DECK
        set(value) {
            field = value
            decks.accept(field)
        }
    @VisibleForTesting val decks = BehaviorRelay.createDefault(deck)!!
    @VisibleForTesting val dealOperations = BehaviorRelay.create<DealOperation>()!!
    @VisibleForTesting val shuffleOperations = BehaviorRelay.create<ShuffleOperation>()!!
    @VisibleForTesting val buildingDeckOperations = BehaviorRelay.create<BuildingDeckOperation>()!!


    override fun dealTopCard() {
        Observable.just(dealOperations.accept(DealOperation.Dealing))
                .delay(delayMs, TimeUnit.MILLISECONDS, comp)
                .doOnNext {

                    fun success() {
                        val newDeck = deck.withDealtCard()
                        dealOperations.accept(DealOperation.TopCard(newDeck.lastCardDealt!!))
                        deck = newDeck
                    }

                    fun error() {
                        dealOperations.accept(DealOperation.Error("Failed to deal top card. Try again"))
                    }

                    when (forceError) {
                        NEVER -> success()
                        SOMETIMES -> TODO()
                        ALWAYS -> error()
                    }
                }
                .subscribe()


    }

    override fun shuffleDeck() {
        Observable.just(shuffleOperations.accept(ShuffleOperation.Shuffling))
                .delay(delayMs, TimeUnit.MILLISECONDS, comp)
                .doOnNext {

                    fun success() {
                        val shuffled = deck.toShuffled()
                        shuffleOperations.accept(ShuffleOperation.Shuffled(shuffled))
                        deck = shuffled
                    }

                    fun error() {
                        shuffleOperations.accept(ShuffleOperation.Error("Shuffle failed. Try again"))
                    }

                    when (forceError) {
                        NEVER -> success()
                        SOMETIMES -> TODO()
                        ALWAYS -> error()
                    }
                }
                .subscribe()


    }

    override fun requestNewDeck() {
        Observable.just(buildingDeckOperations.accept(BuildingDeckOperation.Building))
                .delay(delayMs, TimeUnit.MILLISECONDS, comp)
                .doOnNext {

                    fun success() {
                        val fresh = Deck.FRESH_DECK
                        buildingDeckOperations.accept(BuildingDeckOperation.Built(fresh))
                        deck = fresh
                    }

                    fun error() {
                        buildingDeckOperations.accept(BuildingDeckOperation.Error("Building new deck failed. Try again"))
                    }

                    when (forceError) {
                        NEVER -> success()
                        SOMETIMES -> TODO()
                        ALWAYS -> error()
                    }
                }
                .subscribe()
    }

    override fun decks(): Observable<Deck> {
        return decks
//                .mergeWith(
//                shuffleOperations.filter { it is ShuffleOperation.Shuffled }
//                        .map { it as ShuffleOperation.Shuffled }
//                        .map { it.deck }
//        )
    }

    override fun dealOperations(): Observable<DealOperation> = dealOperations

    override fun buildingDeckOperations(): Observable<BuildingDeckOperation> = buildingDeckOperations

    override fun shuffleOperations(): Observable<ShuffleOperation> = shuffleOperations

}


public enum class ForceError {
    NEVER,
    SOMETIMES,
    ALWAYS
}