package adco

import helpers.assertRight
import kotlinx.collections.immutable.minus
import kotlinx.collections.immutable.plus
import kotlin.test.Test
import kotlin.test.assertEquals

class Day15Tests {
    val day15 = Day15()
    val inputRaw = "0,3,6"

    @Test
    fun testParser() {
        val output = assertRight(day15.parser(inputRaw))
        val expected = listOf(0, 3, 6)
        assertEquals(expected, output)
    }

    @Test
    fun testPartA() {
        val cases = listOf(
            "0,3,6" to 436,
            "1,3,2" to 1,
            "2,1,3" to 10,
            "1,2,3" to 27,
            "2,3,1" to 78,
            "3,2,1" to 438,
            "3,1,2" to 1836,
        )
        for ((inputRaw, expected) in cases) {
            val input = assertRight(day15.parser(inputRaw))
            val output = assertRight(day15.partA(input))
            assertEquals(expected, output)
        }
    }
}
