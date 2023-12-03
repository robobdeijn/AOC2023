import java.io.File
import kotlin.math.abs
import kotlin.math.log10

fun main() {
    val partNumberSum = Schematic.create(File("src/main/resources/day3.txt").readLines()).partNumberSum()
    println("Sum: $partNumberSum")

    val gearRatio = Schematic.create(File("src/main/resources/day3.txt").readLines()).gearRatio()
    println("Gear Ratio: $gearRatio")
}

class Schematic(private val parts: List<PartNumber>, private val symbols: List<Symbol>) {
    companion object Factory {
        fun create(schematicString: List<String>) : Schematic = Schematic(schematicString.flatMapIndexed { y, s -> extractPartNumbers(y, s) }, schematicString.flatMapIndexed { y, s -> extractSymbols(y, s) });

        private fun extractSymbols(y: Int, line: String) : List<Symbol> = line.mapIndexedNotNull { x, c -> if (isSymbol(c)) Symbol(c, Coordinate(x, y)) else null }
        private fun isSymbol(c: Char) = !(c.isDigit() || c == '.')
        private fun extractPartNumbers(y: Int, line: String) : List<PartNumber> = Regex("\\d+").findAll(line).map { PartNumber(it.value.toInt(), Coordinate(it.range.first, y)) }.toList()
    }

    fun gearRatio() : Int = symbols.filter { it.value == '*' }
            .map { s -> parts.filter { it.isAdjacent(s.coordinate) } }
            .filter { it.count() == 2 }
            .sumOf { it[0].number * it[1].number }

    fun partNumberSum() : Int = parts.filter { p -> symbols.any { p.isAdjacent(it.coordinate) } }.sumOf { it.number }
}

class PartNumber(val number: Int, private val start: Coordinate) {
    fun isAdjacent(coordinate: Coordinate) : Boolean = coordinate.x >= start.x -1 && coordinate.x <= (start.x + log10(abs(number.toDouble())).toInt() +1)
                && coordinate.y >= start.y -1 && coordinate.y <= start.y + 1
}

data class Symbol(val value: Char, val coordinate: Coordinate)
data class Coordinate (val x: Int, val y: Int)
