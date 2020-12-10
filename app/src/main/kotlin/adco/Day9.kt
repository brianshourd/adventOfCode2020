package adco

import lib.either.Either
import lib.either.flatMap
import lib.either.toEither
import lib.option.Option
import lib.option.none
import lib.option.toOption
import lib.parsers.Parser
import lib.parsers.longP
import lib.parsers.map
import lib.parsers.newlineP
import lib.parsers.plus
import lib.parsers.sepBy

class Day9() : IAdcoProblem<List<Long>, Long> {
    override val title = "Day 9: Encoding Error"

    override val parser: Parser<List<Long>> = longP() sepBy newlineP()

    class RollingPairSums(val capacity: Int) {
        private val sums = mutableMapOf<Long, Int>()
        private val items: ArrayDeque<Long> = ArrayDeque(capacity)

        private fun addSum(a: Long) { sums[a] = (sums[a] ?: 0) + 1 }
        private fun removeSum(r: Long) {
            val c = sums[r] ?: 0
            if (c == 1) { sums.remove(r) } else { sums[r] = c - 1 }
        }

        fun add(itemToAdd: Long): RollingPairSums {
            if (items.size >= capacity) {
                val itemRemoved = items.removeFirst()
                items.forEach { removeSum(itemRemoved + it) }
            }
            items.forEach { addSum(itemToAdd + it) }
            items.addLast(itemToAdd)
            return this
        }

        fun containsSum(x: Long): Boolean = sums.containsKey(x)
    }

    fun findFailingChecksum(xs: List<Long>, preambleSize: Int): Option<Long> {
        val rps = RollingPairSums(preambleSize)
        xs.take(preambleSize).forEach { rps.add(it) }
        for (x in xs.drop(preambleSize)) {
            if (rps.containsSum(x)) { rps.add(x) } else { return x.toOption() }
        }
        return none()
    }

    // Note this only works if all of the numbers are positive, otherwise we must perform a more exhaustive search
    fun findSublistSummingTo(xs: List<Long>, target: Long): Option<List<Long>> {
        if (xs.size <= 2) { return none() }
        var i = 0
        var j = 1
        var sum = xs[i] + xs[j]
        do {
            if (sum == target) {
                return xs.subList(i, j + 1).toOption()
            } else if (sum < target) {
                j += 1
                if (j >= xs.size) { break; }
                sum += xs[j]
            } else {
                sum -= xs[i]
                i += 1
                if (i >= j) {
                    j = i + 1
                    if (j >= xs.size) { break; }
                    sum += xs[j]
                }
            }
        } while (i < xs.size && j < xs.size)
        return none()
    }

    override fun partA(input: List<Long>): Either<AdcoError, Long> =
        findFailingChecksum(input, 25).toEither { AdcoError("Not found") }

    override fun partB(input: List<Long>): Either<AdcoError, Long> =
        partA(input).flatMap { target ->
            findSublistSummingTo(input, target)
                .toEither { AdcoError("Sublist not found") }
                .map { it.min()!! + it.max()!! }
        }
}
