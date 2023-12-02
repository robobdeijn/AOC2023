class Game(val gameNo: Int, val maxRed: Int, val maxBlue: Int, val maxGreen: Int) {
    companion object Factory {
        fun create(gameString: String) : Game {
            val gameNo = gameString.split(":").first.split(" ").last.toInt()
            val draws = gameString.split(":").last.split(";").map { Draw.create(it) }
            return Game(gameNo, draws.map { it.red }.max(), draws.map { it.blue }.max(), draws.map { it.green }.max())
        }
    }
    fun isValid(redAllowed: Int, blueAllowed: Int, greenAllowed: Int) : Boolean {
        return redAllowed >= maxRed && blueAllowed >= maxBlue && greenAllowed >= maxGreen
    }

    fun power() : Int {
        return maxRed * maxGreen * maxBlue
    }
}

data class Draw(val red: Int, val green: Int, val blue: Int) {
    companion object Factory {
        fun create(pullString: String) : Draw {
            var red = 0; var green = 0; var blue = 0

            pullString.split(",").map { it.trim() }.forEach {
                val singleColor = it.split(" ")
                val colorCount = singleColor.first.toInt()
                when(singleColor.last) {
                    "red" -> red = colorCount
                    "green" -> green = colorCount
                    "blue" -> blue = colorCount
                }
            }
            return Draw(red, green, blue)
        }
    }
}
