package adco

import lib.either.Either
import lib.either.asRight
import lib.option.Option
import lib.option.none
import lib.option.some
import lib.option.somes
import lib.parsers.Parser
import lib.parsers.charP
import lib.parsers.choiceP
import lib.parsers.intP
import lib.parsers.map
import lib.parsers.newlineP
import lib.parsers.plus
import lib.parsers.sepBy1
import lib.parsers.seriesP
import java.math.BigInteger

class Day13() : IAdcoProblem<Day13.Input, BigInteger> {
    override val title = "Day 13: Shuttle Search"

    data class Input(
        val arrivalTime: Int,
        val busSchedule: List<Option<Int>>
    )

    val parseBusSchedule: Parser<List<Option<Int>>> =
        choiceP(intP().map { some(it) }, charP('x').map { none<Int>() }) sepBy1 charP(',')

    override val parser: Parser<Input> =
        seriesP(intP(), newlineP(), parseBusSchedule).map { (t, _, bs) -> Input(t, bs) }

    data class Mod(val r: BigInteger, val m: BigInteger)
    fun addConstraint(y: Mod, x: Mod): Mod {
        var i = 0.toBigInteger()
        while (i < x.m) {
            val attempt = (i * y.m) + y.r
            if (attempt % x.m == x.r) { return Mod(attempt, (y.m * x.m) / y.m.gcd(x.m)) }
            i++
        }
        return Mod(0.toBigInteger(), 1.toBigInteger()) // Can't happen
    }

    override fun partA(input: Input): Either<AdcoError, BigInteger> =
        input.busSchedule.somes().map { Pair(it, it - (input.arrivalTime % it)) }
            .minByOrNull { it.second }!!.let { it.first * it.second }.toBigInteger().asRight()

    override fun partB(input: Input): Either<AdcoError, BigInteger> =
        input.busSchedule.mapIndexed { ix, idOpt ->
            idOpt.map { Mod(Math.floorMod(it - ix, it).toBigInteger(), it.toBigInteger()) }
        }.somes().sortedBy { -it.m }.reduce(::addConstraint).let { it.r }.asRight()
}
