import java.io.File

fun main() {
    println(Day13.create(File("src/main/resources/day13.txt").readText().split("\n\n")).solve1())
    println(Day13.create(File("src/main/resources/day13.txt").readText().split("\n\n")).solve2())
}

class Day13(private val patterns: List<List<String>>) {
    companion object Factory {
        fun create(input: List<String>): Day13 {
            return Day13(input.map { list -> list.split("\n").filter { it.isNotEmpty() } })
        }
    }

    fun solve1(): Int {
        return solve(false)
    }

    fun solve2() : Int {
        return solve(true)
    }

    private fun solve(hasSmudge: Boolean): Int {
        return patterns.map { calculateRowsAboveMirror(it, hasSmudge) * 100 + calculateRowsAboveMirror(transpose(it), hasSmudge) }
            .sumOf { it }
    }

    private fun calculateRowsAboveMirror(pattern: List<String>, hasSmudge: Boolean): Int {
        for (index in pattern.indices) {
            var above = pattern.take(index + 1)
            var below = pattern.drop(index + 1).take(index + 1)
            if (below.size > above.size) {
                below = below.take(above.size)
            }
            if (above.size > below.size) {
                above = above.drop(above.size - below.size)
            }
            if (above.isNotEmpty() && isReflection(above, below, hasSmudge)) {
                return index + 1
            }
        }
        return 0
    }

    private fun isReflection(above: List<String>, below: List<String>, hasSmudge: Boolean): Boolean = above.indices.map { above.reversed()[it].charDiffs(below[it]) }.sumOf { it } == (if(hasSmudge) 1 else 0)

    private fun transpose(pattern: List<String>): List<String> {
        val twoDimensionalCharArray = pattern.map { it.toCharArray() }.toTypedArray()

        var transposed = Array(twoDimensionalCharArray.first().size) { Array(twoDimensionalCharArray.size) { 'x' } }
        for (y in twoDimensionalCharArray.indices) {
            for (x in 0..<twoDimensionalCharArray[y].size) {
                transposed[x][y] = twoDimensionalCharArray[y][x]
            }
        }

        return transposed.map { String(it.toCharArray()) }.toList()
    }
}

private fun String.charDiffs(other: String) : Int {
    var differences = 0;
    for (i in this.indices) {
        if (this[i] != other[i]) {
            differences++
        }
    }
    return differences
}


