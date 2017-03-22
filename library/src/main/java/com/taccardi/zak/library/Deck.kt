package com.taccardi.zak.library

import com.taccardi.zak.library.pojo.Card
import com.taccardi.zak.library.pojo.Rank
import com.taccardi.zak.library.pojo.Suit
import java.util.*

/**
 * A deck of [Card]s
 */
class Deck constructor(
        val cards: Stack<Card>
) {

    init {

    }

    companion object {
        fun create(): Deck {
            val cards = Stack<Card>()
            Suit.values.forEach { suit ->
                Rank.values.forEach { rank ->
                    cards.add(Card(rank, suit))
                }
            }

            return Deck(cards)
        }

        val SIZE: Int by lazy {
            Suit.count * Rank.count
        }
    }
}
