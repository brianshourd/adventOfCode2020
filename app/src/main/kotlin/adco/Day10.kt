package adco

import lib.either.Either
import lib.either.asRight
import lib.memoize.Memoize
import lib.parsers.Parser
import lib.parsers.intP
import lib.parsers.newlineP
import lib.parsers.plus
import lib.parsers.sepBy

class Day10() : IAdcoProblem<List<Int>, Long> {
    override val title = "Day 10: Adapter Array"

    override val parser: Parser<List<Int>> = intP() sepBy newlineP()

    fun getJumps(xs: List<Int>): List<Int> =
        (xs + 0).sorted().let { it + ((it.lastOrNull() ?: 0) + 3) }
            .zipWithNext { a, b -> b - a }

    fun countArrangements(jumps: List<Int>): Int =
        if (jumps.size == 0 || jumps[0] > 3) {
            0
        } else if (jumps.size < 2) {
            1
        } else {
            countArrangements(jumps.drop(1)) +
                countArrangements(listOf(jumps[0] + jumps[1]) + jumps.drop(2))
        }

    tailrec fun processJumps(jumps: List<Int>, f: (List<Int>) -> Int, acc: Long = 1L): Long =
        if (jumps.isEmpty()) {
            acc
        } else if (jumps[0] == 3) {
            processJumps(jumps.dropWhile { it == 3 }, f, acc)
        } else {
            var xs = jumps.takeWhile { it != 3 }
            processJumps(jumps.drop(xs.size), f, acc * f(xs))
        }

    override fun partA(input: List<Int>): Either<AdcoError, Long> =
        getJumps(input).groupingBy { it }.eachCount().let {
            (it[1] ?: 0) * (it[3] ?: 0)
        }.toLong().asRight()

    override fun partB(input: List<Int>): Either<AdcoError, Long> =
        getJumps(input).let { processJumps(it, Memoize(::countArrangements)) }.asRight()
}
