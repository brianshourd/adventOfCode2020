package adco

import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.minus
import kotlinx.collections.immutable.toPersistentSet
import lib.either.Either
import lib.either.asRight
import lib.either.toEither
import lib.option.Option
import lib.option.toOption
import lib.parsers.Parser
import lib.parsers.charP
import lib.parsers.intP
import lib.parsers.manyTill
import lib.parsers.map
import lib.parsers.newlineP
import lib.parsers.noneOfP
import lib.parsers.sepBy1
import lib.parsers.seriesP
import lib.parsers.stringP
import lib.parsers.then
import lib.parsers.thenIgnore

class Day16() : IAdcoProblem<Day16.Input, Long> {
    override val title = "Day 16: Ticket Translation"

    data class Input(
        val rules: PersistentSet<FieldRule>,
        val yourTicket: List<Int>,
        val nearbyTickets: List<List<Int>>,
    )

    data class FieldRule(val name: String, val r1: IntRange, val r2: IntRange) {
        fun isValidFor(x: Int) = r1.contains(x) || r2.contains(x)
    }

    val parseRange: Parser<IntRange> =
        (intP() thenIgnore charP('-') then intP()).map { (x, y) -> x..y }

    val parseFieldRule: Parser<FieldRule> = seriesP(
        (noneOfP(':') manyTill charP(':')).map { it.joinToString("") },
        stringP(": "),
        parseRange,
        stringP(" or "),
        parseRange,
    ).map { (name, _, r1, _, r2) -> FieldRule(name, r1, r2) }

    val parseTicket: Parser<List<Int>> = intP() sepBy1 charP(',')

    override val parser: Parser<Input> = seriesP(
        (parseFieldRule sepBy1 newlineP()).map { it.toPersistentSet() },
        newlineP() then stringP("your ticket:") then newlineP(),
        parseTicket thenIgnore newlineP(),
        newlineP() then stringP("nearby tickets:") then newlineP(),
        parseTicket sepBy1 newlineP(),
    ).map { (rs, _, yt, _, nts) -> Input(rs, yt, nts) }

    fun validForAnyField(rules: PersistentSet<FieldRule>): PersistentSet<Int> {
        val validValues = mutableSetOf<Int>()
        rules.forEach { rule ->
            validValues += rule.r1
            validValues += rule.r2
        }
        return validValues.toPersistentSet()
    }

    fun findValidOrdering(input: Input): Option<List<FieldRule>> {
        val validValues = validForAnyField(input.rules)
        val validTickets = input.nearbyTickets.filter { ticket ->
            ticket.all { validValues.contains(it) }
        }
        val fieldValues = transpose(validTickets)

        // We want to sort these in order of how many possibilities they have, in order to test (and eliminate) the most
        // promising fields first. On my input, this led to a speedup of about 500x
        val labeledFieldValues = fieldValues.zip(fieldValues.indices)
        val fasterLabeledFieldValues = labeledFieldValues.sortedBy { (values, _) ->
            possibleRules(input.rules, values).size
        }
        val newOrder = fasterLabeledFieldValues.map { it.second }
        val fasterFieldValues = fasterLabeledFieldValues.map { it.first }

        return findValidOrderings(
            fasterFieldValues,
            input.rules,
            emptyList()
        ).firstOrNull().toOption().map { misorderedOrdering: List<FieldRule> ->
            // Now we have to un-map the sorting we did above
            misorderedOrdering.indices.map { i ->
                misorderedOrdering[newOrder.indexOf(i)]
            }
        }
    }

    private fun findValidOrderings(
        fieldValues: List<List<Int>>,
        remainingRules: PersistentSet<FieldRule>,
        knownRules: List<FieldRule>
    ): List<List<FieldRule>> {
        if (remainingRules.isEmpty() || fieldValues.isEmpty()) {
            return listOf(knownRules)
        }
        val firstValues = fieldValues[0]
        val remainingValues = fieldValues.drop(1)
        val possibleFirstRules = possibleRules(remainingRules, firstValues)
        return possibleFirstRules.flatMap { possibleRule ->
            findValidOrderings(
                remainingValues,
                remainingRules - possibleRule,
                knownRules + possibleRule
            )
        }
    }

    fun transpose(xss: List<List<Int>>): List<List<Int>> {
        if (xss.isEmpty()) { return emptyList() }
        return xss[0].indices.map { i -> xss.map { it[i] } }
    }

    fun possibleRules(rules: Iterable<FieldRule>, xs: List<Int>): List<FieldRule> =
        rules.filter { rule -> xs.all { rule.isValidFor(it) } }

    fun labelTicket(rules: List<FieldRule>, ticket: List<Int>): Map<String, Int> =
        rules.zip(ticket) { fr, x -> fr.name to x }.toMap()

    override fun partA(input: Input): Either<AdcoError, Long> {
        val validValues = validForAnyField(input.rules)
        return input.nearbyTickets.flatten().filter { !validValues.contains(it) }.sum().toLong().asRight()
    }

    override fun partB(input: Input): Either<AdcoError, Long> =
        findValidOrdering(input).toEither {
            AdcoError("No valid ordering found")
        }.map { ordering ->
            labelTicket(ordering, input.yourTicket)
                .filter { (k, v) -> k.startsWith("departure") }
                .values
                .map { it.toLong() }
                .fold(1L, Long::times)
        }
}
