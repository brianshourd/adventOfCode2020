package adco

import lib.either.Either
import lib.either.asRight
import lib.parsers.Parser
import lib.parsers.many1P
import lib.parsers.map
import lib.parsers.newlineP
import lib.parsers.oneOfP
import lib.parsers.plus
import lib.parsers.sepBy1
import java.util.BitSet

class Day11() : IAdcoProblem<Day11.SeatGrid, Int> {
    override val title = "Day 11: Seating System"

    data class SeatGrid(
        val rows: Int,
        val cols: Int,
        val seats: Set<Pair<Int, Int>>
    )

    enum class SeatingSystemStrategy { NEAREST, VISIBLE }

    class SeatingSystem(
        val grid: SeatGrid,
        val tolerance: Int,
        val strategy: SeatingSystemStrategy
    ) {
        private val rows = grid.rows
        private val cols = grid.cols
        private val size = rows * cols
        private val seats: BitSet = BitSet(size)
        private var emptySeats: BitSet = BitSet(size)

        // Buffers
        private var filledSeatsBuf: BitSet = BitSet(size)
        private val surroundingSeatsBuf: BitSet = BitSet(size)

        private val calculateSurrounding: (Int) -> Unit =
            if (strategy == SeatingSystemStrategy.NEAREST) ::surroundingNearest else ::surroundingVisible

        init {
            for ((r, c) in this.grid.seats) { seats.set(r * cols + c) }
            emptySeats.or(seats)
        }

        private val directions = listOf(
            Pair(0, 1),
            Pair(1, 1),
            Pair(1, 0),
            Pair(1, -1),
            Pair(0, -1),
            Pair(-1, -1),
            Pair(-1, 0),
            Pair(-1, 1),
        )

        private fun surroundingNearest(x: Int) {
            surroundingSeatsBuf.clear()
            directions.map { (dr, dc) ->
                Pair((x / cols) + dr, (x % cols) + dc)
            }.forEach { (r, c) ->
                if (r >= 0 && r < rows && c >= 0 && c < cols) {
                    surroundingSeatsBuf.set(r * cols + c)
                }
            }
            surroundingSeatsBuf.and(seats)
        }

        private fun surroundingVisible(x: Int) {
            surroundingSeatsBuf.clear()
            val row = x / cols
            val col = x % cols
            for ((dr, dc) in directions) {
                var r = row + dr
                var c = col + dc
                while (r >= 0 && r < rows && c >= 0 && c < cols) {
                    val i = r * cols + c
                    if (seats.get(i)) {
                        surroundingSeatsBuf.set(i)
                        break
                    }
                    r += dr
                    c += dc
                }
            }
        }

        fun step(): SeatingSystem {
            for (i in 0 until size) {
                if (!seats.get(i)) {
                    continue
                }
                calculateSurrounding(i)
                surroundingSeatsBuf.andNot(emptySeats)
                if (emptySeats.get(i)) {
                    filledSeatsBuf.set(i, surroundingSeatsBuf.isEmpty())
                } else {
                    filledSeatsBuf.set(i, surroundingSeatsBuf.cardinality() < tolerance)
                }
            }
            emptySeats = seats.clone() as BitSet
            emptySeats.andNot(filledSeatsBuf)
            return this
        }

        fun stabilize(): SeatingSystem {
            var lastEmptySeats: BitSet = BitSet()
            do {
                lastEmptySeats = emptySeats.clone() as BitSet
                step()
            } while (lastEmptySeats != emptySeats)
            return this
        }

        fun countFilledSeats(): Int = filledSeatsBuf.cardinality()

        override fun toString(): String {
            val sb = StringBuilder(rows * (cols + 1))
            var i = 0
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    if (emptySeats.get(i)) {
                        sb.append('L')
                    } else if (filledSeatsBuf.get(i)) {
                        sb.append('#')
                    } else {
                        sb.append('.')
                    }
                    i++
                }
                sb.append('\n')
            }
            sb.deleteAt(sb.lastIndex)
            return sb.toString()
        }
    }

    override val parser: Parser<SeatGrid> =
        (many1P(oneOfP("L.")) sepBy1 newlineP()).map { lines ->
            val seats = mutableSetOf<Pair<Int, Int>>()
            lines.mapIndexed { r, line ->
                for (c in line.indices) {
                    if (line[c] == 'L') {
                        seats.add(Pair(r, c))
                    }
                }
            }
            SeatGrid(lines.size, lines[0].size, seats.toSet())
        }

    override fun partA(input: SeatGrid): Either<AdcoError, Int> =
        SeatingSystem(input, 4, SeatingSystemStrategy.NEAREST)
            .stabilize().let { it.countFilledSeats() }.asRight()

    override fun partB(input: SeatGrid): Either<AdcoError, Int> =
        SeatingSystem(input, 5, SeatingSystemStrategy.VISIBLE)
            .stabilize().let { it.countFilledSeats() }.asRight()
}
