package lib.parsers

import lib.either.Either
import lib.either.flatMap
import lib.either.left
import lib.either.right
import lib.either.traverse
import lib.option.Option
import lib.option.none
import lib.option.some

// Individual character parsers
fun oneOfP(cs: Set<Char>): Parser<Char> = Parser.OneOfP(cs)
fun oneOfP(vararg cs: Char): Parser<Char> = oneOfP(cs.toSet())
fun oneOfP(cs: String): Parser<Char> = oneOfP(cs.toSet())
fun noneOfP(cs: Set<Char>): Parser<Char> = Parser.NoneOfP(cs)
fun noneOfP(vararg cs: Char): Parser<Char> = noneOfP(cs.toSet())
fun noneOfP(cs: String): Parser<Char> = noneOfP(cs.toSet())
fun spacesP(): Parser<Char> = manyP(satisfyP(Char::isWhitespace)).map { ' ' }.named("spacesP")
fun spaceP(): Parser<Char> = satisfyP(Char::isWhitespace).named("spaceP")
fun newlineP(): Parser<Char> = (charP('\n') or stringP("\r\n"))
    .map { '\n' }.named("newlineP")
fun upperP(): Parser<Char> = satisfyP(Char::isUpperCase).named("upperP")
fun lowerP(): Parser<Char> = satisfyP(Char::isLowerCase).named("lowerP")
fun digitP(): Parser<Char> = satisfyP(Char::isDigit).named("digitP")
fun charP(c: Char): Parser<Char> = Parser.CharP(c)
fun anyCharP(): Parser<Char> = Parser.AnyCharP
fun satisfyP(f: (Char) -> Boolean): Parser<Char> = Parser.SatisfyP(f)

// Multi-character parsers
fun stringP(s: String): Parser<String> = Parser.StringP(s)
fun intP(): Parser<Int> = Parser.IntP

// Combinators
fun <T> choiceP(ps: List<Parser<T>>): Parser<T> = Parser.ChoiceP(ps)
fun <T> choiceP(vararg ps: Parser<T>): Parser<T> = choiceP(ps.toList())
fun <T> manyP(p: Parser<T>): Parser<List<T>> = Parser.ManyP(p)
fun <T> many1P(p: Parser<T>): Parser<List<T>> = Parser.Many1P(p)
fun <T> between(openP: Parser<*>, closeP: Parser<*>, p: Parser<T>): Parser<T> =
    (openP ignoreThen p thenIgnore closeP).named("betweenP")
fun <T> Parser<T>.optional(): Parser<Option<T>> = Parser.OptionalP(this)
infix fun <T> Parser<T>.manyTillP(end: Parser<*>): Parser<List<T>> = Parser.ManyTillP(this, end)

fun <T, S> sepByP(p: Parser<T>, sep: Parser<S>, allowTrailing: Boolean = true): Parser<List<T>> =
    Parser.SepByP(p, sep, allowTrailing)
infix fun <T, S> Parser<T>.sepBy(s: Parser<S>): Parser<List<T>> = Parser.SepByP(this, s)
operator fun <T, S> Parser<T>.plus(s: Parser<S>): Parser<Pair<T, S>> = Parser.PlusP(this, s)
infix fun <T, S> Parser<T>.ignoreThen(s: Parser<S>): Parser<S> = Parser.PlusP(this, s).map { it.second }
infix fun <T, S> Parser<T>.thenIgnore(s: Parser<S>): Parser<T> = Parser.PlusP(this, s).map { it.first }
infix fun <T, S> Parser<T>.or(s: Parser<S>): Parser<Either<T, S>> = Parser.OrP(this, s)
fun <T> Parser<T>.named(name: String): Parser<T> = Parser.NamedP(this, name)

// Functor/Monad functionality for combinations
fun <T, S> Parser<T>.map(f: (T) -> S): Parser<S> = Parser.MapP(this, f)
fun <T, S> Parser<T>.flatMap(f: (T) -> Parser<S>): Parser<S> = Parser.FlatMapP(this, f)
fun <T> lift(v: T): Parser<T> = Parser.LiftP(v)
fun failureP(msg: String): Parser<Nothing> = Parser.Failure(msg)

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
            manyP(digitP()).parsePartial(input, pos).flatMap { (found, loc) ->
                if (found.isEmpty()) {
                    left(ParserException(input, pos, this, "No digits found"))
                } else {
                    right(PartialParse(found.joinToString("").toInt(), loc))
                }
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
        override fun getName(): String = "manyTillP(${p.getName()}, ${end.getName()})"
        override fun parsePartial(input: String, pos: Int): ParseResult<List<T>> {
            var currentPos = pos
            var output = mutableListOf<T>()
            while (currentPos < input.length) {
                val endResult = end.parsePartial(input, currentPos)
                when (endResult) {
                    is Either.Right<PartialParse<*>> ->
                        return right(PartialParse(output.toList(), endResult.v.loc))
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
