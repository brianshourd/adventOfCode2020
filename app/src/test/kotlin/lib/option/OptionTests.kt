package lib.option

import kotlin.test.Test
import kotlin.test.assertEquals

class OptionTests {
    @Test
    fun testOptionMapSome() {
        assertEquals(Option.Some(2), some(1).map { it + 1 })
    }

    @Test
    fun testOptionMapNone() {
        assertEquals(Option.None, none<Int>().map { it + 1 })
    }

    @Test
    fun testOptionFlatMapSomeSome() {
        assertEquals(Option.Some("1"), some(1).flatMap { some(it.toString()) })
    }

    @Test
    fun testOptionFlatMapSomeNone() {
        assertEquals(Option.None, some(1).flatMap { none<String>() })
    }

    @Test
    fun testOptionFlatMapNone() {
        assertEquals(Option.None, none<Int>().flatMap { some(it.toString()) })
    }

    @Test
    fun testOptionFlattenSomeSome() {
        assertEquals(Option.Some(1), some(some(1)).flatten())
    }

    @Test
    fun testOptionFlattenSomeNone() {
        assertEquals(Option.None, some(none<Int>()).flatten())
    }

    @Test
    fun testOptionFlattenNone() {
        assertEquals(Option.None, none<Option<Int>>().flatten())
    }
}
