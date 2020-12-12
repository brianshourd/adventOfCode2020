package adco

import adco.Day11.SeatGrid
import adco.Day11.SeatingSystem
import adco.Day11.SeatingSystemStrategy
import helpers.assertRight
import kotlin.test.Test
import kotlin.test.assertEquals

class Day11Tests {
    val day11 = Day11()
    val inputRaw = listOf(
        "L.LL.LL.LL",
        "LLLLLLL.LL",
        "L.L.L..L..",
        "LLLL.LL.LL",
        "L.LL.LL.LL",
        "L.LLLLL.LL",
        "..L.L.....",
        "LLLLLLLLLL",
        "L.LLLLLL.L",
        "L.LLLLL.LL",
    ).joinToString("\n")

    val example = SeatGrid(
        10,
        10,
        setOf(
            Pair(0, 0),
            Pair(0, 2),
            Pair(0, 3),
            Pair(0, 5),
            Pair(0, 6),
            Pair(0, 8),
            Pair(0, 9),

            Pair(1, 0),
            Pair(1, 1),
            Pair(1, 2),
            Pair(1, 3),
            Pair(1, 4),
            Pair(1, 5),
            Pair(1, 6),
            Pair(1, 8),
            Pair(1, 9),

            Pair(2, 0),
            Pair(2, 2),
            Pair(2, 4),
            Pair(2, 7),

            Pair(3, 0),
            Pair(3, 1),
            Pair(3, 2),
            Pair(3, 3),
            Pair(3, 5),
            Pair(3, 6),
            Pair(3, 8),
            Pair(3, 9),

            Pair(4, 0),
            Pair(4, 2),
            Pair(4, 3),
            Pair(4, 5),
            Pair(4, 6),
            Pair(4, 8),
            Pair(4, 9),

            Pair(5, 0),
            Pair(5, 2),
            Pair(5, 3),
            Pair(5, 4),
            Pair(5, 5),
            Pair(5, 6),
            Pair(5, 8),
            Pair(5, 9),

            Pair(6, 2),
            Pair(6, 4),

            Pair(7, 0),
            Pair(7, 1),
            Pair(7, 2),
            Pair(7, 3),
            Pair(7, 4),
            Pair(7, 5),
            Pair(7, 6),
            Pair(7, 7),
            Pair(7, 8),
            Pair(7, 9),

            Pair(8, 0),
            Pair(8, 2),
            Pair(8, 3),
            Pair(8, 4),
            Pair(8, 5),
            Pair(8, 6),
            Pair(8, 7),
            Pair(8, 9),

            Pair(9, 0),
            Pair(9, 2),
            Pair(9, 3),
            Pair(9, 4),
            Pair(9, 5),
            Pair(9, 6),
            Pair(9, 8),
            Pair(9, 9),
        )
    )

    @Test
    fun testParser() {
        val output = assertRight(day11.parser(inputRaw))
        assertEquals(example, output)
    }

    @Test
    fun testStep() {
        val system = SeatingSystem(example, 4, SeatingSystemStrategy.NEAREST)
        var expected = inputRaw
        assertEquals(expected, system.toString())

        system.step()
        expected = listOf(
            "#.##.##.##",
            "#######.##",
            "#.#.#..#..",
            "####.##.##",
            "#.##.##.##",
            "#.#####.##",
            "..#.#.....",
            "##########",
            "#.######.#",
            "#.#####.##",
        ).joinToString("\n")
        assertEquals(expected, system.toString())

        system.step()
        expected = listOf(
            "#.LL.L#.##",
            "#LLLLLL.L#",
            "L.L.L..L..",
            "#LLL.LL.L#",
            "#.LL.LL.LL",
            "#.LLLL#.##",
            "..L.L.....",
            "#LLLLLLLL#",
            "#.LLLLLL.L",
            "#.#LLLL.##",
        ).joinToString("\n")
        assertEquals(expected, system.toString())

        system.step()
        expected = listOf(
            "#.##.L#.##",
            "#L###LL.L#",
            "L.#.#..#..",
            "#L##.##.L#",
            "#.##.LL.LL",
            "#.###L#.##",
            "..#.#.....",
            "#L######L#",
            "#.LL###L.L",
            "#.#L###.##",
        ).joinToString("\n")
        assertEquals(expected, system.toString())

        system.step()
        expected = listOf(
            "#.#L.L#.##",
            "#LLL#LL.L#",
            "L.L.L..#..",
            "#LLL.##.L#",
            "#.LL.LL.LL",
            "#.LL#L#.##",
            "..L.L.....",
            "#L#LLLL#L#",
            "#.LLLLLL.L",
            "#.#L#L#.##",
        ).joinToString("\n")
        assertEquals(expected, system.toString())

        system.step()
        expected = listOf(
            "#.#L.L#.##",
            "#LLL#LL.L#",
            "L.#.L..#..",
            "#L##.##.L#",
            "#.#L.LL.LL",
            "#.#L#L#.##",
            "..L.L.....",
            "#L#L##L#L#",
            "#.LLLLLL.L",
            "#.#L#L#.##",
        ).joinToString("\n")
        assertEquals(expected, system.toString())
    }

    @Test
    fun testPartA() {
        val output = assertRight(day11.partA(example))
        val expected = 37
        assertEquals(expected, output)
    }

    @Test
    fun testStabilizeNearest() {
        val input = listOf(
            "LLLLLLLLLL",
            "LLLLLLLLLL",
            "LLLLLLLLLL",
        ).joinToString("\n")
        val grid = assertRight(day11.parser(input))
        val system = SeatingSystem(grid, 4, SeatingSystemStrategy.NEAREST)
        system.stabilize()
        val expected = listOf(
            "#L#L##L#L#",
            "LLLLLLLLLL",
            "#L#L##L#L#",
        ).joinToString("\n")
        assertEquals(expected, system.toString())
    }

    @Test
    fun testStabilizeVisible() {
        val input = listOf(
            "LLLLLLLLLL",
            "LLLLLLLLLL",
            "LLLLLLLLLL",
        ).joinToString("\n")
        val grid = assertRight(day11.parser(input))
        val system = SeatingSystem(grid, 5, SeatingSystemStrategy.VISIBLE)
        system.stabilize()
        val expected = listOf(
            "#L#L##L#L#",
            "LLLLLLLLLL",
            "#L#L##L#L#",
        ).joinToString("\n")
        assertEquals(expected, system.toString())
    }

    @Test
    fun testPartB() {
        val output = assertRight(day11.partB(example))
        val expected = 26
        assertEquals(expected, output)
    }
}
