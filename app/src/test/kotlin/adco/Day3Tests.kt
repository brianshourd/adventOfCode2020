package adco

import helpers.assertRight
import kotlin.test.Test
import kotlin.test.assertEquals

class Day3Tests {
    val day3 = Day3()

    val input = listOf(
        "..##.......",
        "#...#...#..",
        ".#....#..#.",
        "..#.#...#.#",
        ".#...##..#.",
        "..#.##.....",
        ".#.#.#....#",
        ".#........#",
        "#.##...#...",
        "#...##....#",
        ".#..#...#.#",
    )

    @Test
    fun testPartA() {
        val output = assertRight(day3.partA(input))
        val expected = 7L
        assertEquals(expected, output)
    }

    @Test
    fun testCountTrees() {
        val cases = listOf(
            Pair(Pair(1, 1), 2L),
            Pair(Pair(3, 1), 7L),
            Pair(Pair(5, 1), 3L),
            Pair(Pair(7, 1), 4L),
            Pair(Pair(1, 2), 2L),
        )
        cases.forEach { (coords, expected) ->
            val output = day3.countTrees(input, coords.first, coords.second)
            assertEquals(expected, output, "Expected for slope $coords")
        }
    }

    @Test
    fun testPartB() {
        val output = assertRight(day3.partB(input))
        val expected = 336L
        assertEquals(expected, output)
    }
}
