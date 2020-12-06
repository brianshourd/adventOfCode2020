package adco

import adco.Day6.GroupAnswers
import helpers.assertRight
import kotlin.test.Test
import kotlin.test.assertEquals

class Day6Tests {
    val day6 = Day6()
    val inputRaw = listOf(
        "abc",
        "",
        "a",
        "b",
        "c",
        "",
        "ab",
        "ac",
        "",
        "a",
        "a",
        "a",
        "a",
        "",
        "b",
    ).joinToString("\n")

    @Test
    fun testParser() {
        val output = assertRight(day6.parser(inputRaw))
        val expected = listOf(
            GroupAnswers(listOf(setOf('a', 'b', 'c'))),
            GroupAnswers(
                listOf(
                    setOf('a'),
                    setOf('b'),
                    setOf('c'),
                )
            ),
            GroupAnswers(
                listOf(
                    setOf('a', 'b'),
                    setOf('a', 'c'),
                )
            ),
            GroupAnswers(
                listOf(
                    setOf('a'),
                    setOf('a'),
                    setOf('a'),
                    setOf('a'),
                )
            ),
            GroupAnswers(
                listOf(
                    setOf('b'),
                )
            ),
        )
        assertEquals(expected, output)
    }

    @Test
    fun testPartA() {
        val input = assertRight(day6.parser(inputRaw))
        val output = assertRight(day6.partA(input))
        val expected = 11
        assertEquals(expected, output)
    }

    @Test
    fun testPartB() {
        val input = assertRight(day6.parser(inputRaw))
        val output = assertRight(day6.partB(input))
        val expected = 6
        assertEquals(expected, output)
    }
}
