package adco

import lib.either.Either

data class AdcoError(
    val msg: String,
    val underlying: Exception? = null
) : Exception(msg) {
    override fun toString(): String {
        var str = "AdcoError: $msg"
        if (underlying != null) {
            str += "; caused by $underlying"
        }
        return str
    }
}

interface ITwoPartProblem {
    val title: String
    val partAProblemText: String
    val partBProblemText: String
    fun partA(input: String): Either<AdcoError, String>
    fun partB(input: String): Either<AdcoError, String>
}
