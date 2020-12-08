package adco

import adco.Day7.ContainReq
import helpers.assertRight
import kotlin.test.Test
import kotlin.test.assertEquals

class Day7Tests {
    val day7 = Day7()
    val inputRaw = listOf(
        "light red bags contain 1 bright white bag, 2 muted yellow bags.",
        "dark orange bags contain 3 bright white bags, 4 muted yellow bags.",
        "bright white bags contain 1 shiny gold bag.",
        "muted yellow bags contain 2 shiny gold bags, 9 faded blue bags.",
        "shiny gold bags contain 1 dark olive bag, 2 vibrant plum bags.",
        "dark olive bags contain 3 faded blue bags, 4 dotted black bags.",
        "vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.",
        "faded blue bags contain no other bags.",
        "dotted black bags contain no other bags.",
    ).joinToString("\n")

    @Test
    fun testParser() {
        val output = assertRight(day7.parser(inputRaw))
        val expected = mapOf(
            Pair(
                "light red",
                setOf(
                    ContainReq(1, "bright white"),
                    ContainReq(2, "muted yellow"),
                )
            ),
            Pair(
                "dark orange",
                setOf(
                    ContainReq(3, "bright white"),
                    ContainReq(4, "muted yellow"),
                )
            ),
            Pair(
                "bright white",
                setOf(
                    ContainReq(1, "shiny gold"),
                )
            ),
            Pair(
                "muted yellow",
                setOf(
                    ContainReq(2, "shiny gold"),
                    ContainReq(9, "faded blue"),
                )
            ),
            Pair(
                "shiny gold",
                setOf(
                    ContainReq(1, "dark olive"),
                    ContainReq(2, "vibrant plum"),
                )
            ),
            Pair(
                "dark olive",
                setOf(
                    ContainReq(3, "faded blue"),
                    ContainReq(4, "dotted black"),
                )
            ),
            Pair(
                "vibrant plum",
                setOf(
                    ContainReq(5, "faded blue"),
                    ContainReq(6, "dotted black"),
                )
            ),
            Pair("faded blue", emptySet()),
            Pair("dotted black", emptySet()),
        )
        assertEquals(expected, output)
    }

    @Test
    fun testBuildCanContainMap() {
        val rules = assertRight(day7.parser(inputRaw))
        val output = day7.buildCanContainMap(rules)
        val expected = mapOf(
            "dotted black" to setOf("vibrant plum", "dark olive"),
            "faded blue" to setOf("vibrant plum", "dark olive", "muted yellow"),
            "vibrant plum" to setOf("shiny gold"),
            "dark olive" to setOf("shiny gold"),
            "shiny gold" to setOf("muted yellow", "bright white"),
            "muted yellow" to setOf("dark orange", "light red"),
            "bright white" to setOf("dark orange", "light red"),
        )
        assertEquals(expected, output)
    }

    @Test
    fun testPartA() {
        val input = assertRight(day7.parser(inputRaw))
        val output = assertRight(day7.partA(input))
        val expected = 4
        assertEquals(expected, output)
    }

    @Test
    fun testPartB() {
        val input = assertRight(day7.parser(inputRaw))
        val output = assertRight(day7.partB(input))
        val expected = 32
        assertEquals(expected, output)
    }

    @Test
    fun testPartB2() {
        val inputRaw = listOf(
            "shiny gold bags contain 2 dark red bags.",
            "dark red bags contain 2 dark orange bags.",
            "dark orange bags contain 2 dark yellow bags.",
            "dark yellow bags contain 2 dark green bags.",
            "dark green bags contain 2 dark blue bags.",
            "dark blue bags contain 2 dark violet bags.",
            "dark violet bags contain no other bags.",
        ).joinToString("\n")
        val input = assertRight(day7.parser(inputRaw))
        val output = assertRight(day7.partB(input))
        val expected = 126
        assertEquals(expected, output)
    }
}
