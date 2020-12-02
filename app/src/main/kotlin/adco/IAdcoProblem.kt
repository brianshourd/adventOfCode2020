package adco

import lib.either.Either

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

interface IAdcoProblem {
    val title: String
    val partAProblemText: String
    val partBProblemText: String

    fun partA(input: String): Either<AdcoError, String>
    fun partB(input: String): Either<AdcoError, String>
}
