import java.io.File
import kotlin.time.measureTime

fun main() {
    println(measureTime {
        println(Report.create(File("src/main/resources/day9.txt").readLines()).solve1())
    })

    println(measureTime {
        println(Report.create(File("src/main/resources/day9.txt").readLines()).solve2())
    })
}

class Report(private val input: List<List<Long>>) {
    companion object Factory {
        fun create(input: List<String>) : Report {
            val longInputs = input.map { it.trim().split("\\s+".toRegex()).map { item -> item.toLong() } }
            return Report(longInputs)
        }
    }

    fun solve1(): Long {
        return input.map { getNext(it) }.sumOf { it }
    }

    fun solve2() : Long {
        return input.map { getNext(it.reversed()) }.sumOf { it }
    }

    private fun getNext(it: List<Long>) : Long = getAllDerivatives(it).sumOf { it.last() }

    private fun getAllDerivatives(it: List<Long>): MutableList<List<Long>> {
        val all = mutableListOf<List<Long>>()
        all.add(it)
        var next = getNextDerivatives(it)
        while (!next.all { it == 0L }) {
            all.add(next)
            next = getNextDerivatives(next)
        }
        all.add(next)
        return all
    }

    private fun getNextDerivatives(it: List<Long>) = it.windowed(2, 1).map { it[1] - it[0] }
}
