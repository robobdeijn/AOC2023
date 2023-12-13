import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {

//    println("Part1: ${Universe.create(File("src/main/resources/day11.txt").readLines()).solve(1)}")
    println("Part2: ${Universe.create(File("src/main/resources/day11.txt").readLines()).solve((999999L))}")

    //2112733540
}

class Universe (private val galaxies: List<Galaxy>) {
    private val rowsWithGalaxies = rowsWithGalaxies()
    private val columnsWithGalaxies = columnsWithGalaxies()

    private fun rowsWithGalaxies(): List<Int> {
        return galaxies.map { it.y }
    }

    private fun columnsWithGalaxies(): List<Int> {
        return galaxies.map { it.x }
    }

    companion object Factory  {
        fun create(input: List<String>) : Universe {
            return Universe(input.flatMapIndexed { y, s ->
                s.mapIndexed { x, c -> Triple(x, y, c) }.filter { it.third == '#' }.map { Galaxy(it.first, it.second) }
            })
        }

    }

    fun solve(expansionFactor: Long) : Long {
        return getAllPairs(galaxies).map { getDistance(it, expansionFactor) }.sumOf { it }
    }

    private fun getDistance(galaxyPair: Pair<Galaxy, Galaxy>, expansionFactor: Long) : Long {
        val distanceX = galaxyPair.second.x - galaxyPair.first.x
        val distanceY = galaxyPair.second.y - galaxyPair.first.y
        val emptyColumns = (min(galaxyPair.first.x, galaxyPair.second.x) + 1..<max(
            galaxyPair.first.x,
            galaxyPair.second.x
        )).count { it !in columnsWithGalaxies } * expansionFactor
        val emptyRows = (min(galaxyPair.first.y, galaxyPair.second.y) + 1..<max(
            galaxyPair.first.y,
            galaxyPair.second.y
        )).count { it: Int -> it !in rowsWithGalaxies } * expansionFactor
        return abs(distanceX) + abs(distanceY) + emptyRows + emptyColumns

    }

    private fun getAllPairs(galaxies: List<Galaxy>) : List<Pair<Galaxy, Galaxy>> {
        return galaxies.indices.flatMap { galaxies.drop(it + 1).map {inner -> Pair(galaxies[it],inner) }  }
    }

}

data class Galaxy (val x: Int, val y: Int)
