package adco

import lib.either.Either
import lib.either.asRight
import lib.parsers.Parser
import lib.parsers.failureP
import lib.parsers.flatMap
import lib.parsers.intP
import lib.parsers.lift
import lib.parsers.newlineP
import lib.parsers.oneOfP
import lib.parsers.plus
import lib.parsers.sepBy

class Day12() : IAdcoProblem<List<Day12.Instruction>, Int> {
    override val title = "Day 12: Rain Risk"

    sealed class Instruction() {
        data class North(val n: Int) : Instruction()
        data class South(val n: Int) : Instruction()
        data class East(val n: Int) : Instruction()
        data class West(val n: Int) : Instruction()
        data class Rotate(val n: Int) : Instruction() // n is number of 90 degree turns clockwise
        data class Forward(val n: Int) : Instruction()
    }

    data class Vector(val x: Int, val y: Int) {
        operator fun plus(v: Vector): Vector = Vector(x + v.x, y + v.y)
        operator fun times(s: Int): Vector = Vector(x * s, y * s)
        // Rotate clockwise 90 degrees, n times
        fun rotate(n: Int): Vector =
            if (n <= 0) this else Vector(y, -x).rotate((n % 4) - 1)
        fun manhattanMagnitude(): Int = Math.abs(x) + Math.abs(y)
    }

    data class Ship(val position: Vector, val bearing: Vector, val waypoint: Vector)

    val parseInstruction: Parser<Instruction> =
        (oneOfP("NSEWLRF") + intP()).flatMap { (c, n) ->
            when (c) {
                'N' -> lift(Instruction.North(n))
                'S' -> lift(Instruction.South(n))
                'E' -> lift(Instruction.East(n))
                'W' -> lift(Instruction.West(n))
                'L' -> lift(Instruction.Rotate((4 - (n % 360) / 90) % 4))
                'R' -> lift(Instruction.Rotate((n % 360) / 90))
                'F' -> lift(Instruction.Forward(n))
                else -> failureP("Unexpected character $c")
            }
        }

    fun moveShip(ship: Ship, i: Instruction): Ship =
        when (i) {
            is Instruction.North -> ship.copy(position = ship.position + Vector(0, i.n))
            is Instruction.South -> ship.copy(position = ship.position + Vector(0, -i.n))
            is Instruction.East -> ship.copy(position = ship.position + Vector(i.n, 0))
            is Instruction.West -> ship.copy(position = ship.position + Vector(-i.n, 0))
            is Instruction.Rotate -> ship.copy(bearing = ship.bearing.rotate(i.n))
            is Instruction.Forward -> ship.copy(position = ship.position + (ship.bearing * i.n))
        }

    fun moveWaypoint(ship: Ship, i: Instruction): Ship =
        when (i) {
            is Instruction.North -> ship.copy(waypoint = ship.waypoint + Vector(0, i.n))
            is Instruction.South -> ship.copy(waypoint = ship.waypoint + Vector(0, -i.n))
            is Instruction.East -> ship.copy(waypoint = ship.waypoint + Vector(i.n, 0))
            is Instruction.West -> ship.copy(waypoint = ship.waypoint + Vector(-i.n, 0))
            is Instruction.Rotate -> ship.copy(waypoint = ship.waypoint.rotate(i.n))
            is Instruction.Forward -> ship.copy(position = ship.position + (ship.waypoint * i.n))
        }

    override val parser: Parser<List<Instruction>> = parseInstruction sepBy newlineP()

    val start = Ship(Vector(0, 0), Vector(1, 0), Vector(10, 1))

    override fun partA(input: List<Instruction>): Either<AdcoError, Int> =
        input.fold(start, ::moveShip).position.manhattanMagnitude().asRight()

    override fun partB(input: List<Instruction>): Either<AdcoError, Int> =
        input.fold(start, ::moveWaypoint).position.manhattanMagnitude().asRight()
}
