package lib.either

import lib.option.none
import lib.option.some
import kotlin.test.Test
import kotlin.test.assertEquals

class EitherTests {
    @Test
    fun testEitherMapOnRight() {
        assertEquals(Either.Right(2), right(1).map { it + 1 })
    }

    @Test
    fun testEitherMapOnLeft() {
        val vEither = left("foo") as Either<String, Int>
        assertEquals(Either.Left("foo"), vEither.map { it + 1 })
    }

    @Test
    fun testEitherMapLeftOnRight() {
        val vEither = right(1) as Either<String, Int>
        assertEquals(Either.Right(1), vEither.mapLeft { it + "hello" })
    }

    @Test
    fun testEitherMapLeftOnLeft() {
        assertEquals(Either.Left("foohello"), left("foo").mapLeft { it + "hello" })
    }

    @Test
    fun testEitherFlatMapOnRightToRight() {
        assertEquals(Either.Right(2), right(1).flatMap { right(it + 1) })
    }

    @Test
    fun testEitherFlatMapOnRightToLeft() {
        assertEquals(Either.Left("foo"), right(1).flatMap { left("foo") })
    }

    @Test
    fun testEitherFlatMapOnLeftToRight() {
        val vEither = left("foo") as Either<String, Int>
        assertEquals(Either.Left("foo"), vEither.flatMap { right(it + 1) })
    }

    @Test
    fun testEitherFlatMapOnLeftToLeft() {
        val vEither = left("foo") as Either<String, Int>
        assertEquals(Either.Left("foo"), vEither.flatMap { left(it.toString()) })
    }

    @Test
    fun testSequenceAllRights() {
        val eithers = listOf(
            right(1),
            right(2),
            right(3)
        )
        assertEquals(Either.Right(listOf(1, 2, 3)), eithers.sequence())
    }

    @Test
    fun testSequenceFirstLeft() {
        val eithers = listOf(
            right(1),
            left("foo"),
            right(2),
            left("bar")
        )
        assertEquals(Either.Left("foo"), eithers.sequence())
    }

    fun addOneIfNotTooBig(x: Int): Either<String, Int> =
        if (x < 10) {
            right(x + 1)
        } else {
            left("$x is too big")
        }

    @Test
    fun testTraverseAllRights() {
        val input = listOf(1, 2, 3, 4)
        assertEquals(Either.Right(listOf(2, 3, 4, 5)), input.traverse(::addOneIfNotTooBig))
    }

    @Test
    fun testTraverseFirstLeft() {
        val input = listOf(8, 9, 10, 11)
        assertEquals(Either.Left("10 is too big"), input.traverse(::addOneIfNotTooBig))
    }

    @Test
    fun testTraverseEmpty() {
        val input = emptyList<Int>()
        assertEquals(Either.Right(emptyList<Int>()), input.traverse(::addOneIfNotTooBig))
    }

    @Test
    fun testOptionSomeToEither() {
        assertEquals(Either.Right(1), some(1).toEither { "foo" })
    }

    @Test
    fun testOptionNoneToEither() {
        assertEquals(Either.Left("foo"), none<Int>().toEither { "foo" })
    }
}
