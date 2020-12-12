package lib.parsers

import lib.either.Either
import lib.either.asLeft
import lib.either.asRight
import lib.either.flatMap
import lib.either.left
import lib.either.right
import lib.either.traverse
import lib.either.tryEither
import lib.hlist.HList10
import lib.hlist.HList2
import lib.hlist.HList3
import lib.hlist.HList4
import lib.hlist.HList5
import lib.hlist.HList6
import lib.hlist.HList7
import lib.hlist.HList8
import lib.hlist.HList9
import lib.hlist.hlist
import lib.option.Option
import lib.option.getOrElse
import lib.option.none
import lib.option.some

// Individual character parsers

// Match and consume exactly one of the given characters
fun oneOfP(cs: Set<Char>): Parser<Char> = Parser.OneOfP(cs)
fun oneOfP(vararg cs: Char): Parser<Char> = oneOfP(cs.toSet())
fun oneOfP(cs: String): Parser<Char> = oneOfP(cs.toSet())

// Match and consume any character except the given characters
fun noneOfP(cs: Set<Char>): Parser<Char> = Parser.NoneOfP(cs)
fun noneOfP(vararg cs: Char): Parser<Char> = noneOfP(cs.toSet())
fun noneOfP(cs: String): Parser<Char> = noneOfP(cs.toSet())

// Match and consume all consecutive whitespace characters
fun spacesP(): Parser<String> = manyP(satisfyP(Char::isWhitespace))
    .map { it.joinToString("") }.named("spacesP")

// Match and consume exactly one whitespace character
fun spaceP(): Parser<Char> = satisfyP(Char::isWhitespace).named("spaceP")

// Match and consume an end-of-line characters/string, returning '\n'
fun newlineP(): Parser<Char> = (charP('\n') or stringP("\r\n"))
    .map { '\n' }.named("newlineP")

// Match and consume any upper-case character
fun upperP(): Parser<Char> = satisfyP(Char::isUpperCase).named("upperP")

// Match and consume any lower-case character
fun lowerP(): Parser<Char> = satisfyP(Char::isLowerCase).named("lowerP")

// Match and consume any digit character
fun digitP(): Parser<Char> = satisfyP(Char::isDigit).named("digitP")

// Match and consume exactly the given character
fun charP(c: Char): Parser<Char> = Parser.CharP(c)

// Match and consume any one character
fun anyCharP(): Parser<Char> = Parser.AnyCharP

// Match and consume any one character satisfying the predicate
fun satisfyP(f: (Char) -> Boolean): Parser<Char> = Parser.SatisfyP(f)

// Multi-character parsers

// Match and consume exactly the given string
fun stringP(s: String): Parser<String> = Parser.StringP(s)

// Match and consume as many digits as present, and parse into an integer
fun intP(): Parser<Int> = Parser.IntP

// Match and consume as many digits as present, and parse into an integer
fun longP(): Parser<Long> = Parser.LongP

// Match and consume the rest of the line (either newline, or eof). Does not consume newlines
fun restOfLineP(): Parser<String> = Parser.RestOfLineP

// Combinators

// Match the first of the given parsers that matches
fun <T> choiceP(ps: List<Parser<T>>): Parser<T> = Parser.ChoiceP(ps)
fun <T> choiceP(vararg ps: Parser<T>): Parser<T> = choiceP(ps.toList())

// Match 0 or more instances of the given parser
fun <T> manyP(p: Parser<T>): Parser<List<T>> = Parser.ManyP(p)

// Match 1 or more instances of the given parser
fun <T> many1P(p: Parser<T>): Parser<List<T>> = Parser.Many1P(p)

// Match n instances of the given parser
infix fun <T> Parser<T>.repeated(n: Int): Parser<List<T>> = Parser.RepeatedP(this, n)

// Match open, then p, then closeP
fun <T> between(openP: Parser<*>, closeP: Parser<*>, p: Parser<T>): Parser<T> =
    (openP ignoreThen p thenIgnore closeP).named("betweenP")

// Match if present, otherwise None
fun <T> Parser<T>.optional(): Parser<Option<T>> = Parser.OptionalP(this)

// Match 0 or more instances of this parser, until end is a match
infix fun <T> Parser<T>.manyTill(end: Parser<*>): Parser<List<T>> = Parser.ManyTillP(this, end)

// Match 0 or more instances of this parser, separated by the sep parser
fun <T, S> sepByP(p: Parser<T>, sep: Parser<S>, allowTrailing: Boolean = true): Parser<List<T>> =
    Parser.SepByP(p, sep, allowTrailing)
infix fun <T, S> Parser<T>.sepBy(s: Parser<S>): Parser<List<T>> = Parser.SepByP(this, s)

// Match 1 or more instances of this parser, separated by the sep parser
fun <T, S> sepBy1P(p: Parser<T>, sep: Parser<S>, allowTrailing: Boolean = true): Parser<List<T>> =
    Parser.SepBy1P(p, sep, allowTrailing)
infix fun <T, S> Parser<T>.sepBy1(s: Parser<S>): Parser<List<T>> = Parser.SepBy1P(this, s)

// Do the first parser, then the second parser, returning a Pair
operator fun <T, S> Parser<T>.plus(s: Parser<S>): Parser<Pair<T, S>> = Parser.PlusP(this, s)
infix fun <T, S> Parser<T>.then(s: Parser<S>): Parser<Pair<T, S>> = Parser.PlusP(this, s)

// Do the first parser, then the second parser, but only return the output of the second
infix fun <T, S> Parser<T>.ignoreThen(s: Parser<S>): Parser<S> = Parser.PlusP(this, s).map { it.second }

// Do the first parser, then the second parser, but only return the output of the first
infix fun <T, S> Parser<T>.thenIgnore(s: Parser<S>): Parser<T> = Parser.PlusP(this, s).map { it.first }

// Do the first parser, but if that doesn't match, then do the second. Like choice, but with different types
infix fun <T, S> Parser<T>.or(s: Parser<S>): Parser<Either<T, S>> = Parser.OrP(this, s)

// Name a parser something else, for debugging
fun <T> Parser<T>.named(name: String): Parser<T> = Parser.NamedP(this, name)

// Functor/Monad functionality for combinations

fun <T, S> Parser<T>.map(f: (T) -> S): Parser<S> = Parser.MapP(this, f)
fun <T, S> Parser<T>.flatMap(f: (T) -> Parser<S>): Parser<S> = Parser.FlatMapP(this, f)
fun <T> lift(v: T): Parser<T> = Parser.LiftP(v)
fun <T> failureP(msg: String): Parser<T> = Parser.Failure(msg)

/**
 * Avoid nested flatmaps when running a series of parsers
 *
 * Example:
 * ```
 * seriesP(
 *   intP(),
 *   charP(':'),
 *   intP()
 * ).map { (n: Int, _: Char, d: Int) -> Ratio(n, d) }
 * ```
 * Note that these HListN implementations support flat destructuring syntaxes,
 * so they can be transparent to the caller
 */
fun <T1, T2> seriesP(p1: Parser<T1>, p2: Parser<T2>): Parser<HList2<T1, T2>> =
    p1.flatMap { t1 -> p2.map { t2 -> hlist(t1, t2) } }
fun <T1, T2, T3> seriesP(p1: Parser<T1>, p2: Parser<T2>, p3: Parser<T3>): Parser<HList3<T1, T2, T3>> =
    p1.flatMap { t1 -> seriesP(p2, p3).map { it.prepend(t1) } }
fun <T1, T2, T3, T4> seriesP(p1: Parser<T1>, p2: Parser<T2>, p3: Parser<T3>, p4: Parser<T4>): Parser<HList4<T1, T2, T3, T4>> =
    p1.flatMap { t1 -> seriesP(p2, p3, p4).map { it.prepend(t1) } }
fun <T1, T2, T3, T4, T5> seriesP(p1: Parser<T1>, p2: Parser<T2>, p3: Parser<T3>, p4: Parser<T4>, p5: Parser<T5>): Parser<HList5<T1, T2, T3, T4, T5>> =
    p1.flatMap { t1 -> seriesP(p2, p3, p4, p5).map { it.prepend(t1) } }
fun <T1, T2, T3, T4, T5, T6> seriesP(p1: Parser<T1>, p2: Parser<T2>, p3: Parser<T3>, p4: Parser<T4>, p5: Parser<T5>, p6: Parser<T6>): Parser<HList6<T1, T2, T3, T4, T5, T6>> =
    p1.flatMap { t1 -> seriesP(p2, p3, p4, p5, p6).map { it.prepend(t1) } }
fun <T1, T2, T3, T4, T5, T6, T7> seriesP(p1: Parser<T1>, p2: Parser<T2>, p3: Parser<T3>, p4: Parser<T4>, p5: Parser<T5>, p6: Parser<T6>, p7: Parser<T7>): Parser<HList7<T1, T2, T3, T4, T5, T6, T7>> =
    p1.flatMap { t1 -> seriesP(p2, p3, p4, p5, p6, p7).map { it.prepend(t1) } }
fun <T1, T2, T3, T4, T5, T6, T7, T8> seriesP(p1: Parser<T1>, p2: Parser<T2>, p3: Parser<T3>, p4: Parser<T4>, p5: Parser<T5>, p6: Parser<T6>, p7: Parser<T7>, p8: Parser<T8>): Parser<HList8<T1, T2, T3, T4, T5, T6, T7, T8>> =
    p1.flatMap { t1 -> seriesP(p2, p3, p4, p5, p6, p7, p8).map { it.prepend(t1) } }
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> seriesP(p1: Parser<T1>, p2: Parser<T2>, p3: Parser<T3>, p4: Parser<T4>, p5: Parser<T5>, p6: Parser<T6>, p7: Parser<T7>, p8: Parser<T8>, p9: Parser<T9>): Parser<HList9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> =
    p1.flatMap { t1 -> seriesP(p2, p3, p4, p5, p6, p7, p8, p9).map { it.prepend(t1) } }
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> seriesP(p1: Parser<T1>, p2: Parser<T2>, p3: Parser<T3>, p4: Parser<T4>, p5: Parser<T5>, p6: Parser<T6>, p7: Parser<T7>, p8: Parser<T8>, p9: Parser<T9>, p10: Parser<T10>): Parser<HList10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> =
    p1.flatMap { t1 -> seriesP(p2, p3, p4, p5, p6, p7, p8, p9, p10).map { it.prepend(t1) } }

private fun formatParserExceptionMessage(
    input: String,
    loc: Int,
    parser: Parser<*>,
    msg: String? = null,
    underlying: Exception? = null
): String {
    // Only show surrounding 20 characters at most, for clarity
    var locStr = loc.toString()
    val inputSelection = if (input.length > 20) {
        if (loc > input.length) {
            // Shouldn't happen, but just take
            locStr += " (out of range somehow)"
            input.substring(0, 20) + "..."
        } else if (loc - 10 < 0) {
            // Just take first 20 characters
            input.substring(0, 20) + "..."
        } else if (loc + 10 >= input.length) {
            // Just take last 20 characters
            locStr += " (${loc - (input.length - 20)})"
            "..." + input.substring(input.length - 20)
        } else {
            // Take 20 characters surrounding
            locStr += " (10)"
            "..." + input.substring(loc - 10, loc + 10) + "..."
        }
    } else {
        input
    }
    var ret = "Error parsing \"$inputSelection\" at location $locStr using ${parser.getName()}"
    if (msg != null) {
        ret += ": $msg"
    }
    if (underlying != null) {
        ret += "; $msg"
    }
    return ret
}

data class ParserException(
    val input: String,
    val loc: Int,
    val parser: Parser<*>,
    val msg: String? = null,
    val underlying: Exception? = null
) : Exception(formatParserExceptionMessage(input, loc, parser, msg, underlying)) {
    override fun toString(): String = this.message!!
}

data class PartialParse<out T>(val v: T, val loc: Int)
typealias ParseResult<T> = Either<ParserException, PartialParse<T>>

sealed class Parser<out T>() {
    operator fun invoke(input: String): Either<ParserException, T> = parse(input)

    fun parse(input: String): Either<ParserException, T> =
        parsePartial(input, 0).flatMap { res ->
            if (res.loc != input.length) {
                left(ParserException(input, res.loc, this, "Input was remaining after parse"))
            } else {
                right(res.v)
            }
        }

    abstract fun parsePartial(
        input: String,
        pos: Int
    ): ParseResult<T>

    // Used for filling in detailed error messages
    internal abstract fun getName(): String

    protected fun extractChar(input: String, pos: Int): Either<ParserException, Char> =
        if (pos < 0) {
            left(ParserException(input, pos, this, "Position less than 0"))
        } else if (pos >= input.length) {
            left(ParserException(input, pos, this, "End of input reached"))
        } else {
            right(input[pos])
        }

    internal data class CharP(val c: Char) : Parser<Char>() {
        override fun getName(): String = "charP($c)"
        override fun parsePartial(input: String, pos: Int): ParseResult<Char> =
            extractChar(input, pos).flatMap { x ->
                if (x == c) {
                    right(PartialParse(x, pos + 1))
                } else {
                    left(ParserException(input, pos, this, "Non-matching character '$x' not '$c'"))
                }
            }
    }

    internal object AnyCharP : Parser<Char>() {
        override fun getName(): String = "anyCharP"
        override fun parsePartial(input: String, pos: Int): ParseResult<Char> =
            extractChar(input, pos).map { x -> PartialParse(x, pos + 1) }
    }

    internal data class SatisfyP(val f: (Char) -> Boolean) : Parser<Char>() {
        override fun getName(): String = "satisfyP"
        override fun parsePartial(input: String, pos: Int): ParseResult<Char> =
            extractChar(input, pos).flatMap { x ->
                if (f(x)) {
                    right(PartialParse(x, pos + 1))
                } else {
                    left(ParserException(input, pos, this, "Character '$x' did not satisfy condition"))
                }
            }
    }

    internal data class OneOfP(val cs: Set<Char>) : Parser<Char>() {
        override fun getName(): String = "oneOfP(${cs.toList().sorted()})"
        override fun parsePartial(input: String, pos: Int): ParseResult<Char> =
            extractChar(input, pos).flatMap { x ->
                if (cs.contains(x)) {
                    right(PartialParse(x, pos + 1))
                } else {
                    val msg = "Non-matching character '$x' not in ${cs.toList().sorted()}"
                    left(ParserException(input, pos, this, msg))
                }
            }
    }

    internal data class NoneOfP(val cs: Set<Char>) : Parser<Char>() {
        override fun getName(): String = "noneOfP(${cs.toList().sorted()})"
        override fun parsePartial(input: String, pos: Int): ParseResult<Char> =
            extractChar(input, pos).flatMap { x ->
                if (cs.contains(x)) {
                    val msg = "Found disallowed character '$x'"
                    left(ParserException(input, pos, this, msg))
                } else {
                    right(PartialParse(input[pos], pos + 1))
                }
            }
    }

    internal data class StringP(val s: String) : Parser<String>() {
        override fun getName(): String = "stringP($s)"
        override fun parsePartial(input: String, pos: Int): ParseResult<String> {
            var currentPos = pos
            return s.toList().traverse { c: Char ->
                extractChar(input, currentPos).flatMap { x: Char ->
                    if (x == c) {
                        currentPos += 1
                        right(Unit)
                    } else {
                        val msg = "Non-matching character '$x' breaks match of string \"$s\""
                        left(ParserException(input, currentPos, this, msg))
                    }
                }
            }.map {
                PartialParse(s, pos + s.length)
            }
        }
    }

    internal object IntP : Parser<Int>() {
        override fun getName(): String = "intP"
        override fun parsePartial(input: String, pos: Int): ParseResult<Int> =
            LongP.parsePartial(input, pos).map { (i, l) ->
                PartialParse(i.toInt(), l)
            }.mapLeft {
                it.copy(parser = this)
            }
    }

    internal object LongP : Parser<Long>() {
        override fun getName(): String = "longP"
        override fun parsePartial(input: String, pos: Int): ParseResult<Long> =
            (charP('-').optional() then manyP(digitP()))
                .parsePartial(input, pos)
                .flatMap { (found, loc) ->
                    val (minusOpt, digits) = found
                    if (digits.isEmpty()) {
                        left(ParserException(input, pos, this, "No digits found"))
                    } else {
                        tryEither {
                            digits.joinToString("").toLong()
                        }.mapLeft {
                            // Should not happen
                            ParserException(input, pos, this, "Unable to parse int from collected digits $digits", it)
                        }.map { int ->
                            val minus = minusOpt.map { -1 }.getOrElse { 1 }
                            PartialParse(int * minus, loc)
                        }
                    }
                }
    }

    internal object RestOfLineP : Parser<String>() {
        override fun getName(): String = "restOfLineP"
        override fun parsePartial(input: String, pos: Int): ParseResult<String> {
            if (pos < 0) {
                return left(ParserException(input, pos, this, "Position less than 0"))
            }
            val s = StringBuilder()
            var i = pos
            do {
                if (i >= input.length) {
                    return right(PartialParse(s.toString(), i))
                }
                val reachedEol = newlineP().parsePartial(input, i)
                    .fold({ false }, { true })
                if (reachedEol) {
                    return right(PartialParse(s.toString(), i))
                } else {
                    s.append(input[i])
                    i += 1
                }
            } while (true)
        }
    }

    internal data class ChoiceP<T>(val ps: List<Parser<T>>) : Parser<T>() {
        override fun getName(): String = "choice(${ps.map { it.getName() }})"
        override fun parsePartial(input: String, pos: Int): ParseResult<T> {
            for (p in ps) {
                val r = p.parsePartial(input, pos)
                if (r.isRight()) {
                    return r
                }
            }
            return left(ParserException(input, pos, this, "None of the choices matched"))
        }
    }

    internal data class ManyP<T>(val p: Parser<T>) : Parser<List<T>>() {
        override fun getName(): String = "manyP(${p.getName()})"
        override fun parsePartial(input: String, pos: Int): ParseResult<List<T>> {
            var currentPos = pos
            var output = mutableListOf<T>()
            while (currentPos < input.length) {
                val result = p.parsePartial(input, currentPos)
                when (result) {
                    is Either.Right<PartialParse<T>> -> {
                        output.add(result.v.v)
                        currentPos = result.v.loc
                    }
                    is Either.Left<ParserException> ->
                        // Discard this result - this means that we're done
                        break
                }
            }
            return right(PartialParse(output.toList(), currentPos))
        }
    }

    internal data class ManyTillP<T>(val p: Parser<T>, val end: Parser<*>) : Parser<List<T>>() {
        override fun getName(): String = "(${p.getName()} manyTill ${end.getName()})"
        override fun parsePartial(input: String, pos: Int): ParseResult<List<T>> {
            var currentPos = pos
            var output = mutableListOf<T>()
            while (currentPos < input.length) {
                val endResult = end.parsePartial(input, currentPos)
                when (endResult) {
                    is Either.Right<PartialParse<*>> ->
                        return right(PartialParse(output.toList(), currentPos))
                    is Either.Left<ParserException> -> {
                        val result = p.parsePartial(input, currentPos)
                        when (result) {
                            is Either.Right<PartialParse<T>> -> {
                                output.add(result.v.v)
                                currentPos = result.v.loc
                            }
                            is Either.Left<ParserException> ->
                                return left(
                                    ParserException(
                                        input,
                                        currentPos,
                                        this,
                                        "Unable to parse, but end not yet reached"
                                    )
                                )
                        }
                    }
                }
            }
            return left(ParserException(input, currentPos, this, "Did not find end before reaching end of input"))
        }
    }

    internal data class RepeatedP<T>(val p: Parser<T>, val n: Int) : Parser<List<T>>() {
        override fun getName(): String = "(${p.getName()} repeated $n)"
        override fun parsePartial(input: String, pos: Int): ParseResult<List<T>> {
            var currentPos = pos
            var output = mutableListOf<T>()
            var error: ParserException? = null
            while (output.size < n && error == null) {
                val result = p.parsePartial(input, currentPos)
                when (result) {
                    is Either.Right<PartialParse<T>> -> {
                        output.add(result.v.v)
                        currentPos = result.v.loc
                    }
                    is Either.Left<ParserException> -> {
                        error = result.v
                        break
                    }
                }
            }
            if (error != null) {
                return error.asLeft()
            } else {
                return PartialParse(output.toList(), currentPos).asRight()
            }
        }
    }

    internal data class Many1P<T>(val p: Parser<T>) : Parser<List<T>>() {
        override fun getName(): String = "many1P(${p.getName()})"
        override fun parsePartial(input: String, pos: Int): ParseResult<List<T>> =
            manyP(p).parsePartial(input, pos).flatMap { (ts, loc) ->
                if (ts.size == 0) {
                    left(ParserException(input, pos, this, "Expected to match at least one"))
                } else {
                    right(PartialParse(ts, loc))
                }
            }.mapLeft { it.copy(parser = this) }
    }

    internal data class SepByP<T, S>(
        val p: Parser<T>,
        val sep: Parser<S>,
        val allowTrailing: Boolean = true
    ) : Parser<List<T>>() {
        override fun getName(): String = "(${p.getName()} sepBy ${sep.getName()})"
        override fun parsePartial(input: String, pos: Int): ParseResult<List<T>> =
            p.map { listOf(it) }.parsePartial(input, pos).fold(
                { _ ->
                    // Special case, empty list
                    right(PartialParse(emptyList(), pos))
                },
                { firstPartial ->
                    val many = if (allowTrailing) {
                        manyP(sep ignoreThen p) thenIgnore (sep.optional())
                    } else {
                        manyP(sep ignoreThen p)
                    }
                    many.parsePartial(input, firstPartial.loc).fold(
                        { _ -> right(firstPartial) },
                        { (vs, loc) ->
                            right(PartialParse(firstPartial.v + vs, loc))
                        }
                    )
                }
            )
    }

    internal data class SepBy1P<T, S>(
        val p: Parser<T>,
        val sep: Parser<S>,
        val allowTrailing: Boolean = true
    ) : Parser<List<T>>() {
        override fun getName(): String = "(${p.getName()} sepBy1 ${sep.getName()})"
        override fun parsePartial(input: String, pos: Int): ParseResult<List<T>> =
            sepByP(p, sep, allowTrailing).parsePartial(input, pos).flatMap { (ts, loc) ->
                if (ts.size == 0) {
                    left(ParserException(input, pos, this, "Expected to match at least one"))
                } else {
                    right(PartialParse(ts, loc))
                }
            }.mapLeft { it.copy(parser = this) }
    }

    internal data class PlusP<T, S>(val p1: Parser<T>, val p2: Parser<S>) : Parser<Pair<T, S>>() {
        override fun getName(): String = "(${p1.getName()} + ${p2.getName()})"
        override fun parsePartial(input: String, pos: Int): ParseResult<Pair<T, S>> =
            p1.parsePartial(input, pos).flatMap { r1 ->
                p2.parsePartial(input, r1.loc).map { r2 ->
                    PartialParse(Pair(r1.v, r2.v), r2.loc)
                }
            }
    }

    internal data class OrP<T, S>(val p1: Parser<T>, val p2: Parser<S>) : Parser<Either<T, S>>() {
        override fun getName(): String = "(${p1.getName()} or ${p2.getName()})"
        override fun parsePartial(input: String, pos: Int): ParseResult<Either<T, S>> =
            p1.optional().flatMap { tOpt: Option<T> ->
                tOpt.fold(
                    { t: T -> lift(t).map { left(it) } },
                    { p2.map { right(it) } }
                )
            }.parsePartial(input, pos).mapLeft { _ ->
                ParserException(input, pos, this, "Neither ${p1.getName()} nor ${p2.getName()} matched")
            }
    }
    internal data class OptionalP<T>(val p: Parser<T>) : Parser<Option<T>>() {
        override fun getName(): String = "optionalP(${p.getName()})"
        override fun parsePartial(input: String, pos: Int): ParseResult<Option<T>> =
            p.parsePartial(input, pos).fold(
                { _ -> right(PartialParse(none<T>(), pos)) },
                { partial -> right(PartialParse(some(partial.v), partial.loc)) }
            )
    }

    internal data class MapP<T, S>(val p: Parser<T>, val f: (T) -> S) : Parser<S>() {
        override fun getName(): String = "mapP(${p.getName()}, f)"
        override fun parsePartial(input: String, pos: Int): ParseResult<S> =
            p.parsePartial(input, pos).map { PartialParse(f(it.v), it.loc) }
    }

    internal data class FlatMapP<T, S>(val p: Parser<T>, val f: (T) -> Parser<S>) : Parser<S>() {
        override fun getName(): String = "flatMapP(${p.getName()}, f)"
        override fun parsePartial(input: String, pos: Int): ParseResult<S> =
            p.parsePartial(input, pos).flatMap { (v, loc) ->
                f(v).parsePartial(input, loc)
            }
    }

    internal data class LiftP<T>(val v: T) : Parser<T>() {
        override fun getName(): String = "liftP($v)"
        override fun parsePartial(input: String, pos: Int): ParseResult<T> =
            right(PartialParse(v, pos))
    }

    internal data class Failure<T>(val msg: String) : Parser<T>() {
        override fun getName(): String = "failureP"
        override fun parsePartial(input: String, pos: Int): ParseResult<T> =
            left(ParserException(input, pos, this, msg))
    }

    internal data class NamedP<T>(val p: Parser<T>, val name: String) : Parser<T>() {
        override fun getName(): String = "$name"
        override fun parsePartial(input: String, pos: Int): ParseResult<T> =
            p.parsePartial(input, pos).mapLeft { err ->
                err.copy(parser = this)
            }
    }
}
