package adco

import adco.Day14.Instruction.SetMask
import adco.Day14.Instruction.SetMemory
import helpers.assertRight
import kotlinx.collections.immutable.immutableSetOf
import kotlinx.collections.immutable.minus
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toImmutableSet
import kotlin.test.Test
import kotlin.test.assertEquals

class Day14Tests {
    val day14 = Day14()
    val inputRawA = listOf(
        "mask = XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X",
        "mem[8] = 11",
        "mem[7] = 101",
        "mem[8] = 0",
        "mask = XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX0XX",
        "mem[3] = 1",
    ).joinToString("\n")
    val inputRawB = listOf(
        "mask = 000000000000000000000000000000X1001X",
        "mem[42] = 100",
        "mask = 00000000000000000000000000000000X0XX",
        "mem[26] = 1",
    ).joinToString("\n")
    val allWilds = (0..35).fold(Pair(mutableSetOf<Long>(), 1L)) { (acc, m), _ ->
        acc += m
        Pair(acc, m shl 1)
    }.first.toSet()

    @Test
    fun testParserA() {
        val output = assertRight(day14.parser(inputRawA))
        val expected = listOf(
            SetMask(64L, 2L, (allWilds - setOf(2L, 64L)).toImmutableSet()),
            SetMemory(8L, 11L),
            SetMemory(7L, 101L),
            SetMemory(8L, 0L),
            SetMask(0L, 4L, (allWilds - setOf(4L)).toImmutableSet()),
            SetMemory(3L, 1L),
        )
        assertEquals(expected, output)
    }

    @Test
    fun testParserB() {
        val output = assertRight(day14.parser(inputRawB))
        val expected = listOf(
            SetMask(18L, 68719476684L, immutableSetOf(1L, 32L)),
            SetMemory(42L, 100L),
            SetMask(0L, 68719476724L, immutableSetOf(1L, 2L, 8L)),
            SetMemory(26L, 1L),
        )
        assertEquals(expected, output)
    }

    @Test
    fun testPartA() {
        val input = assertRight(day14.parser(inputRawA))
        val output = assertRight(day14.partA(input))
        val expected = 166L
        assertEquals(expected, output)
    }

    @Test
    fun testPartB() {
        val input = assertRight(day14.parser(inputRawB))
        val output = assertRight(day14.partB(input))
        val expected = 208L
        assertEquals(expected, output)
    }
}
