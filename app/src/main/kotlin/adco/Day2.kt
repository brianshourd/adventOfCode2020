package adco

import lib.either.Either
import lib.either.asRight
import lib.parsers.Parser
import lib.parsers.anyCharP
import lib.parsers.charP
import lib.parsers.ignoreThen
import lib.parsers.intP
import lib.parsers.map
import lib.parsers.named
import lib.parsers.newlineP
import lib.parsers.restOfLineP
import lib.parsers.sepBy
import lib.parsers.seriesP
import lib.parsers.spaceP
import lib.parsers.stringP
import lib.parsers.then
import lib.parsers.thenIgnore

class Day2() : IAdcoProblem<List<Day2.PasswordLine>, Int> {
    override val title = "Day 2: Password Philosophy"

    // Types
    data class PasswordLine(
        val range: IntRange,
        val char: Char,
        val password: String
    )

    // Parsing
    val rangeParser: Parser<IntRange> =
        (intP() thenIgnore charP('-') then intP())
            .map { (s, e) -> s..e }.named("rangeParser")

    val passwordLineParser: Parser<PasswordLine> =
        seriesP(
            rangeParser,
            spaceP() ignoreThen anyCharP() thenIgnore stringP(": "),
            restOfLineP()
        ).map { (r, c, p) -> PasswordLine(r, c, p) }.named("passwordLineParser")

    override val parser: Parser<List<PasswordLine>> = passwordLineParser sepBy newlineP()

    // Implementation
    fun passwordIsValidA(pwLine: PasswordLine): Boolean =
        pwLine.range.contains(pwLine.password.count { it == pwLine.char })

    fun passwordIsValidB(pwLine: PasswordLine): Boolean {
        val (range, char, password) = pwLine
        val i = range.start - 1
        val j = range.endInclusive - 1
        if (i < 0 || i >= password.length || j < 0 || j >= password.length) {
            return false
        }
        return (password[i] == char) xor (password[j] == char)
    }

    override fun partA(input: List<PasswordLine>): Either<AdcoError, Int> =
        input.count(::passwordIsValidA).asRight()

    override fun partB(input: List<PasswordLine>): Either<AdcoError, Int> =
        input.count(::passwordIsValidB).asRight()
}
