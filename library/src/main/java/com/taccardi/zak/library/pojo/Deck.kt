package com.taccardi.zak.library.pojo

import android.os.Parcel
import android.os.Parcelable
import paperparcel.PaperParcel
import java.util.*

/**
 * A deck of [Card]s
 */
@PaperParcel
data class Deck constructor(
        val remaining: List<Card>,
        val dealt: List<Card>
) : Parcelable {

    @Transient val lastCardDealt: Card? = dealt.firstOrNull()

    /**
     * @return the top card of the deck, or null if it does not exist. This does not actually "deal" the top card.
     */
    @Transient val topCard: Card? = remaining.firstOrNull()


    /**
     * @return a new instance of this deck with a card dealt, or the same instance if no cards remain
     */
    fun withDealtCard(): Deck {
        if (remaining.isNotEmpty()) {
            val cards = remaining.toStack()
            val topCard = cards.pop()
            return Deck(cards, topCard.append(this.dealt))
        }
        return this
    }

    /**
     * @return a new instance of this deck with the remaining cards shuffled
     */
    fun toShuffled(): Deck {
        val shuffled = this.remaining.toMutableList()
        Collections.shuffle(shuffled)
        return this.copy(remaining = shuffled)
    }

    companion object {
        @JvmField val CREATOR = PaperParcelDeck.CREATOR

        val FRESH_DECK by lazy {
            val cards = Stack<Card>()
            Suit.values.forEach { suit ->
                Rank.values.forEach { rank ->
                    cards.add(Card(rank, suit))
                }
            }

            Deck(cards, emptyList())
        }

        val EVERY_CARD_DEALT by lazy {
            val cards = Stack<Card>()
            Suit.values.forEach { suit ->
                Rank.values.forEach { rank ->
                    cards.add(Card(rank, suit))
                }
            }

            Deck(emptyList(), cards)
        }
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = PaperParcelDeck.writeToParcel(this, dest, flags)


    fun of(cards: List<Card>): Deck {
        return Deck(cards, emptyList())
    }

}

fun Collection<Card>.toStack(): Stack<Card> {
    val stack = Stack<Card>()
    this.reversed().forEach { card -> stack.push(card) }

    return stack
}

fun <T> T.append(collection: List<T>): List<T> {
    return ArrayList<T>(collection.size + 1)
            .also {
                it.add(this)
                it.addAll(collection)
            }
}