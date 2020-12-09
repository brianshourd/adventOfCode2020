package adco

import lib.either.Either
import lib.parsers.Parser

open class AdcoError(
    val msg: String,
    val underlying: Exception? = null
) : Exception(msg) {
    override fun equals(other: Any?): Boolean =
        (other is AdcoError) &&
            other.msg == this.msg &&
            other.underlying == this.underlying

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
