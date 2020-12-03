package adco

import lib.either.Either
import lib.either.asRight
import lib.parsers.Parser
import lib.parsers.map
import lib.parsers.newlineP
import lib.parsers.restOfLineP
import lib.parsers.sepBy

class Day3() : IAdcoProblem<List<String>, Long> {
    override val title = "Day 3: Toboggan Trajectory"

    override val parser: Parser<List<String>> = restOfLineP() sepBy newlineP()

    fun countTrees(map: List<String>, slopeX: Int, slopeY: Int): Long =
        map.filterIndexed { i, _ -> i % slopeY == 0 }
            .mapIndexed { i, line -> line[(slopeX * i) % line.length] }
            .count { it == '#' }.toLong()

    override fun partA(input: List<String>): Either<AdcoError, Long> =
        countTrees(input, 3, 1).asRight()

    override fun partB(input: List<String>): Either<AdcoError, Long> =
        listOf(Pair(1, 1), Pair(3, 1), Pair(5, 1), Pair(7, 1), Pair(1, 2)).map { (x, y) ->
            countTrees(input, x, y)
        }.fold(1L, Long::times).asRight()
}
