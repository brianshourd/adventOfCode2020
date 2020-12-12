package lib.memoize

import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoizeTests {
    fun fib(x: Int, recurse: (Int) -> BigInteger): BigInteger =
        if (x <= 1) BigInteger(x.toString()) else recurse(x - 1) + recurse(x - 2)

    @Test
    fun testMemoFib() {
        val memoFib = Memoize(::fib)
        assertEquals(BigInteger("354224848179261915075"), memoFib(100))
    }
}
