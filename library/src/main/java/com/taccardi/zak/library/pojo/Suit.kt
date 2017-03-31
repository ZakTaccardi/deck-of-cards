package com.taccardi.zak.library.pojo

/**
 * The suit of a [Card]. ex: Hearts
 *
 * @property stringDef human readable string definition of this enum
 * @property intDef integer definition of this enum. Useful for storing in a database
 * @property symbol unicode symbol for suit shape
 */
enum class Suit(val stringDef: String, val intDef: Int, val symbol: String) {
    HEARTS("hearts", 1, "♥"),
    SPADES("spades", 2, "♠"),
    CLUBS("clubs", 3, "♣"),
    DIAMONDS("diamonds", 4, "♦");

    companion object {
        /**
         * Java defensively copies a new array every time this is called. Let's only do that once
         */
        val values by lazy { values() }

        /**
         * A count of the standard four suits only
         */
        val count by lazy { values.size }
    }
}