package adco

import adco.Day5.BoardingPass
import adco.Day5.Bsp
import adco.Day5.Bsp.B
import adco.Day5.Bsp.F
import helpers.assertLeft
import helpers.assertRight
import kotlin.test.Test
import kotlin.test.assertEquals

class Day5Tests {
    val day5 = Day5()

    @Test
    fun testParser() {
        val inputRaw = listOf(
            "BFFFBBFRRR",
            "FFFBBBFRRR",
            "BBFFBBFRLL",
        ).joinToString("\n")
        val output = assertRight(day5.parser(inputRaw))
        val expected = listOf(
            BoardingPass(
                listOf(B, F, F, F, B, B, F),
                listOf(B, B, B),
            ),
            BoardingPass(
                listOf(F, F, F, B, B, B, F),
                listOf(B, B, B),
            ),
            BoardingPass(
                listOf(B, B, F, F, B, B, F),
                listOf(B, F, F),
            ),
        )
        assertEquals(expected, output)
    }

    @Test
    fun testDecodeBoardingPass() {
        val cases = listOf(
            Pair("BFFFBBFRRR", Triple(70, 7, 567)),
            Pair("FFFBBBFRRR", Triple(14, 7, 119)),
            Pair("BBFFBBFRLL", Triple(102, 4, 820)),
        )
        for ((inputRaw, expected) in cases) {
            val input = assertRight(day5.parseBoardingPass(inputRaw))
            assertEquals(expected, Triple(input.rowId, input.colId, input.seatId))
        }
    }

    @Test
    fun testPartA() {
        val inputRaw = listOf(
            "BFFFBBFRRR",
            "FFFBBBFRRR",
            "BBFFBBFRLL",
        ).joinToString("\n")
        val input = assertRight(day5.parser(inputRaw))
        val output = assertRight(day5.partA(input))
        val expected = 820
        assertEquals(820, output)
    }
}
