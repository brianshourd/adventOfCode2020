package adco

import lib.either.Either
import lib.either.toEither
import lib.option.Option
import lib.option.none
import lib.option.some
import lib.parsers.Parser
import lib.parsers.intP
import lib.parsers.newlineP
import lib.parsers.sepBy

class Day1() : IAdcoProblem<List<Int>, Int> {
    override val title = "Day 1: Report Repair"

    // Parsing
    override val parser: Parser<List<Int>> = intP() sepBy newlineP()

    // Implementation
    fun findNSummingTo(
        sortedCandidates: List<Int>,
        sum: Int,
        n: Int
    ): Option<List<Int>> {
        if (n <= 0) {
            if (sum == 0) {
                return some(emptyList())
            } else {
                return none()
            }
        }
        if (sortedCandidates.size == 0) {
            return none()
        }
        val x = sortedCandidates[0]
        if (x > sum) {
            return none()
        } else {
            return findNSummingTo(sortedCandidates.drop(1), sum - x, n - 1).fold(
                { xs: List<Int> -> some(xs + x) },
                { findNSummingTo(sortedCandidates.drop(1), sum, n) }
            )
        }
    }

    fun List<Int>.product() = this.fold(1, Int::times)

    override fun partA(input: List<Int>): Either<AdcoError, Int> =
        findNSummingTo(input.sorted(), 2020, 2).toEither {
            AdcoError("No pair found summing to 2020")
        }.map { it.product() }

    override fun partB(input: List<Int>): Either<AdcoError, Int> =
        findNSummingTo(input.sorted(), 2020, 3).toEither {
            AdcoError("No triple found summing to 2020")
        }.map { it.product() }
}
