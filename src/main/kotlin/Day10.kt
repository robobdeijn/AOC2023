import java.io.File
import kotlin.math.ceil
import kotlin.time.measureTime

fun main() {
    println(Maze.create(File("src/main/resources/day10.txt").readLines()).maxDistanceFromStart())

    println(measureTime {
        println(Maze.create(File("src/main/resources/day10.txt").readLines()).enclosingCount())
    }
    )
}

class Maze(private var layout: List<List<PipeType>>) {
    companion object Factory {
        fun create(input: List<String>): Maze {
            val layout = input.map { it.map { char -> PipeType.getInstance(char) } }
            return Maze(layout)
        }
    }

    private fun getStartingPosition(): StartingPoint {
        val position = layout.indices.flatMap { i ->
            layout[i].indices.map { j -> Position(j, i) }.filter { layout[it.y][it.x] == PipeType.START }
        }.first()

        val north = getAt(position.north())
        val east = getAt(position.east())
        val south = getAt(position.south())
        if (north in listOf(PipeType.VERTICAL, PipeType.SOUTH_TO_WEST, PipeType.SOUTH_TO_EAST)) {
            return StartingPoint(position, Direction.NORTH)
        } else if (east in listOf(PipeType.HORIZONTAL, PipeType.NORTH_TO_WEST, PipeType.SOUTH_TO_WEST)) {
            return StartingPoint(position, Direction.EAST)
        } else if (south in listOf(PipeType.VERTICAL, PipeType.NORTH_TO_WEST, PipeType.NORTH_TO_WEST)) {
            return StartingPoint(position, Direction.SOUTH)
        }

        return StartingPoint(position, Direction.WEST)
    }

    fun maxDistanceFromStart(): Int {
        val visited = getRoute()

        return ceil(visited.size / 2.0).toInt()
    }

    fun enclosingCount(): Int {
        val route = getRoute()
        val (topLeft, botRight) = getBoundingBox(route)
        val potentialNests = findNests(topLeft, botRight, route)
        val encapsulatedNests = potentialNests.filter { isEncapsulated(it, route) }
        val encapsulatedCount = encapsulatedNests.map { it.size }.sumOf { it }
        return encapsulatedCount
    }

    private fun isEncapsulated(nest: List<Position>, route: MutableSet<Position>): Boolean {
        return nest.none { canEscape(it, route) }
    }

    private fun canEscape(position: Position, route: MutableSet<Position>): Boolean {
        return upAndLeftDiagonal(position, route) % 2 == 0 || downAndLeftDiagonal(position, route) % 2 == 0

    }

    private fun upAndLeftDiagonal(position: Position, route: MutableSet<Position>): Int {
        var walls = 0
        var currentPosition = Position(position.x - 1, position.y - 1)
        while (currentPosition.x > 0 && currentPosition.y > 0) {
            val countWallsLeftAndUp = countWallsLeftAndUp(currentPosition, route)
            walls += countWallsLeftAndUp
            currentPosition = Position(currentPosition.x - 1, currentPosition.y - 1)
        }

        return walls
    }

    private fun downAndLeftDiagonal(position: Position, route: MutableSet<Position>): Int {
        var walls = 0
        var currentPosition = Position(position.x - 1, position.y + 1)
        while (currentPosition.x > 0 && currentPosition.y > 0) {
            walls += countWallsLeftAndDown(currentPosition, route)
            currentPosition = Position(currentPosition.x - 1, currentPosition.y + 1)
        }

        return walls
    }


    private fun countWallsLeftAndUp(currentPosition: Position, route: MutableSet<Position>): Int {
        if (currentPosition !in route) {
            return 0
        }
        var at = getAt(currentPosition)
        if (at == PipeType.START) {
            at = getActualTypeForStartPos(currentPosition)
        }
        return when (at) {
            PipeType.NORTH_TO_EAST, PipeType.SOUTH_TO_WEST -> 2
            PipeType.VERTICAL, PipeType.HORIZONTAL, PipeType.NORTH_TO_WEST, PipeType.SOUTH_TO_EAST -> 1
            PipeType.EMPTY -> 0
            PipeType.START -> 0
            PipeType.OUTER -> 0
        }
    }


    private fun countWallsLeftAndDown(currentPosition: Position, route: MutableSet<Position>): Int {
        if (currentPosition !in route) {
            return 0
        }
        var at = getAt(currentPosition)
        if (at == PipeType.START) {
            at = getActualTypeForStartPos(currentPosition)
        }
        return when (at) {
            PipeType.NORTH_TO_WEST, PipeType.SOUTH_TO_EAST -> 2
            PipeType.VERTICAL, PipeType.HORIZONTAL, PipeType.NORTH_TO_EAST, PipeType.SOUTH_TO_WEST -> 1
            PipeType.EMPTY -> 0
            PipeType.START -> 0
            PipeType.OUTER -> 0
        }
    }

    private fun getActualTypeForStartPos(position: Position): PipeType {
        val north = getAt(position.north())
        val east = getAt(position.east())
        val south = getAt(position.south())

        var northConnection = false
        var eastConnection = false
        var southConnection = false
        if (north in listOf(PipeType.VERTICAL, PipeType.SOUTH_TO_WEST, PipeType.SOUTH_TO_EAST)) {
            northConnection = true
        }

        if (east in listOf(PipeType.HORIZONTAL, PipeType.NORTH_TO_WEST, PipeType.SOUTH_TO_WEST)) {
            eastConnection = true
        }

        if (south in listOf(PipeType.VERTICAL, PipeType.NORTH_TO_WEST, PipeType.NORTH_TO_WEST)) {
            southConnection = true
        }
        if (northConnection) {
            if (eastConnection) {
                return PipeType.NORTH_TO_EAST
            }
            if (southConnection) {
                return PipeType.VERTICAL
            }
            return PipeType.NORTH_TO_WEST
        }
        if (eastConnection) {
            if (southConnection) {
                return PipeType.SOUTH_TO_EAST
            }
            return PipeType.SOUTH_TO_WEST
        }
        return PipeType.HORIZONTAL
    }


    private fun findNests(
        topLeft: Position,
        botRight: Position,
        route: MutableSet<Position>
    ): MutableSet<MutableList<Position>> {
        val nests = mutableSetOf<MutableList<Position>>()
        for (y in topLeft.y..botRight.y) {
            for (x in topLeft.x..botRight.x) {
                if (Position(x, y) !in route) {
                    val find = nests.find { it.any { pos -> pos.isAdjacent(Position(x, y)) } }
                    if (find == null) {
                        nests.add(mutableListOf(Position(x, y)))
                    } else {
                        find.add(Position(x, y))
                    }
                }
            }
        }
        return nests
    }

    private fun getBoundingBox(route: MutableSet<Position>): Pair<Position, Position> {
        val minX = route.minByOrNull { it.x }!!.x
        val minY = route.minByOrNull { it.y }!!.y
        val maxX = route.maxByOrNull { it.x }!!.x
        val maxY = route.maxByOrNull { it.y }!!.y
        return Pair(Position(minX, minY), Position(maxX, maxY))

    }

    private fun getRoute(): MutableSet<Position> {
        val startingPosition = getStartingPosition()
        val visited = mutableSetOf<Position>();
        visited.add(startingPosition.pos)
        var currentPos = startingPosition.getNext()
        while (currentPos != startingPosition.pos) {
            visited.add(currentPos)
            val connections = getConnections(currentPos)
            currentPos = if (connections.first in visited) connections.second!! else connections.first!!
        }
        return visited
    }

    private fun getConnections(pos: Position): Pair<Position?, Position?> {
        return Pair(pos.next(getAt(pos).end()), pos.next(getAt(pos).start()))
    }

    private fun getAt(pos: Position): PipeType {
        if (pos.x < 0 || pos.y < 0 || pos.y > layout.size || pos.x > layout[pos.y].size) {
            return PipeType.getInstance('O')
        }
        return layout[pos.y][pos.x]
    }


}

enum class PipeType(private val type: Char) {
    VERTICAL('|'),
    HORIZONTAL('-'),
    NORTH_TO_EAST('L'),
    NORTH_TO_WEST('J'),
    SOUTH_TO_WEST('7'),
    SOUTH_TO_EAST('F'),
    EMPTY('.'),
    START('S'),
    OUTER('O');

    companion object Factory {
        fun getInstance(type: Char): PipeType {
            val first = entries.firstOrNull() { it.type == type }

            if (first == null) {
                throw Exception("type not found: $type")
            }

            return first
        }
    }

    fun start(): Direction {
        return when (this) {
            VERTICAL -> Direction.NORTH
            HORIZONTAL -> Direction.WEST
            NORTH_TO_EAST -> Direction.NORTH
            NORTH_TO_WEST -> Direction.NORTH
            SOUTH_TO_WEST -> Direction.SOUTH
            SOUTH_TO_EAST -> Direction.SOUTH
            EMPTY -> Direction.NONE
            OUTER -> Direction.NONE
            START -> Direction.NONE
        }
    }

    fun end(): Direction {
        return when (this) {
            VERTICAL -> Direction.SOUTH
            HORIZONTAL -> Direction.EAST
            NORTH_TO_EAST -> Direction.EAST
            NORTH_TO_WEST -> Direction.WEST
            SOUTH_TO_WEST -> Direction.WEST
            SOUTH_TO_EAST -> Direction.EAST
            EMPTY -> Direction.NONE
            OUTER -> Direction.NONE
            START -> Direction.NONE
        }
    }

}

enum class Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST,
    NONE
}

data class Position(val x: Int, val y: Int) {

    fun withinBoundingBox(topLeft: Position, botRight: Position): Boolean {
        return x >= topLeft.x && x <= botRight.x && y >= topLeft.y && y <= botRight.y
    }

    fun isAdjacent(other: Position): Boolean {
        return other.x in x - 1..x + 1 && other.y in y - 1..y + 1
    }

    fun next(direction: Direction): Position? {
        return when (direction) {
            Direction.NORTH -> north()
            Direction.EAST -> east()
            Direction.SOUTH -> south()
            Direction.WEST -> west()
            Direction.NONE -> null
        }
    }

    fun north(): Position {
        return Position(x, y - 1)
    }

    fun south(): Position {
        return Position(x, y + 1)
    }

    fun east(): Position {
        return Position(x + 1, y)
    }

    fun west(): Position {
        return Position(x - 1, y)
    }
}

data class StartingPoint(val pos: Position, val connection: Direction) {
    fun getNext(): Position {
        return pos.next(connection)!!
    }
}
