package adco

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice

fun readAllInput(): String {
    val lines = mutableListOf<String>()
    var line = readLine()
    while (line != null) {
        lines.add(line)
        line = readLine()
    }
    return lines.joinToString("\n")
}

enum class Part { A, B }
data class Config(val showProblem: Boolean, val part: Part)

class Main : CliktCommand(
    help =
        """
        Command line tool to run the programs from Advent Of Code 2020.
        All input is piped in via stdin, and output is via stdout
        """.trimMargin()
) {
    override fun run() { }
}

abstract class DayCommand(
    val problem: IAdcoProblem
) : CliktCommand(help = problem.title) {
    val showProblem: Boolean by option(
        "--show",
        "-s",
        help = "Just printout the statement of the problem. False by default"
    ).flag("--no-show", default = false)

    val part: String by argument().choice("a", "b")

    override fun run() {
        val partEnum = when (part) {
            "a" -> Part.A
            "b" -> Part.B
            else -> {
                System.err.println("Unknown part: $part")
                System.exit(1)
                Part.A
            }
        }
        val config = Config(showProblem, partEnum)

        if (config.showProblem) {
            when (partEnum) {
                Part.A -> println(problem.partAProblemText)
                Part.B -> println(problem.partBProblemText)
            }
        } else {
            runImpl(config.part)
        }
    }

    // Default implementation, will likely work for most/all of the problems
    fun runImpl(part: Part) {
        val input = readAllInput()
        val result = when (part) {
            Part.A -> problem.partA(input)
            Part.B -> problem.partB(input)
        }
        result.fold(
            { err -> println("ERROR: $err") },
            { output -> println(output) }
        )
    }
}

class Day1Command() : DayCommand(Day1())

fun main(args: Array<String>) = Main().subcommands(
    Day1Command()
).main(args)
