package adco

import adco.Day13.Input
import adco.Day13.Mod
import helpers.assertRight
import lib.option.none
import lib.option.some
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals

class Day13Tests {
    val day13 = Day13()
    val inputRaw = listOf(
        "939",
        "7,13,x,x,59,x,31,19",
    ).joinToString("\n")

    @Test
    fun testParser() {
        val output = assertRight(day13.parser(inputRaw))
        val expected = Input(
            939,
            listOf(some(7), some(13), none(), none(), some(59), none(), some(31), some(19))
        )
        assertEquals(expected, output)
    }

    @Test
    fun testPartA() {
        val input = assertRight(day13.parser(inputRaw))
        val output = assertRight(day13.partA(input))
        val expected = 295.toBigInteger()
        assertEquals(expected, output)
    }

    @Test
    fun testAddConstraint() {
        val a = Mod(0.toBigInteger(), 5.toBigInteger())
        val b = Mod(1.toBigInteger(), 7.toBigInteger())
        val c = Mod(0.toBigInteger(), 3.toBigInteger())
        val output = day13.addConstraint(b, a).let { day13.addConstraint(it, c) }
        val expected = Mod(15.toBigInteger(), 105.toBigInteger())
        assertEquals(expected, output)
    }

    @Test
    fun testPartB() {
        val cases = listOf(
            "7,13,x,x,59,x,31,19" to BigInteger("1068781"),
            "17,x,13,19" to BigInteger("3417"),
            "67,7,59,61" to BigInteger("754018"),
            "67,x,7,59,61" to BigInteger("779210"),
            "67,7,x,59,61" to BigInteger("1261476"),
            "1789,37,47,1889" to BigInteger("1202161486"),
        )
        for ((inputRaw, expected) in cases) {
            val input = assertRight(day13.parseBusSchedule(inputRaw))
            val output = assertRight(day13.partB(Input(0, input)))
            assertEquals(expected, output)
        }
    }
}
