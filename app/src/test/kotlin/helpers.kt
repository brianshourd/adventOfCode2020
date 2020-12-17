package helpers

import lib.either.Either
import lib.option.Option
import kotlin.test.fail

fun <L, R> assertLeft(input: Either<L, R>, msgPrefix: String? = null): L =
    input.fold(
        { it },
        { fail(msgPrefix + "Expected $input to be Left") }
    )

fun <L, R> assertRight(input: Either<L, R>, msgPrefix: String? = null): R =
    input.fold(
        { fail(msgPrefix + "Expected $input to be Right") },
        { it }
    )

fun <T> assertSome(input: Option<T>, msgPrefix: String? = null): T =
    input.fold(
        { it },
        { fail(msgPrefix + "Expected $input to be some") },
    )
