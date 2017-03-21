package com.taccardi.zak.library

import android.support.annotation.VisibleForTesting
import com.taccardi.zak.library.pojo.Card
import com.taccardi.zak.library.pojo.Rank
import com.taccardi.zak.library.pojo.Suit
import java.util.*
import kotlin.collections.ArrayList

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
            val cards = ArrayList<Card>(size)
            Suit.values.forEach { suit ->
                Rank.values.forEach { rank ->
                    cards.add(Card(rank, suit))
                }
            }

            return null!!
        }

        @VisibleForTesting
        val size: Int = Suit.count * Rank.count

    }

}
