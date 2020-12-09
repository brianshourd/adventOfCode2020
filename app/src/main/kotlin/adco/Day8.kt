package adco

import lib.either.Either
import lib.either.asLeft
import lib.either.asRight
import lib.option.Option
import lib.option.getOrElse
import lib.option.none
import lib.either.toEither
import lib.option.toOption
import lib.parsers.Parser
import lib.parsers.charP
import lib.parsers.choiceP
import lib.parsers.intP
import lib.parsers.map
import lib.parsers.newlineP
import lib.parsers.optional
import lib.parsers.plus
import lib.parsers.sepBy
import lib.parsers.seriesP
import lib.parsers.stringP
import java.util.BitSet

class Day8() : IAdcoProblem<List<Day8.Instruction>, Int> {
    override val title = "Day 8: Handheld Halting"

    // Parsing
    enum class Operation { ACC, JMP, NOP }
    data class Instruction(val op: Operation, val arg: Int)

    val parseOperation: Parser<Operation> = choiceP(
        stringP("acc").map { Operation.ACC },
        stringP("jmp").map { Operation.JMP },
        stringP("nop").map { Operation.NOP },
    )

    val parseInstruction: Parser<Instruction> = seriesP(
        parseOperation,
        charP(' '),
        charP('+').optional(),
        intP()
    ).map { (op, _, _, arg) -> Instruction(op, arg) }

    override val parser: Parser<List<Instruction>> = parseInstruction sepBy newlineP()

    // Implementation
    data class InstructionOutOfRangeError(
        val pos: Int,
        val acc: Int,
    ) : AdcoError("Instruction $pos out of range, current acc=$acc")
    data class InfiniteLoopError(
        val pos: Int,
        val acc: Int,
    ) : AdcoError("Visited instruction $pos more than once, current acc=$acc")

    fun runProgram(instructions: List<Instruction>): Either<AdcoError, Int> {
        val visited = BitSet(instructions.size)
        var current = 0
        var acc = 0
        var error: Option<AdcoError> = none()
        while (error.isEmpty() && current != instructions.size) {
            if (current !in instructions.indices) {
                error = InstructionOutOfRangeError(current, acc).toOption()
                break
            }
            if (visited[current]) {
                error = InfiniteLoopError(current, acc).toOption()
                break
            }
            visited.set(current)
            val instruction = instructions[current]
            when (instruction.op) {
                Operation.ACC -> {
                    acc += instruction.arg
                    current += 1
                }
                Operation.NOP -> {
                    current += 1
                }
                Operation.JMP -> {
                    current += instruction.arg
                }
            }
        }
        return error.map { it.asLeft() }.getOrElse { acc.asRight() }
    }

    override fun partA(input: List<Instruction>): Either<AdcoError, Int> =
        runProgram(input).fold(
            { err ->
                when (err) {
                    is InfiniteLoopError -> err.acc.asRight()
                    else -> err.asLeft()
                }
            },
            { AdcoError("Program terminated without looping").asLeft() }
        )

    override fun partB(input: List<Instruction>): Either<AdcoError, Int> {
        val instructions = input.toMutableList()
        var output: Option<Int> = none()
        for (i in instructions.indices) {
            if (!output.isEmpty()) { break }
            val current = instructions[i]
            val newOp = when (current.op) {
                Operation.NOP -> Operation.JMP
                Operation.JMP -> Operation.NOP
                Operation.ACC -> continue
            }
            instructions[i] = current.copy(op = newOp)
            runProgram(instructions.toList()).map { output = it.toOption() }
            instructions[i] = current
        }
        return output.toEither { AdcoError("No single substitution found") }
    }
}
