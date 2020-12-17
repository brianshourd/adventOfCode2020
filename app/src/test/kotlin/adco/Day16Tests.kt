package adco

import adco.Day16.FieldRule
import adco.Day16.Input
import helpers.assertRight
import helpers.assertSome
import kotlinx.collections.immutable.minus
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.plus
import lib.option.some
import kotlin.test.Test
import kotlin.test.assertEquals

class Day16Tests {
    val day16 = Day16()
    val inputRawA = listOf(
        "class: 1-3 or 5-7",
        "row: 6-11 or 33-44",
        "seat: 13-40 or 45-50",
        "",
        "your ticket:",
        "7,1,14",
        "",
        "nearby tickets:",
        "7,3,47",
        "40,4,50",
        "55,2,20",
        "38,6,12",
    ).joinToString("\n")
    val inputRawB = listOf(
        "class: 0-1 or 4-19",
        "row: 0-5 or 8-19",
        "seat: 0-13 or 16-19",
        "",
        "your ticket:",
        "11,12,13",
        "",
        "nearby tickets:",
        "3,9,18",
        "15,1,5",
        "5,14,9",
    ).joinToString("\n")

    @Test
    fun testParser() {
        val output = assertRight(day16.parser(inputRawA))
        val expected = Input(
            rules = persistentSetOf(
                FieldRule("class", 1..3, 5..7),
                FieldRule("row", 6..11, 33..44),
                FieldRule("seat", 13..40, 45..50),
            ),
            yourTicket = listOf(7, 1, 14),
            nearbyTickets = listOf(
                listOf(7, 3, 47),
                listOf(40, 4, 50),
                listOf(55, 2, 20),
                listOf(38, 6, 12),
            ),
        )
        assertEquals(expected, output)
    }

    @Test
    fun testValidForAnyField() {
        val input = assertRight(day16.parser(inputRawA))
        val output = day16.validForAnyField(input.rules)
        val expected = persistentSetOf(
            1, 2, 3, 5, 6, 7, 8, 9, 10, 11, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 45, 46, 47, 48, 49, 50
        )
        assertEquals(expected, output)
    }

    @Test
    fun testPartA() {
        val input = assertRight(day16.parser(inputRawA))
        val output = assertRight(day16.partA(input))
        val expected = 71L
        assertEquals(expected, output)
    }

    @Test
    fun testOrderFields() {
        val input = assertRight(day16.parser(inputRawB))
        val output = day16.findValidOrdering(input)
        val expected = listOf(
            FieldRule("row", 0..5, 8..19),
            FieldRule("class", 0..1, 4..19),
            FieldRule("seat", 0..13, 16..19),
        )
        assertEquals(some(expected), output)
    }

    @Test
    fun testLabelTicket() {
        val input = assertRight(day16.parser(inputRawB))
        val ordering = assertSome(day16.findValidOrdering(input), "No valid ordering: ")
        val output = day16.labelTicket(ordering, input.yourTicket)
        val expected = mapOf("class" to 12, "row" to 11, "seat" to 13)
        assertEquals(expected, output)
    }
}
