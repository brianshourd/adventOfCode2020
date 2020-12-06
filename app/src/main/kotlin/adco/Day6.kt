package adco

import lib.either.Either
import lib.either.asRight
import lib.parsers.Parser
import lib.parsers.many1P
import lib.parsers.map
import lib.parsers.newlineP
import lib.parsers.noneOfP
import lib.parsers.optional
import lib.parsers.sepBy
import lib.parsers.thenIgnore

class Day6() : IAdcoProblem<List<Day6.GroupAnswers>, Int> {
    override val title = "Day 6: Custom Customs"

    data class GroupAnswers(val answers: List<Set<Char>>)

    val parseAnswers: Parser<Set<Char>> =
        (many1P(noneOfP('\n')) thenIgnore newlineP().optional()).map(List<Char>::toSet)

    val parseGroupAnswers: Parser<GroupAnswers> = many1P(parseAnswers).map(::GroupAnswers)

    override val parser: Parser<List<GroupAnswers>> = parseGroupAnswers sepBy newlineP()

    override fun partA(input: List<GroupAnswers>): Either<AdcoError, Int> =
        input.map { group ->
            group.answers.fold(emptySet(), Set<Char>::plus)
        }.fold(0) { acc, s -> acc + s.size }.asRight()

    override fun partB(input: List<GroupAnswers>): Either<AdcoError, Int> =
        input.map { group ->
            group.answers.reduceOrNull(Set<Char>::intersect) ?: emptySet()
        }.fold(0) { acc, s -> acc + s.size }.asRight()
}
