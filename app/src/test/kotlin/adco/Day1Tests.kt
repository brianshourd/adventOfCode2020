package adco

import helpers.assertRight
import lib.either.right
import lib.option.some
import kotlin.test.Test
import kotlin.test.assertEquals

class Day1Tests {
    val day1 = Day1()

    @Test
    fun testFindPairSummingTo() {
        val output = day1.findNSummingTo(
            listOf(1721, 979, 366, 299, 675, 1456).sorted(),
            2020,
            2
        )
        assertEquals(some(listOf(1721, 299)), output)
    }

    @Test
    fun testPartA() {
        val inputRaw = listOf(1721, 979, 366, 299, 675, 1456).joinToString("\n")
        val input = assertRight(day1.parser.parse(inputRaw))
        val output = day1.partA(input)
        val expected = right(514579)
        assertEquals(expected, output)
    }

    @Test
    fun testFindThreeSummingTo() {
        val output = day1.findNSummingTo(
            listOf(1721, 979, 366, 299, 675, 1456).sorted(),
            2020,
            3
        )
        assertEquals(some(listOf(979, 675, 366)), output)
    }

    @Test
    fun testPartB() {
        val inputRaw = listOf(1721, 979, 366, 299, 675, 1456).joinToString("\n")
        val input = assertRight(day1.parser.parse(inputRaw))
        val output = day1.partB(input)
        val expected = right(241861950)
        assertEquals(expected, output)
    }
}
