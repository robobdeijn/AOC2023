import java.io.File

fun main() {
    val result = File("src/main/resources/day2.txt").useLines { l ->
        l.map { Game.create(it) }.filter { it.isValid(12, 14 ,13) }.map { it.gameNo }.sum()
    }
    println("Sum: $result")

    val powersum = File("src/main/resources/day2.txt").useLines { l ->
        l.map { Game.create(it) }.map { it.power() }.sum()
    }
    println("Sum: $powersum")
}
