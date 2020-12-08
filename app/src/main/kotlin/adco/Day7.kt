package adco

import lib.either.Either
import lib.either.asRight
import lib.option.getOrElse
import lib.option.toOption
import lib.parsers.Parser
import lib.parsers.anyCharP
import lib.parsers.charP
import lib.parsers.intP
import lib.parsers.manyTill
import lib.parsers.map
import lib.parsers.newlineP
import lib.parsers.optional
import lib.parsers.or
import lib.parsers.plus
import lib.parsers.sepBy
import lib.parsers.sepBy1
import lib.parsers.seriesP
import lib.parsers.spaceP
import lib.parsers.stringP
import lib.parsers.then
import lib.parsers.thenIgnore

private typealias LuggageRules = Map<String, Set<Day7.ContainReq>>

class Day7() : IAdcoProblem<LuggageRules, Int> {
    override val title = "Day 7: Handy Haversacks"

    // Parsing
    data class ContainReq(val count: Int, val color: String)

    val parseColor: Parser<String> = seriesP(
        anyCharP() manyTill stringP(" bag"),
        stringP(" bag") + charP('s').optional(),
    ).map { (color, _) -> color.joinToString("") }

    val parseContainReq: Parser<ContainReq> = seriesP(
        intP(),
        spaceP(),
        parseColor,
    ).map { (n, _, color) -> ContainReq(n, color) }

    val parseContainReqs: Parser<Set<ContainReq>> =
        (
            ((parseContainReq sepBy1 stringP(", ")) or stringP("no other bags"))
                thenIgnore charP('.')
            ).map { it.fold({ it.toSet() }, { emptySet() }) }

    val parseLuggageRule: Parser<Pair<String, Set<ContainReq>>> =
        parseColor thenIgnore stringP(" contain ") then parseContainReqs

    override val parser: Parser<LuggageRules> =
        (parseLuggageRule sepBy newlineP()).map { it.toMap() }

    // Implementation
    // Build the reverse mapping - from a bag color to the colors of bags that
    // are allowed to contain it directly
    @OptIn(kotlin.ExperimentalStdlibApi::class)
    fun buildCanContainMap(rules: LuggageRules): Map<String, Set<String>> =
        buildMap(rules.size) {
            rules.forEach { (color, reqs) ->
                reqs.forEach { req ->
                    this[req.color] = (this[req.color] ?: emptySet()) + color
                }
            }
        }

    fun findBagsContaining(canContainMap: Map<String, Set<String>>, color: String): Set<String> {
        val containing = canContainMap[color] ?: emptySet()
        return containing + containing.flatMap { findBagsContaining(canContainMap, it) }
    }

    fun countBagsContainedIn(rules: LuggageRules, containingColor: String): Int {
        // Memoize this function, for repeated lookups
        val memoTable = mutableMapOf<String, Int>()

        fun countIn(color: String): Int =
            memoTable[color].toOption().getOrElse {
                val contained = rules[color] ?: emptySet()
                val containedAndLower = contained.map { req ->
                    req.count * (1 + countIn(req.color))
                }.fold(0, Int::plus)
                memoTable[color] = containedAndLower
                containedAndLower
            }

        return countIn(containingColor)
    }

    override fun partA(input: LuggageRules): Either<AdcoError, Int> =
        buildCanContainMap(input).let { findBagsContaining(it, "shiny gold") }.size.asRight()

    override fun partB(input: LuggageRules): Either<AdcoError, Int> =
        countBagsContainedIn(input, "shiny gold").asRight()
}
