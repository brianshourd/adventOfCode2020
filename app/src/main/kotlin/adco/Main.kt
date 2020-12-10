package adco

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.enum
import lib.either.Either
import lib.either.flatMap

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

class DayCommand<TInput, TOutput>(
    val problem: IAdcoProblem<TInput, TOutput>,
    val name: String,
) : CliktCommand(help = problem.title, name = name) {
    val part: Part by argument().enum()

    override fun run() {
        val inputRaw = readAllInput()
        runCore(inputRaw).fold(
            { err -> println("ERROR: $err") },
            { output -> println(output) }
        )
    }

    fun runCore(inputRaw: String): Either<AdcoError, String> =
        problem.parser.parse(inputRaw).mapLeft {
            AdcoError("Error parsing input", it)
        }.flatMap { input: TInput ->
            when (part) {
                Part.A -> problem.partA(input)
                Part.B -> problem.partB(input)
            }.map { it.toString() }
        }
}

fun main(args: Array<String>) = Main().subcommands(
    DayCommand(Day1(), "day1"),
    DayCommand(Day2(), "day2"),
    DayCommand(Day3(), "day3"),
    DayCommand(Day4(), "day4"),
    DayCommand(Day5(), "day5"),
    DayCommand(Day6(), "day6"),
    DayCommand(Day7(), "day7"),
    DayCommand(Day8(), "day8"),
    DayCommand(Day9(), "day9"),
).main(args)
