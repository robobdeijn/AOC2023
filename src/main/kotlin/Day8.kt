import java.io.File


fun main() {
    println("Part1: ${Instruction.create(File("src/main/resources/day8.txt").readLines()).solve1()}")
    println("Part2: ${Instruction.create(File("src/main/resources/day8.txt").readLines()).solve2()}")
}

class Instruction(private val directions: String, private val nodes: Map<String, Node>) {
    companion object Factory {
        fun create(input: List<String>): Instruction {
            return Instruction(input[0].trim(), input.drop(2).map { getNode(it) }.associateBy { it.key })
        }

        private fun getNode(it: String): Node {
            val parts = it.split(" = ")
            val subParts = parts[1].substring(1..(parts[1].length - 2)).split(", ")
            return Node(parts[0], subParts[0], subParts[1])
        }
    }

    fun solve1(): Int {
        var currentLocation = "AAA"
        return getStepsForNode(currentLocation, target = { it == "ZZZ" })
    }

    private fun getStepsForNode(startingLocation: String, target: (loc: String) -> Boolean): Int {
        var currentLocation = startingLocation
        var steps = 0
        var found = false
        while (!found) {
            currentLocation =
                (if (directions[steps % directions.length] == 'L') nodes[currentLocation]?.left else nodes[currentLocation]?.right)!!
            steps++
            found = target(currentLocation)
        }

        return steps
    }

    fun solve2(): Long {
        var startingNodes = nodes.filter { it.key.endsWith('A') }

        val stepsPerNode = startingNodes.map { getStepsForNode(it.value.key, target = { loc -> loc.endsWith('Z') }).toLong() }

        return lowestCommonMultiplier(stepsPerNode)
    }

    private fun lowestCommonMultiplier(a: Long, b: Long): Long {
        val larger = if (a > b) a else b
        val maxLcm = a * b
        var lcm = larger
        while (lcm <= maxLcm) {
            if (lcm % a == 0L && lcm % b == 0L) {
                return lcm
            }
            lcm += larger
        }
        return maxLcm
    }

    private fun lowestCommonMultiplier(numbers: List<Long>): Long {
        var result = numbers[0]
        for (i in 1..<numbers.size) {
            result = lowestCommonMultiplier(result, numbers[i])
        }
        return result
    }

}

data class Node(val key: String, val left: String, val right: String)
