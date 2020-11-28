package adco

import helpers.assertRight
import kotlin.test.Test

class Day1Tests {
    val day1 = Day1()

    @Test
    fun testDay1() {
        assertRight(day1.partA("foo"))
    }
}
