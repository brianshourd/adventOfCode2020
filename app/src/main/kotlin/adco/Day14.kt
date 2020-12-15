package adco

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.immutableSetOf
import kotlinx.collections.immutable.minus
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import lib.either.Either
import lib.either.asRight
import lib.parsers.Parser
import lib.parsers.choiceP
import lib.parsers.failureP
import lib.parsers.flatMap
import lib.parsers.ignoreThen
import lib.parsers.lift
import lib.parsers.longP
import lib.parsers.many1P
import lib.parsers.map
import lib.parsers.newlineP
import lib.parsers.oneOfP
import lib.parsers.plus
import lib.parsers.sepBy
import lib.parsers.seriesP
import lib.parsers.stringP

class Day14() : IAdcoProblem<ImmutableList<Day14.Instruction>, Long> {
    override val title = "Day 14: Docking Data"

    sealed class Instruction() {
        // ones/zeros is all 0s, with a 1 anywhere the mask includes the requisite char
        data class SetMask(val ones: Long, val zeros: Long, val wilds: ImmutableSet<Long>) : Instruction()
        data class SetMemory(val loc: Long, val value: Long) : Instruction()
    }

    val parseSetMask: Parser<Instruction.SetMask> =
        stringP("mask = ") ignoreThen many1P(oneOfP("X01")).flatMap { cs ->
            if (cs.size != 36) {
                failureP("Map of invalid size ${cs.size}: expected 36")
            } else {
                var m = 1L
                var ones = 0L
                var zeros = 0L
                val wilds = mutableSetOf<Long>()
                for (i in (cs.size - 1) downTo 0) {
                    if (cs[i] == '1') { ones = ones or m }
                    if (cs[i] == '0') { zeros = zeros or m }
                    if (cs[i] == 'X') { wilds += m }
                    m = m shl 1
                }
                lift(Instruction.SetMask(ones, zeros, wilds.toImmutableSet()))
            }
        }

    val parseSetMemory: Parser<Instruction.SetMemory> = seriesP(
        stringP("mem["),
        longP(),
        stringP("] = "),
        longP(),
    ).map { (_, k, _, v) -> Instruction.SetMemory(k, v) }

    override val parser: Parser<ImmutableList<Instruction>> =
        (choiceP(parseSetMask, parseSetMemory) sepBy newlineP()).map { it.toImmutableList() }

    private data class ProgramState(
        val mask: Instruction.SetMask,
        val memory: PersistentMap<Long, Long>
    ) {
        companion object {
            fun empty(): ProgramState = ProgramState(
                Instruction.SetMask(0L, 0L, immutableSetOf()),
                persistentMapOf<Long, Long>()
            )
        }
    }

    fun runProgramA(instructions: ImmutableList<Instruction>): ImmutableMap<Long, Long> =
        instructions.fold(ProgramState.empty()) { state, instruction ->
            when (instruction) {
                is Instruction.SetMemory -> {
                    val v = (instruction.value or state.mask.ones) and state.mask.zeros.inv()
                    state.copy(memory = state.memory.put(instruction.loc, v))
                }
                is Instruction.SetMask -> state.copy(mask = instruction)
            }
        }.memory

    fun runProgramB(instructions: ImmutableList<Instruction>): ImmutableMap<Long, Long> =
        instructions.fold(ProgramState.empty()) { state, instruction ->
            when (instruction) {
                is Instruction.SetMemory -> {
                    val locs = state.mask.wilds.fold(
                        listOf(instruction.loc or state.mask.ones)
                    ) { locs, w ->
                        locs.flatMap { listOf(it or w, it and w.inv()) }
                    }
                    state.copy(
                        memory = state.memory.putAll(
                            locs.associate { it to instruction.value }
                        )
                    )
                }
                is Instruction.SetMask -> state.copy(mask = instruction)
            }
        }.memory

    override fun partA(input: ImmutableList<Instruction>): Either<AdcoError, Long> =
        runProgramA(input).values.sum().asRight()

    override fun partB(input: ImmutableList<Instruction>): Either<AdcoError, Long> =
        runProgramB(input).values.sum().asRight()
}
