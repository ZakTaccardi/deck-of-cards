package com.taccardi.zak.library

import com.taccardi.zak.library.pojo.Rank
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Tests for [Rank]
 */
class RankTest {

    @Test
    fun countWithoutJoker() {
        val actualCount = Rank.count
        val expected = 13

        assertThat(actualCount)
                .describedAs("When jokers are not included, the number of ranks should be $expected")
                .isEqualTo(expected)
    }

    @Test
    fun valuesNotDefensivelyCopied() {
        val values1 = Rank.values
        val values2 = Rank.values

        assertThat(values1)
                .describedAs("Java defensively copies an array every time a EnumClass.values() is accessed. Don't pay that price every time")
                .isSameAs(values2)
    }
}