package lib.parsers

import lib.either.Either
import lib.either.tryEither

data class ParseException(
    val input: String,
    val outputType: String,
    val underlying: Exception
) : Exception("Cannot parse '$input' as $outputType: $underlying")

fun parseInt(input: String): Either<ParseException, Int> =
    tryEither { input.toInt() }.mapLeft { ParseException(input, "Int", it) }

fun parseInt(input: Char): Either<ParseException, Int> =
    tryEither { input.toInt() }.mapLeft { ParseException(input.toString(), "Int", it) }

fun parseLong(input: String): Either<ParseException, Long> =
    tryEither { input.toLong() }.mapLeft { ParseException(input, "Long", it) }
