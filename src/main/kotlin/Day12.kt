import java.io.File
import java.util.concurrent.atomic.AtomicInteger

fun main() {
    println(Records.create(File("src/main/resources/day12.txt").readLines()).solve1())

}

class Records(private val brokenRecords: List<BrokenRecord>) {
    var processed = AtomicInteger(0)
    companion object Factory {
        private fun getBrokenRecord(map: List<String>) : BrokenRecord {
            return BrokenRecord(map[0], map[1].split(",").map { it.toInt() })
        }

        fun create(input: List<String>): Records {
            return Records(input.map { getBrokenRecord(it.trim().split(" "))})
        }
    }

    fun solve1() : Int {
        return brokenRecords.sumOf { countValidPermutations(it) }
    }

    private fun countValidPermutations(brokenRecord: BrokenRecord) : Int {
        processed.getAndIncrement()
        println("processing item $processed")
        return brokenRecord.getPermutations().map { brokenRecord.input.replaceAtPositions(it.toList(), '#') }
            .count { isValidLine(it, brokenRecord.groups) }

    }


    private fun isValidLine(line: String, expectedGroups: List<Int>): Boolean {
        return "#+".toRegex().findAll(line).map { it.range.last - it.range.first + 1}.toList() == expectedGroups
    }
}

private fun String.replaceAtPositions(positions: List<Int>, c: Char) : String {
   var output = this
    positions.forEach { output = output.replaceRange(it, it + 1, c.toString()) }
    return output.replace("?", ".")
}

data class BrokenRecord(val input: String, val groups: List<Int>) {
    private val questionMarkIndices = input.mapIndexedNotNull { index, c -> if (c == '?') index else null }

    fun getPermutations(): Set<Set<Int>> {
        return questionMarkIndices.allPermutationsOfLength(groups.sumOf { it } - input.count { it == '#' })
    }
}

private fun <E> List<E>.allPermutationsOfLength(length: Int): Set<Set<E>> {
    val permutations = mutableSetOf<Set<E>>()
    fun permute(elements: List<E>, permutation: Set<E>) {
        if (permutation.size == length) {
            permutations.add(permutation)
            return
        }
        for (element in elements) {
            val remainingElements = elements - element
            val newPermutation = permutation + element
            permute(remainingElements, newPermutation)
        }
    }
    permute(this, emptySet())
    return permutations
}
