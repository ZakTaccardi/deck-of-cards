package com.taccardi.zak.library

import com.taccardi.zak.library.pojo.Suit
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Tests for [Suit]
 */
class SuitTest {
    @Test
    fun countStandardFour() {
        val actualCount = Suit.count
        val expected = 4

        assertThat(actualCount)
                .describedAs("The number of suits should be $expected")
                .isEqualTo(expected)
    }

    @Test
    fun valuesNotDefensivelyCopied() {
        val values1 = Suit.values
        val values2 = Suit.values

        assertThat(values1)
                .describedAs("Java defensively copies an array every time a EnumClass.values() is accessed. Don't pay that price every time")
                .isSameAs(values2)
    }
}