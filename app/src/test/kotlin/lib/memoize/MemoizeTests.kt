package lib.memoize

import helpers.assertRight
import kotlin.test.Test
import kotlin.test.assertEquals
import java.math.BigInteger

class MemoizeTests {
    fun fib(x: Int, recurse: (Int) -> BigInteger): BigInteger =
        if (x <= 1) BigInteger(x.toString()) else recurse(x - 1) + recurse(x - 2)

    @Test
    fun testMemoFib() {
        val memoFib = Memoize(::fib)
        assertEquals(BigInteger("354224848179261915075"), memoFib(100))
    }
}
