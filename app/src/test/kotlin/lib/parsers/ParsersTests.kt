package lib.parsers

import helpers.assertLeft
import lib.either.Either
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ParsersTests {
    @Test
    fun testParseInt() {
        assertEquals(Either.Right(1), parseInt("1"))
    }

    @Test
    fun testParseIntFails() {
        val parseException = assertLeft(parseInt("one"), "Expected parseInt(\"one\") to fail: ")
        assertEquals("one", parseException.input)
        assertEquals("Int", parseException.outputType)
        val expectedPrefix = "Cannot parse 'one' as Int: "
        assertTrue(
            parseException.message!!.startsWith(expectedPrefix),
            "Expected parseException to start with $expectedPrefix, but was ${parseException.message}"
        )
    }

    @Test
    fun testParseLong() {
        assertEquals(Either.Right(30000000000L), parseLong("30000000000"))
    }

    @Test
    fun testParseLongFails() {
        val parseException = assertLeft(parseLong("one"), "Expected parseLong(\"one\") to fail: ")
        assertEquals("one", parseException.input)
        assertEquals("Long", parseException.outputType)
        val expectedPrefix = "Cannot parse 'one' as Long: "
        assertTrue(
            parseException.message!!.startsWith(expectedPrefix),
            "Expected parseException to start with $expectedPrefix, but was ${parseException.message}"
        )
    }
}
