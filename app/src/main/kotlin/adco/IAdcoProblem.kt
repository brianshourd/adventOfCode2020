package adco

import lib.either.Either
import lib.parsers.Parser

data class AdcoError(
    val msg: String,
    val underlying: Exception? = null
) : Exception(msg) {
    override fun toString(): String =
        if (underlying != null) {
            msg + "; caused by: $underlying"
        } else {
            msg
        }
}

interface IAdcoProblem<TInput, TOutput> {
    val title: String
    val parser: Parser<TInput>
    fun partA(input: TInput): Either<AdcoError, TOutput>
    fun partB(input: TInput): Either<AdcoError, TOutput>
}
