package com.taccardi.zak.library.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * A "card" in a deck. ex: Jack of clubs
 *
 * @param rank 2,3,4...Jack,Queen,etc
 * @param suit Hearts, Spades, etc
 */
@Parcelize
data class Card(val rank: Rank, val suit: Suit) : Parcelable {
    @Transient
    val id = hashCode()

}
