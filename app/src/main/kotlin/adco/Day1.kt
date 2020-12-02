package adco

import lib.either.Either
import lib.either.flatMap
import lib.either.toEither
import lib.option.Option
import lib.option.none
import lib.option.some
import lib.parsers.Parser
import lib.parsers.intP
import lib.parsers.newlineP
import lib.parsers.sepBy

class Day1() : IAdcoProblem {
    override val title: String = "Day 1: Report Repair"
    override val partAProblemText: String =
        """
        After saving Christmas five years in a row, you've decided to take a vacation at a nice resort on a tropical island. Surely, Christmas will go on without you.

        The tropical island has its own currency and is entirely cash-only. The gold coins used there have a little picture of a starfish; the locals just call them stars. None of the currency exchanges seem to have heard of them, but somehow, you'll need to find fifty of these coins by the time you arrive so you can pay the deposit on your room.

        To save your vacation, you need to get all fifty stars by December 25th.

        Collect stars by solving puzzles. Two puzzles will be made available on each day in the Advent calendar; the second puzzle is unlocked when you complete the first. Each puzzle grants one star. Good luck!

        Before you leave, the Elves in accounting just need you to fix your expense report (your puzzle input); apparently, something isn't quite adding up.

        Specifically, they need you to find the two entries that sum to 2020 and then multiply those two numbers together.

        For example, suppose your expense report contained the following:

        1721
        979
        366
        299
        675
        1456

        In this list, the two entries that sum to 2020 are 1721 and 299. Multiplying them together produces 1721 * 299 = 514579, so the correct answer is 514579.

        Of course, your expense report is much larger. Find the two entries that sum to 2020; what do you get if you multiply them together?
        """.trimIndent()

    override val partBProblemText =
        """
        The Elves in accounting are thankful for your help; one of them even offers you a starfish coin they had left over from a past vacation. They offer you a second one if you can find three numbers in your expense report that meet the same criteria.

        Using the above example again, the three entries that sum to 2020 are 979, 366, and 675. Multiplying them together produces the answer, 241861950.

        In your expense report, what is the product of the three entries that sum to 2020?
        """.trimIndent()

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

    val parser: Parser<List<Int>> = intP() sepBy newlineP()

    fun parseInput(input: String): Either<AdcoError, List<Int>> =
        parser.parse(input).mapLeft { AdcoError("Error parsing input", it) }

    fun List<Int>.product() = this.fold(1, Int::times)

    override fun partA(input: String): Either<AdcoError, String> =
        parseInput(input).flatMap { xs ->
            findNSummingTo(xs.sorted(), 2020, 2).toEither {
                AdcoError("No pair found summing to 2020")
            }.map { it.product().toString() }
        }

    override fun partB(input: String): Either<AdcoError, String> =
        parseInput(input).flatMap { xs ->
            findNSummingTo(xs.sorted(), 2020, 3).toEither {
                AdcoError("No triple found summing to 2020")
            }.map { it.product().toString() }
        }
}
