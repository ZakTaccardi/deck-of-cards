package com.taccardi.zak.library.pojo

/**
 * The rank of a [Card]. ex: Queen
 */
enum class Rank(val stringDef: String, val intDef: Int) {
    TWO("two", 2),
    THREE("three", 3),
    FOUR("four", 4),
    FIVE("five", 5),
    SIX("six", 6),
    SEVEN("seven", 7),
    EIGHT("eight", 8),
    NINE("nine", 9),
    TEN("ten", 10),
    JACK("jack", 11),
    QUEEN("queen", 12),
    KING("king", 13),
    ACE("ace", 14);


    override fun toString(): String {
        return stringDef
    }

    companion object {
        /**
         * Java defensively copies a new array every time this is called. Let's only do that once
         */
        val values by lazy { Rank.values() }
        val count by lazy { Companion.values.size }
    }
}