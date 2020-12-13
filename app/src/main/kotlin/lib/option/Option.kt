package lib.option

sealed class Option<out T>(private val vNullable: T?) {
    fun <S> fold(f: (T) -> S, g: () -> S): S = when (this) {
        is Some<T> -> f(this.v)
        is None -> g()
    }

    fun <S> map(f: (T) -> S): Option<S> = flatMap { v -> Some(f(v)) }

    fun <S> flatMap(f: (T) -> Option<S>): Option<S> = fold(f, { None })

    fun orNull(): T? = vNullable

    fun isEmpty(): Boolean = vNullable == null

    data class Some<out T>(val v: T) : Option<T>(v)
    object None : Option<Nothing>(null)
}

fun <T> none(): Option<T> = Option.None
fun <T> some(v: T): Option<T> = Option.Some(v)

fun <T> T?.toOption(): Option<T> = if (this != null) some(this) else none()
fun <T> Iterable<Option<T>>.somes(): List<T> = this.mapNotNull { it.orNull() }

fun <T, S : Option<T>> Option<S>.flatten(): Option<T> = when (this) {
    is Option.Some<S> -> this.v
    is Option.None -> Option.None
}
fun <T> Option<T>.getOrElse(f: () -> T): T = this.fold({ it }, f)
