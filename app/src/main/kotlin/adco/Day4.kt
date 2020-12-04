package adco

import lib.either.Either
import lib.either.asLeft
import lib.either.asRight
import lib.either.flatMap
import lib.either.toEither
import lib.either.traverse
import lib.parsers.Parser
import lib.parsers.charP
import lib.parsers.choiceP
import lib.parsers.digitP
import lib.parsers.ignoreThen
import lib.parsers.intP
import lib.parsers.many1P
import lib.parsers.map
import lib.parsers.newlineP
import lib.parsers.noneOfP
import lib.parsers.oneOfP
import lib.parsers.or
import lib.parsers.sepBy
import lib.parsers.spaceP
import lib.parsers.stringP
import lib.parsers.then
import lib.parsers.thenIgnore

class Day4() : IAdcoProblem<List<Day4.Passport>, Int> {
    override val title = "Day 4: Passport Processing"

    data class Passport(val fields: Map<String, String>) {
        companion object {
            data class ValidationError(
                val fieldName: String,
                val msg: String
            )
            val requiredFields = setOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")
        }

        fun containsRequiredFields() =
            fields.keys.containsAll(requiredFields)

        private fun fieldIsRequired(fieldName: String): Either<ValidationError, String> =
            fields[fieldName].toEither { ValidationError(fieldName, "required field") }

        private fun fieldIsNumber(fieldName: String, range: IntRange): Either<ValidationError, Int> =
            fieldIsRequired(fieldName).flatMap { field ->
                intP().parse(field).mapLeft { ValidationError(fieldName, "not an integer") }
            }.flatMap {
                if (range.contains(it)) {
                    it.asRight()
                } else {
                    ValidationError(fieldName, "not in range").asLeft()
                }
            }

        private fun fieldIsHeight(fieldName: String): Either<ValidationError, String> =
            fieldIsRequired(fieldName).flatMap { field ->
                (intP() then (choiceP(stringP("cm"), stringP("in")))).parse(field).mapLeft {
                    ValidationError(fieldName, "not a valid height")
                }.flatMap { (x, unit) ->
                    val range = if (unit == "cm") 150..193 else 59..76
                    if (range.contains(x)) {
                        field.asRight()
                    } else {
                        ValidationError(fieldName, "not in range").asLeft()
                    }
                }
            }

        private fun fieldIsHairColor(fieldName: String): Either<ValidationError, String> =
            fieldIsRequired(fieldName).flatMap { field ->
                (charP('#') ignoreThen many1P(oneOfP("0123456789abcdef"))).parse(field).mapLeft {
                    ValidationError(fieldName, "not a valid hair color")
                }.flatMap { xs ->
                    if (xs.size != 6) {
                        ValidationError(fieldName, "not a valid hair color").asLeft()
                    } else {
                        field.asRight()
                    }
                }
            }

        private fun fieldIsEyeColor(fieldName: String): Either<ValidationError, String> =
            fieldIsRequired(fieldName).flatMap { field ->
                choiceP(listOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth").map(::stringP))
                    .parse(field).mapLeft {
                        ValidationError(fieldName, "not a valid eye color")
                    }
            }

        private fun fieldIsPassportId(fieldName: String): Either<ValidationError, String> =
            fieldIsRequired(fieldName).flatMap { field ->
                many1P(digitP()).parse(field).mapLeft {
                    ValidationError(fieldName, "not a valid passport id")
                }.flatMap { xs ->
                    if (xs.size != 9) {
                        ValidationError(fieldName, "not a valid passport id").asLeft()
                    } else {
                        field.asRight()
                    }
                }
            }

        fun validate(): Either<ValidationError, Unit> =
            listOf(
                { fieldIsNumber("byr", 1920..2002) },
                { fieldIsNumber("iyr", 2010..2020) },
                { fieldIsNumber("eyr", 2020..2030) },
                { fieldIsHeight("hgt") },
                { fieldIsHairColor("hcl") },
                { fieldIsEyeColor("ecl") },
                { fieldIsPassportId("pid") },
            ).traverse { it.invoke() }.map { Unit }
    }

    val parseDatum: Parser<String> = many1P(noneOfP(':', ' ', '\n')).map { it.joinToString("") }

    val parseKvp: Parser<Pair<String, String>> = parseDatum thenIgnore charP(':') then parseDatum

    val parsePassport: Parser<Passport> =
        (parseKvp sepBy (spaceP() or newlineP())).map { Passport(it.toMap()) }

    override val parser: Parser<List<Passport>> = parsePassport sepBy newlineP()

    override fun partA(input: List<Passport>): Either<AdcoError, Int> =
        input.count { it.containsRequiredFields() }.asRight()

    override fun partB(input: List<Passport>): Either<AdcoError, Int> =
        input.count { it.validate().isRight() }.asRight()
}
