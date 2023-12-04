import java.io.File
import kotlin.math.pow

fun main() {
    val value =
        File("src/main/resources/day4.txt").useLines { l -> l.map { ScratchCard.create(it) }.sumOf { it.value() } }
    println("Total value: $value")

    val cardCount = ScratchCardGame.create(File("src/main/resources/day4.txt").readLines()).getTotalScratchCardCount()
    println("Total value: $cardCount")
}

class ScratchCardGame(private val scratchCards: List<ScratchCard>) {
    companion object Factory {
        fun create(scratchCardString: List<String>): ScratchCardGame =
            ScratchCardGame(scratchCardString.map { ScratchCard.create(it) })
    }

    fun getTotalScratchCardCount(): Int {
        val indexToCards = scratchCards.map { scratchCard -> scratchCard.number to mutableListOf(scratchCard) }.toMap()
        indexToCards.forEach { (_, scratchCardList) ->
            scratchCardList.forEach { sc ->
                (1..sc.winCount()).forEach { indexToCards[sc.number + it]?.add(scratchCards[sc.number + it -1]) }
            }
        }
        return indexToCards.values.flatten().size;
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
