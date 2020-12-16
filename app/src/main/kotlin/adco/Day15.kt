package adco

import lib.either.Either
import lib.either.toEither
import lib.option.getOrElse
import lib.option.toOption
import lib.parsers.Parser
import lib.parsers.charP
import lib.parsers.intP
import lib.parsers.sepBy1

class Day15() : IAdcoProblem<List<Int>, Int> {
    override val title = "Day 15: Rambunctious Recitation"

    override val parser: Parser<List<Int>> = intP() sepBy1 charP(',')

    tailrec fun speakNumber(
        oldNumbers: MutableMap<Int, Int>,
        justSpoken: Int,
        currentIx: Int,
        targetIx: Int
    ): Int {
        if (currentIx >= targetIx) { return justSpoken }
        val nextSpoken = oldNumbers[justSpoken].toOption().map { currentIx - it }.getOrElse { 0 }
        oldNumbers.put(justSpoken, currentIx)
        return speakNumber(oldNumbers, nextSpoken, currentIx + 1, targetIx)
    }

    fun run(starters: List<Int>, targetNumber: Int): Either<AdcoError, Int> {
        val oldNumbers = mutableMapOf<Int, Int>()
        starters.dropLast(1).forEachIndexed { ix, oldNum -> oldNumbers[oldNum] = ix }
        return starters.lastOrNull().toEither {
            AdcoError("Cannot begin without any starting numbers")
        }.map { speakNumber(oldNumbers, it, starters.size - 1, targetNumber - 1) }
    }

    override fun partA(input: List<Int>): Either<AdcoError, Int> = run(input, 2020)

    override fun partB(input: List<Int>): Either<AdcoError, Int> = run(input, 30000000)
}
