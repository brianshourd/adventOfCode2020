package lib.memoize

class Memoize<TIn, TOut>(private val f: (TIn) -> TOut) : (TIn) -> TOut {
    private val memo: MutableMap<TIn, TOut> = mutableMapOf()

    override operator fun invoke(x: TIn): TOut =
        if (memo.contains(x)) {
            memo[x]!!
        } else {
            val r = this.f(x)
            memo[x] = r
            r
        }
}
