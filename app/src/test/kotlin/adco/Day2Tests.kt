package adco

import adco.Day2.PasswordLine
import helpers.assertRight
import kotlin.test.Test
import kotlin.test.assertEquals

class Day2Tests {
    val day2 = Day2()

    @Test
    fun testPasswordLineParser() {
        val output = assertRight(day2.passwordLineParser.parse("1-3 a: abcde"))
        val expected = PasswordLine(1..3, 'a', "abcde")
        assertEquals(expected, output)
    }

    @Test
    fun testPasswordIsValidA() {
        val cases = listOf(
            Pair(PasswordLine(1..3, 'a', "abcde"), true),
            Pair(PasswordLine(1..3, 'b', "cdefg"), false),
            Pair(PasswordLine(2..9, 'c', "ccccccccc"), true),
        )
        for ((input, expected) in cases) {
            assertEquals(
                expected,
                day2.passwordIsValidA(input),
                "Expected validatePassword($input) to be $expected"
            )
        }
    }

    @Test
    fun testPartA() {
        val inputRaw = "1-3 a: abcde\n1-3 b: cdefg\n2-9 c: ccccccccc\n"
        val input = assertRight(day2.parser.parse(inputRaw))
        val output = assertRight(day2.partA(input))
        val expected = 2
        assertEquals(expected, output)
    }

    @Test
    fun testPasswordIsValidB() {
        val cases = listOf(
            Pair(PasswordLine(1..3, 'a', "abcde"), true),
            Pair(PasswordLine(1..3, 'b', "cdefg"), false),
            Pair(PasswordLine(2..9, 'c', "ccccccccc"), false),
        )
        for ((input, expected) in cases) {
            assertEquals(
                expected,
                day2.passwordIsValidB(input),
                "Expected validatePassword($input) to be $expected"
            )
        }
    }

    @Test
    fun testPartB() {
        val inputRaw = "1-3 a: abcde\n1-3 b: cdefg\n2-9 c: ccccccccc\n"
        val input = assertRight(day2.parser.parse(inputRaw))
        val output = assertRight(day2.partB(input))
        val expected = 1
        assertEquals(expected, output)
    }
}
