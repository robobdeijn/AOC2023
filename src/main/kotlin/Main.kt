import java.io.File

val wordToDigit = mapOf("one" to '1', "two" to '2', "three" to '3', "four" to '4', "five" to '5',"six" to '6',"seven" to '7', "eight" to '8', "nine" to '9')

fun main() {
    val sum = File("/Users/robobdeijn/IdeaProjects/Aoc2023Kotlin/src/main/resources/day1.txt").useLines { it -> it.map {convertToDigit(it) }.sum() }
    println("sum: $sum")
}

fun convertToDigit(line: String): Int {
    val digits = line.mapIndexed { index, value -> if (value.isDigit()) value else getWordAt(line, index) }
        .filterNotNull()
    return "${digits.first}${digits.last}".toInt()
}

fun getWordAt(input: String, startPos: Int): Char? {
    return wordToDigit.map { if (it.key == input.drop(startPos).take(it.key.length)) it.value else null }.filterNotNull().firstOrNull()
}
