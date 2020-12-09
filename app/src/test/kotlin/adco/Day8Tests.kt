package adco

import adco.Day8.Instruction
import adco.Day8.Operation.ACC
import adco.Day8.Operation.JMP
import adco.Day8.Operation.NOP
import helpers.assertLeft
import helpers.assertRight
import kotlin.test.Test
import kotlin.test.assertEquals

class Day8Tests {
    val day8 = Day8()
    val inputRaw = listOf(
        "nop +0",
        "acc +1",
        "jmp +4",
        "acc +3",
        "jmp -3",
        "acc -99",
        "acc +1",
        "jmp -4",
        "acc +6",
    ).joinToString("\n")

    @Test
    fun testParser() {
        val output = assertRight(day8.parser(inputRaw))
        val expected = listOf(
            Instruction(NOP, 0),
            Instruction(ACC, 1),
            Instruction(JMP, 4),
            Instruction(ACC, 3),
            Instruction(JMP, -3),
            Instruction(ACC, -99),
            Instruction(ACC, 1),
            Instruction(JMP, -4),
            Instruction(ACC, 6),
        )
        assertEquals(expected, output)
    }

    @Test
    fun testRunProgram() {
        val input = assertRight(day8.parser(inputRaw))
        val error = assertLeft(day8.runProgram(input))
        val expected = Day8.InfiniteLoopError(1, 5)
        assertEquals(expected, error)
    }

    @Test
    fun testPartA() {
        val input = assertRight(day8.parser(inputRaw))
        val output = assertRight(day8.partA(input))
        val expected = 5
        assertEquals(expected, output)
    }

    @Test
    fun testRunProgram2() {
        val input = listOf(
            Instruction(NOP, 0),
            Instruction(ACC, 1),
            Instruction(JMP, 4),
            Instruction(ACC, 3),
            Instruction(JMP, -3),
            Instruction(ACC, -99),
            Instruction(ACC, 1),
            Instruction(NOP, -4),
            Instruction(ACC, 6),
        )
        val output = assertRight(day8.runProgram(input))
        val expected = 8
        assertEquals(expected, output)
    }

    @Test
    fun testPartB() {
        val input = assertRight(day8.parser(inputRaw))
        val output = assertRight(day8.partB(input))
        val expected = 8
        assertEquals(expected, output)
    }
}
