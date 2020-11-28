package adco

import lib.either.Either
import lib.either.left

class Day1() : ITwoPartProblem {
    override val title: String = "Day 1: Something"
    override val partAProblemText: String =
        """
        Not yet available
        """.trimIndent()

    override val partBProblemText =
        """
        Not yet available
        """.trimIndent()

    override fun partA(input: String): Either<AdcoError, String> =
        left(AdcoError("Not yet implemented: Day1.partA"))

    override fun partB(input: String): Either<AdcoError, String> =
        left(AdcoError("Not yet implemented: Day1.partB"))
}
