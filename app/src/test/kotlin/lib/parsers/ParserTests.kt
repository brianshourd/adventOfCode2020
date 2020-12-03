package lib.parsers

import helpers.assertLeft
import helpers.assertRight
import lib.either.left
import lib.either.right
import lib.option.none
import lib.option.some
import kotlin.test.Test
import kotlin.test.assertEquals

class ParserTests {
    fun <T> assertParsingSucceeds(
        parser: Parser<T>,
        input: String,
        expected: T,
        identifier: String = "parser" // Add a name for better output
    ) {
        val s = assertRight(parser.parse(input), "Expected parsing to work on $input for $identifier")
        assertEquals(expected, s, "Expected to parse $s from $input for $identifier")
    }

    fun <T> assertParsingFails(
        parser: Parser<T>,
        input: String,
        expectedFailure: ParserException? = null,
        identifier: String = parser.getName()
    ) {
        val f = assertLeft(parser.parse(input), "Expected parsing to fail on $input for $identifier")
        if (expectedFailure != null) {
            assertEquals(
                expectedFailure,
                f,
                "Expected parsing to fail on $input for $identifier with the given exception"
            )
        }
    }

    fun <T> assertParsingFails(
        parser: Parser<T>,
        input: String,
        expectedLoc: Int? = null,
        expectedMsg: String? = null,
        expectedUnderlying: Exception? = null,
        identifier: String = parser.getName()
    ) {
        val f = assertLeft(parser.parse(input), "Expected parsing to fail on $input for $identifier")
        assertEquals(input, f.input, "Expected parsing failure to forward the input")
        assertEquals(parser, f.parser, "Expected parsing failure to forward the parser")
        if (expectedLoc != null) {
            assertEquals(expectedLoc, f.loc, "Expected parsing failure to have loc=$expectedLoc")
        }
        if (expectedMsg != null) {
            assertEquals(expectedMsg, f.msg, "Expected parsing failure to have msg=$expectedMsg")
        }
        if (expectedUnderlying != null) {
            assertEquals(
                expectedUnderlying,
                f.underlying,
                "Expected parsing failure to have underlying=$expectedUnderlying"
            )
        }
    }

    @Test
    fun testCharP() {
        val parser = charP('a')
        assertParsingSucceeds(parser, "a", 'a')
        assertParsingFails(
            parser,
            "b",
            ParserException("b", 0, parser, "Non-matching character 'b' not 'a'"),
        )
    }

    @Test
    fun testAnyCharP() {
        val parser = anyCharP()
        assertParsingSucceeds(parser, "a", 'a')
        assertParsingSucceeds(parser, "b", 'b')
        assertParsingFails(
            parser,
            "ba",
            ParserException("ba", 1, parser, "Input was remaining after parse")
        )
    }

    @Test
    fun testAnyOfP() {
        val parser = oneOfP(setOf('a', 'b', 'c'))
        assertParsingSucceeds(parser, "a", 'a')
        assertParsingSucceeds(parser, "b", 'b')
        assertParsingSucceeds(parser, "c", 'c')
        assertParsingFails(
            parser,
            "d",
            ParserException("d", 0, parser, "Non-matching character 'd' not in [a, b, c]")
        )
    }

    @Test
    fun testNoneOfP() {
        val parser = noneOfP(setOf('a', 'b', 'c'))
        assertParsingSucceeds(parser, "d", 'd')
        assertParsingSucceeds(parser, "1", '1')
        assertParsingFails(
            parser,
            "a",
            ParserException("a", 0, parser, "Found disallowed character 'a'")
        )
    }

    @Test
    fun testSpacesP() {
        val parser = (anyCharP() thenIgnore spacesP()) + anyCharP()
        assertParsingSucceeds(parser, "d a", Pair('d', 'a'))
        assertParsingSucceeds(parser, "xy", Pair('x', 'y'))
        assertParsingSucceeds(parser, "x        y", Pair('x', 'y'))
        assertParsingFails(
            parser,
            "a",
            ParserException("a", 1, anyCharP(), "End of input reached")
        )
    }

    @Test
    fun testSpaceP() {
        val parser = (anyCharP() thenIgnore spaceP()) + anyCharP()
        assertParsingSucceeds(parser, "d a", Pair('d', 'a'))

        assertParsingFails(
            parser,
            "xy",
            ParserException("xy", 1, spaceP(), "Character 'y' did not satisfy condition")
        )
        assertParsingFails(
            parser,
            "x        y",
            ParserException("x        y", 3, parser, "Input was remaining after parse")
        )
        assertParsingFails(
            parser,
            "a",
            ParserException("a", 1, spaceP(), "End of input reached")
        )
    }

    @Test
    fun testNewlineP() {
        val parser = (anyCharP() thenIgnore newlineP()) + anyCharP()
        assertParsingSucceeds(parser, "d\na", Pair('d', 'a'))
        assertParsingSucceeds(parser, "d\r\na", Pair('d', 'a'))

        assertParsingFails(
            parser,
            "xy",
            ParserException("xy", 1, newlineP(), "Neither charP(\n) nor stringP(\r\n) matched")
        )
    }

    @Test
    fun testUpperP() {
        val parser = anyCharP() + upperP()
        assertParsingSucceeds(parser, "xY", Pair('x', 'Y'))

        assertParsingFails(
            parser,
            "xy",
            ParserException("xy", 1, upperP(), "Character 'y' did not satisfy condition")
        )
    }

    @Test
    fun testLowerP() {
        val parser = anyCharP() + lowerP()
        assertParsingSucceeds(parser, "xy", Pair('x', 'y'))

        assertParsingFails(
            parser,
            "xY",
            ParserException("xY", 1, lowerP(), "Character 'Y' did not satisfy condition")
        )
    }

    @Test
    fun testStringP() {
        val parser = stringP("hello")
        assertParsingSucceeds(parser, "hello", "hello")
        assertParsingFails(
            parser,
            "hela",
            3,
            "Non-matching character 'a' breaks match of string \"hello\""
        )
        assertParsingFails(
            parser,
            "hello world",
            5,
            "Input was remaining after parse"
        )
    }

    @Test
    fun testIntP() {
        val parser = intP() sepBy charP(',')
        assertParsingSucceeds(parser, "1,12,56", listOf(1, 12, 56))
        assertParsingSucceeds(parser, "1,-12,0", listOf(1, -12, 0))
        assertParsingFails(
            parser,
            "12x",
            2,
            "Input was remaining after parse"
        )
        assertParsingFails(
            intP(),
            "",
            0,
            "No digits found"
        )
    }

    @Test
    fun testRestOfLineP() {
        val parser = restOfLineP() thenIgnore manyP(anyCharP())
        assertParsingSucceeds(parser, "abcdef", "abcdef")
        assertParsingSucceeds(parser, "abcdef\n", "abcdef")
        assertParsingSucceeds(parser, "abcdef\r\n", "abcdef")
    }

    @Test
    fun testChoiceP() {
        val parser = manyP(choiceP(charP('a'), charP('b'), charP('c'), spaceP()))
        assertParsingSucceeds(parser, "ab c", listOf('a', 'b', ' ', 'c'))
        assertParsingFails(
            parser,
            "abz",
            2,
            "Input was remaining after parse"
        )
    }

    @Test
    fun testManyP() {
        val parser = manyP(noneOfP(setOf('a', 'b', 'c')))
        assertParsingSucceeds(parser, "defg", listOf('d', 'e', 'f', 'g'))
        assertParsingSucceeds(parser, "12", listOf('1', '2'))
        assertParsingSucceeds(parser, "", emptyList<Char>())
        assertParsingFails(
            parser,
            "xyzad",
            3,
            "Input was remaining after parse"
        )
    }

    @Test
    fun testMany1P() {
        val parser = many1P(noneOfP(setOf('a', 'b', 'c')))
        assertParsingSucceeds(parser, "defg", listOf('d', 'e', 'f', 'g'))
        assertParsingSucceeds(parser, "12", listOf('1', '2'))
        assertParsingFails(
            parser,
            "",
            ParserException("", 0, parser, "Expected to match at least one")
        )
        assertParsingFails(
            parser,
            "xyzad",
            3,
            "Input was remaining after parse"
        )
    }

    @Test
    fun testManyTillP() {
        val takeTillSemi = anyCharP() manyTillP charP(';')
        val parser = takeTillSemi thenIgnore charP(';')
        assertParsingSucceeds(parser, "defg;", listOf('d', 'e', 'f', 'g'))
        assertParsingSucceeds(parser, "12;", listOf('1', '2'))
        assertParsingSucceeds(parser, ";", emptyList<Char>())
        assertParsingFails(
            parser,
            "",
            ParserException("", 0, takeTillSemi, "Did not find end before reaching end of input")
        )
        assertParsingFails(
            parser,
            "ab",
            ParserException("ab", 2, takeTillSemi, "Did not find end before reaching end of input")
        )
        assertParsingFails(
            parser,
            "ab;;",
            ParserException("ab;;", 3, parser, "Input was remaining after parse")
        )
    }

    @Test
    fun testBetweenP() {
        val parser = between(charP('{'), charP('}'), many1P(digitP()))
        assertParsingSucceeds(parser, "{123}", listOf('1', '2', '3'))
        assertParsingFails(
            parser,
            "123}",
            ParserException("123}", 0, parser, "Non-matching character '1' not '{'")
        )
        assertParsingFails(
            parser,
            "{123",
            ParserException("{123", 4, parser, "End of input reached")
        )
        assertParsingFails(
            parser,
            "{12a}",
            ParserException("{12a}", 3, parser, "Non-matching character 'a' not '}'")
        )
    }

    @Test
    fun testSepByP() {
        val parser = noneOfP(setOf(',')).sepBy(charP(','))
        assertParsingSucceeds(parser, "d,e,f,g", listOf('d', 'e', 'f', 'g'))
        assertParsingSucceeds(parser, "1,2", listOf('1', '2'))
        assertParsingSucceeds(parser, "", listOf<Char>())
        assertParsingSucceeds(parser, "1", listOf('1'))
        assertParsingSucceeds(parser, "1,2,3,4,", listOf('1', '2', '3', '4'))
        assertParsingFails(
            parser,
            "x,y,zz",
            5,
            "Input was remaining after parse"
        )
        assertParsingFails(
            parser,
            ",",
            0,
            "Input was remaining after parse"
        )
    }

    @Test
    fun testSepByPNoTrailing() {
        val parser = sepByP(anyCharP(), charP(','), allowTrailing = false)
        assertParsingSucceeds(parser, "d,e,f,g", listOf('d', 'e', 'f', 'g'))
        assertParsingSucceeds(parser, "1,2", listOf('1', '2'))
        assertParsingSucceeds(parser, "", listOf<Char>())
        assertParsingSucceeds(parser, "1", listOf('1'))

        assertParsingFails(
            parser,
            "1,2,3,4,",
            7,
            "Input was remaining after parse"
        )
        assertParsingFails(
            parser,
            "x,y,zz",
            5,
            "Input was remaining after parse"
        )
    }

    @Test
    fun testPlus() {
        val parser = anyCharP() + charP(',')
        assertParsingSucceeds(parser, "d,", Pair('d', ','))

        assertParsingFails(parser, "xy")
    }

    @Test
    fun testIgnoreLeft() {
        val parser = anyCharP() ignoreThen charP(',')
        assertParsingSucceeds(parser, "d,", ',')

        assertParsingFails(
            parser,
            "xy",
            ParserException("xy", 1, charP(','), "Non-matching character 'y' not ','")
        )
    }

    @Test
    fun testIgnoreRight() {
        val parser = anyCharP() thenIgnore charP(',')
        assertParsingSucceeds(parser, "d,", 'd')

        assertParsingFails(
            parser,
            "xy",
            ParserException("xy", 1, charP(','), "Non-matching character 'y' not ','")
        )
    }

    @Test
    fun testOrP() {
        val parser = stringP("hello") or intP()
        assertParsingSucceeds(parser, "hello", left("hello"))
        assertParsingSucceeds(parser, "123", right(123))

        assertParsingFails(
            parser,
            "xy",
            0,
            "Neither stringP(hello) nor intP matched"
        )
    }

    @Test
    fun testOptional() {
        val parser = (manyP(noneOfP(setOf(':'))) + (charP(':'))) ignoreThen (anyCharP().optional())
        assertParsingSucceeds(parser, "hello:1", some('1'))
        assertParsingSucceeds(parser, "hello:", none())

        assertParsingFails(
            parser,
            "yo",
            ParserException("yo", 2, charP(':'), "End of input reached")
        )
    }

    @Test
    fun testMap() {
        val parser = charP('a').map { it.toInt() }
        assertParsingSucceeds(parser, "a", 97)

        assertParsingFails(
            parser,
            "b",
            ParserException("b", 0, charP('a'), "Non-matching character 'b' not 'a'"),
        )
    }

    @Test
    fun testFlatMap() {
        val parser = anyCharP().flatMap { v: Char ->
            if (v == 'a') {
                charP('b')
            } else {
                charP('c')
            }
        }
        assertParsingSucceeds(parser, "ab", 'b')
        assertParsingSucceeds(parser, "cc", 'c')

        assertParsingFails(
            parser,
            "ac",
            ParserException("ac", 1, charP('b'), "Non-matching character 'c' not 'b'"),
        )
    }

    @Test
    fun testLift() {
        val f: (Int) -> Parser<Char> = { x -> charP(x.toString()[0]) }
        val parser1 = lift(1).flatMap(f)
        val parser2 = f(1)

        assertParsingSucceeds(parser1, "1", '1')
        assertParsingSucceeds(parser2, "1", '1')

        val expectedException = ParserException("2", 0, charP('1'), "Non-matching character '2' not '1'")
        assertParsingFails(parser1, "2", expectedException)
        assertParsingFails(parser2, "2", expectedException)
    }

    @Test
    fun testFailure() {
        val parser = charP('1').flatMap { c ->
            if (c == '1') {
                failureP("Hoping not to parse a 1")
            } else {
                lift(5)
            }
        }

        assertParsingFails(parser, "1")
    }

    @Test
    fun testNamedFailure() {
        val parser = charP('1')
        val namedParser = parser.named("parse1")

        val exception = assertLeft(parser.parse("2"))
        assertEquals("Error parsing \"2\" at location 0 using charP(1): Non-matching character '2' not '1'", exception.message)
        val namedException = assertLeft(namedParser.parse("2"))
        assertEquals("Error parsing \"2\" at location 0 using parse1: Non-matching character '2' not '1'", namedException.message)
    }

    @Test
    fun testSeriesP() {
        val parser = seriesP(
            intP(),
            charP(':'),
            intP()
        ).map { (n: Int, _: Char, d: Int) -> IntRange(n, d) }

        assertParsingSucceeds(parser, "15:18", 15..18)
        assertParsingSucceeds(parser, "-5:100", -5..100)
        assertParsingFails(
            parser,
            "55",
            ParserException("55", 2, charP(':'), "End of input reached")
        )
    }
}
