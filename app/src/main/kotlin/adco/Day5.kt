package adco

import lib.either.Either
import lib.either.asLeft
import lib.either.toEither
import lib.parsers.Parser
import lib.parsers.many1P
import lib.parsers.map
import lib.parsers.newlineP
import lib.parsers.oneOfP
import lib.parsers.sepBy
import lib.parsers.then
import kotlin.math.pow

class Day5() : IAdcoProblem<List<Day5.BoardingPass>, Int> {
    override val title = "Day 5: Binary Boarding"

    enum class Bsp { F, B }

    data class BoardingPass(val row: List<Bsp>, val col: List<Bsp>) {
        private fun partition(range: IntRange, bsp: Bsp): IntRange {
            val size = range.endInclusive + 1 - range.start
            return when (bsp) {
                Bsp.F -> IntRange(range.start, range.endInclusive - (size / 2))
                Bsp.B -> IntRange(range.start + (size / 2), range.endInclusive)
            }
        }

        private fun readBsp(l: List<Bsp>): Int =
            l.fold(IntRange(0, (2.0).pow(l.size).toInt() - 1), ::partition).start

        val rowId: Int get() = readBsp(row)
        val colId: Int get() = readBsp(col)
        val seatId: Int get() = (8 * rowId) + colId
    }

    private val parseBspRow: Parser<Bsp> =
        oneOfP("BF").map { if (it == 'B') Bsp.B else Bsp.F }

    private val parseRow: Parser<List<Bsp>> = many1P(parseBspRow)

    private val parseBspCol: Parser<Bsp> =
        oneOfP("LR").map { if (it == 'R') Bsp.B else Bsp.F }

    private val parseCol: Parser<List<Bsp>> = many1P(parseBspCol)

    val parseBoardingPass: Parser<BoardingPass> =
        (parseRow then parseCol).map { (r, c) -> BoardingPass(r, c) }

    override val parser: Parser<List<BoardingPass>> = parseBoardingPass sepBy newlineP()

    override fun partA(input: List<BoardingPass>): Either<AdcoError, Int> =
        input.map { it.seatId }.max().toEither {
            AdcoError("No largest seat id found")
        }

    override fun partB(input: List<BoardingPass>): Either<AdcoError, Int> {
        val seats = input.map { it.seatId }.toSet()
        if (seats.size <= 2) {
            return AdcoError("Not enough seats").asLeft()
        } else {
            return IntRange(seats.min()!! + 1, seats.max()!! - 1)
                .find { !seats.contains(it) }
                .toEither { AdcoError("No missing seat found") }
        }
    }
}
