package com.taccardi.zak.library.pojo

/**
 * A "card" in a deck. ex: Jack of clubs
 *
 * @param rank 2,3,4...Jack,Queen,etc
 * @param suit Hearts, Spades, etc
 */
data class Card(val rank: Rank, val suit: Suit) {
    val id = hashCode()
}
