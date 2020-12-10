package adco

import helpers.assertRight
import lib.option.toOption
import kotlin.test.Test
import kotlin.test.assertEquals

class Day9Tests {
    val day9 = Day9()
    val inputRaw = listOf(
        "35",
        "20",
        "15",
        "25",
        "47",
        "40",
        "62",
        "55",
        "65",
        "95",
        "102",
        "117",
        "150",
        "182",
        "127",
        "219",
        "299",
        "277",
        "309",
        "576",
    ).joinToString("\n")

    @Test
    fun testParser() {
        val output = assertRight(day9.parser(inputRaw))
        val expected = listOf(35, 20, 15, 25, 47, 40, 62, 55, 65, 95, 102, 117, 150, 182, 127, 219, 299, 277, 309, 576).map(Int::toLong)
        assertEquals(expected, output)
    }

    @Test
    fun testFindFailingChecksum() {
        val input = assertRight(day9.parser(inputRaw))
        val output = day9.findFailingChecksum(input, 5)
        val expected = 127L.toOption()
        assertEquals(expected, output)
    }

    @Test
    fun testFindSublistSummingTo() {
        val input = assertRight(day9.parser(inputRaw))
        val output = day9.findSublistSummingTo(input, 127L)
        val expected = listOf(15L, 25L, 47L, 40L).toOption()
        assertEquals(expected, output)
    }
}
