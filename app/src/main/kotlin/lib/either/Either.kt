package lib.either

import lib.option.Option
import lib.option.getOrElse
import lib.option.none
import lib.option.some

sealed class Either<out L, out R>(private val lNullable: L?, private val rNullable: R?) {
    fun <T> fold(f: (L) -> T, g: (R) -> T): T = when (this) {
        is Left<L> -> f(this.v)
        is Right<R> -> g(this.v)
    }

    fun <T> map(f: (R) -> T): Either<L, T> = when (this) {
        is Left<L> -> this
        is Right<R> -> Right(f(this.v))
    }

    fun <T> mapLeft(f: (L) -> T): Either<T, R> = when (this) {
        is Left<L> -> Left(f(this.v))
        is Right<R> -> this
    }

    fun isLeft(): Boolean = when (this) {
        is Left<L> -> true
        is Right<R> -> false
    }

    fun isRight(): Boolean = !this.isLeft()

    data class Left<out L>(val v: L) : Either<L, Nothing>(v, null)
    data class Right<out R>(val v: R) : Either<Nothing, R>(null, v)
}

fun <L> left(v: L): Either<L, Nothing> = Either.Left(v)
fun <R> right(v: R): Either<Nothing, R> = Either.Right(v)

fun <L, R, T> Either<L, R>.flatMap(f: (R) -> Either<L, T>): Either<L, T> = when (this) {
    is Either.Left<L> -> this
    is Either.Right<R> -> f(this.v)
}

fun <L, R> Either<L, R>.getOrElse(f: () -> R): R = this.fold({ f() }, { it })

fun <L, R> Iterable<Either<L, R>>.sequence(): Either<L, List<R>> {
    var leftOpt = none<L>()
    var rights = mutableListOf<R>()
    for (item in this) {
        item.fold(
            { leftOpt = some(it) },
            { rights.add(it) }
        )
        if (leftOpt.isEmpty()) {
            break
        }
    }
    return leftOpt.map { left(it) }.getOrElse { right(rights.toList()) }
}

fun <L, R, T> Iterable<T>.traverse(f: (T) -> Either<L, R>): Either<L, List<R>> {
    var leftOpt = none<L>()
    var rights = mutableListOf<R>()
    for (item in this) {
        when (val result = f(item)) {
            is Either.Right<R> -> rights.add(result.v)
            is Either.Left<L> -> {
                leftOpt = some(result.v)
                break
            }
        }
    }
    return leftOpt.map { left(it) }.getOrElse { right(rights.toList()) }
}

fun <L, R> Option<R>.toEither(ifLeft: () -> L): Either<L, R> = this.fold({ right(it) }, { left(ifLeft()) })

fun <R> tryEither(action: () -> R): Either<Exception, R> =
    try {
        right(action())
    } catch (e: Exception) {
        left(e)
    }
