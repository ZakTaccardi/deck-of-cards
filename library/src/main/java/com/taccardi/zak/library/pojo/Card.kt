package com.taccardi.zak.library.pojo

import android.os.Parcel
import android.os.Parcelable
import paperparcel.PaperParcel

/**
 * A "card" in a deck. ex: Jack of clubs
 *
 * @param rank 2,3,4...Jack,Queen,etc
 * @param suit Hearts, Spades, etc
 */
@PaperParcel
data class Card(val rank: Rank, val suit: Suit) : Parcelable {
    @Transient val id = hashCode()


    companion object {
        @JvmField val CREATOR = PaperParcelCard.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = PaperParcelCard.writeToParcel(this, dest, flags)

}
