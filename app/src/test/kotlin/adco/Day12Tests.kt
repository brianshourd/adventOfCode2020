package adco

import adco.Day12.Instruction
import adco.Day12.Ship
import adco.Day12.Vector
import helpers.assertRight
import kotlin.test.Test
import kotlin.test.assertEquals

class Day12Tests {
    val day12 = Day12()
    val inputRaw = listOf(
        "F10",
        "N3",
        "F7",
        "R90",
        "F11",
    ).joinToString("\n")

    @Test
    fun testParser() {
        val output = assertRight(day12.parser(inputRaw))
        val expected = listOf(
            Instruction.Forward(10),
            Instruction.North(3),
            Instruction.Forward(7),
            Instruction.Rotate(1),
            Instruction.Forward(11),
        )
        assertEquals(expected, output)
    }

    @Test
    fun testMoveShip() {
        val start = Ship(Vector(0, 0), Vector(1, 0), Vector(10, 1))
        val cases = listOf(
            Instruction.North(5) to Ship(Vector(0, 5), Vector(1, 0), Vector(10, 1)),
            Instruction.South(4) to Ship(Vector(0, -4), Vector(1, 0), Vector(10, 1)),
            Instruction.East(3) to Ship(Vector(3, 0), Vector(1, 0), Vector(10, 1)),
            Instruction.West(2) to Ship(Vector(-2, 0), Vector(1, 0), Vector(10, 1)),
            Instruction.Rotate(1) to Ship(Vector(0, 0), Vector(0, -1), Vector(10, 1)),
            Instruction.Rotate(2) to Ship(Vector(0, 0), Vector(-1, 0), Vector(10, 1)),
            Instruction.Rotate(3) to Ship(Vector(0, 0), Vector(0, 1), Vector(10, 1)),
            Instruction.Rotate(0) to Ship(Vector(0, 0), Vector(1, 0), Vector(10, 1)),
            Instruction.Forward(3) to Ship(Vector(3, 0), Vector(1, 0), Vector(10, 1)),
        )
        for ((instruction, expected) in cases) {
            assertEquals(expected, day12.moveShip(start, instruction))
        }
    }

    @Test
    fun testPartA1() {
        val input = assertRight(day12.parser(inputRaw))
        val output = assertRight(day12.partA(input))
        val expected = 25
        assertEquals(expected, output)
    }

    @Test
    fun testPartA2() {
        val input = assertRight(
            day12.parser(
                listOf(
                    "F10", // 10,0 / 1, 0
                    "N5", // 10, 5
                    "E7", // 17, 5
                    "L90", // 17, 5 / 0, 1
                    "F2", // 17, 7
                    "S2", // 17, 5
                    "L90", // 17, 5 / -1, 0
                    "F2", // 15, 5
                ).joinToString("\n")
            )
        )
        val output = assertRight(day12.partA(input))
        val expected = 20
        assertEquals(expected, output)
    }

    @Test
    fun testMoveWaypoint() {
        val start = Ship(Vector(0, 0), Vector(1, 0), Vector(10, 1))
        val cases = listOf(
            Instruction.North(5) to Ship(Vector(0, 0), Vector(1, 0), Vector(10, 6)),
            Instruction.South(4) to Ship(Vector(0, 0), Vector(1, 0), Vector(10, -3)),
            Instruction.East(3) to Ship(Vector(0, 0), Vector(1, 0), Vector(13, 1)),
            Instruction.West(2) to Ship(Vector(0, 0), Vector(1, 0), Vector(8, 1)),
            Instruction.Rotate(1) to Ship(Vector(0, 0), Vector(1, 0), Vector(1, -10)),
            Instruction.Rotate(2) to Ship(Vector(0, 0), Vector(1, 0), Vector(-10, -1)),
            Instruction.Rotate(3) to Ship(Vector(0, 0), Vector(1, 0), Vector(-1, 10)),
            Instruction.Rotate(0) to Ship(Vector(0, 0), Vector(1, 0), Vector(10, 1)),
            Instruction.Forward(3) to Ship(Vector(30, 3), Vector(1, 0), Vector(10, 1)),
        )
        for ((instruction, expected) in cases) {
            assertEquals(expected, day12.moveWaypoint(start, instruction))
        }
    }

    @Test
    fun testPartB() {
        val input = assertRight(day12.parser(inputRaw))
        val output = assertRight(day12.partB(input))
        val expected = 286
        assertEquals(expected, output)
    }
}
