import java.io.File
import kotlin.math.pow
import kotlin.time.measureTime

fun main() {
    val value =
        File("src/main/resources/day4.txt").useLines { l -> l.map { ScratchCard.create(it) }.sumOf { it.value() } }
    println("Total value: $value")

    println( measureTime {
        val cardCount =
            ScratchCardGame.create(File("src/main/resources/day4.txt").readLines()).getScratchcardCount()
        println("Total number of scratch cards: $cardCount")
    })
}

class ScratchCardGame(private val scratchCards: List<ScratchCard>) {
    companion object Factory {
        fun create(scratchCardString: List<String>): ScratchCardGame =
            ScratchCardGame(scratchCardString.map { ScratchCard.create(it) })
    }

    fun getScratchcardCount() : Int {
        val winCounts = scratchCards.map { scratchCard -> scratchCard.winCount() }
        val processed = mutableListOf<Pair<Int, Int>>()

        for ((index,value) in winCounts.withIndex()) {
            fun previousCount() =
                processed.filterIndexed { innerIndex, pair -> innerIndex + pair.first >= index }.sumOf { it.second }
            processed.add(Pair(value, 1 + previousCount()))
        }

        return processed.sumOf { it.second }
    }

}

class ScratchCard(val number: Int, private val winningNumbers: List<Int>, val actualNumbers: List<Int>) {
    companion object Factory {
        fun create(scratchCardString: String): ScratchCard {
            val whitespacesRegex = "\\s+".toRegex()

            val parts = scratchCardString.split(":")
            val num = parts[0].split(whitespacesRegex)[1].toInt()
            val subParts = parts[1].split("|").map { it.trim() }

            val winning = subParts[0].split(whitespacesRegex).map { it.toInt() }
            val actual = subParts[1].split(whitespacesRegex).map { it.toInt() }
            return ScratchCard(num, winning, actual);
        }
    }

    fun value(): Double {
        val matches = winCount()
        if (matches > 0) {
            return 2.0.pow(matches - 1)
        }
        return 0.0;
    }

    fun winCount(): Int = winningNumbers.toSet().intersect(actualNumbers.toSet()).size
}
