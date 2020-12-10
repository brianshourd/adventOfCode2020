package adco

import helpers.assertRight
import kotlin.test.Test
import kotlin.test.assertEquals

class Day10Tests {
    val day10 = Day10()
    val inputRaw1 = listOf(
        "16",
        "10",
        "15",
        "5",
        "1",
        "11",
        "7",
        "19",
        "6",
        "12",
        "4",
    ).joinToString("\n")
    val inputRaw2 = listOf(
        "28",
        "33",
        "18",
        "42",
        "31",
        "14",
        "46",
        "20",
        "48",
        "47",
        "24",
        "23",
        "49",
        "45",
        "19",
        "38",
        "39",
        "11",
        "1",
        "32",
        "25",
        "35",
        "8",
        "17",
        "7",
        "9",
        "4",
        "2",
        "34",
        "10",
        "3",
    ).joinToString("\n")

    @Test
    fun testPartA1() {
        val input = assertRight(day10.parser(inputRaw1))
        val output = assertRight(day10.partA(input))
        val expected = 35L
        assertEquals(expected, output)
    }

    @Test
    fun testPartA2() {
        val input = assertRight(day10.parser(inputRaw2))
        val output = assertRight(day10.partA(input))
        val expected = 220L
        assertEquals(expected, output)
    }

    @Test
    fun testPartB1() {
        val input = assertRight(day10.parser(inputRaw1))
        val output = assertRight(day10.partB(input))
        val expected = 8L
        assertEquals(expected, output)
    }

    @Test
    fun testPartB2() {
        val input = assertRight(day10.parser(inputRaw2))
        val output = assertRight(day10.partB(input))
        val expected = 19208L
        assertEquals(expected, output)
    }
}
