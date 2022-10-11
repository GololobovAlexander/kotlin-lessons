fun main() {

    fun createField(): MutableList<MutableList<Char>> {
        return mutableListOf(
            mutableListOf(' ', ' ', ' '),
            mutableListOf(' ', ' ', ' '),
            mutableListOf(' ', ' ', ' ')
        )
    }

    val playField = createField()

    fun printField(list: MutableList<MutableList<Char>>) {
        println("---------")
        println("| ${list[0][0]} ${list[0][1]} ${list[0][2]} |")
        println("| ${list[1][0]} ${list[1][1]} ${list[1][2]} |")
        println("| ${list[2][0]} ${list[2][1]} ${list[2][2]} |")
        println("---------")
    }

    fun callInput(sign : Char) {
        while(true) {
            val userInput = readLine()!!
            val uI = userInput.trim().split(" ")
            when {
                userInput.trim().length in 0..2 -> println("Please input coordinates in format 'N N'")
                uI[0][0].code !in (48..57) -> println("You should enter numbers!")
                uI[1][0].code !in (48..57) -> println("You should enter numbers!")
                uI[0][0].code !in (49..51) -> println("Coordinates should be from 1 to 3!")
                uI[1][0].code !in (49..51) -> println("Coordinates should be from 1 to 3!")
                playField[uI[0][0].code - 49][uI[1][0].code - 49] in "XO" ->
                    println("This cell is occupied! Choose another one!")
                else -> {
                    playField[uI[0][0].code - 49][uI[1][0].code - 49] = sign
                    break
                }
            }
        }
    }

    fun checkWinCondition(list: MutableList<MutableList<Char>>): Boolean {
        val winConditions = mutableListOf(
            list[0][0].toString() + list[0][1] + list[0][2],
            list[1][0].toString() + list[1][1] + list[1][2],
            list[2][0].toString() + list[2][1] + list[2][2],
            list[0][0].toString() + list[1][0] + list[2][0],
            list[0][1].toString() + list[1][1] + list[2][1],
            list[0][2].toString() + list[1][2] + list[2][2],
            list[0][0].toString() + list[1][1] + list[2][2],
            list[0][2].toString() + list[1][1] + list[2][0]
        )
        for (winCond in winConditions) {
            if ((winCond == "XXX") || (winCond == "OOO")) {
                return true
            }
        }
        return false
    }

    fun checkDraw():Boolean {
        return (' ' !in playField[0] && ' ' !in playField[1] && ' ' !in playField[2])
    }

    printField(playField)
    var turnCount = 0
    mainLoop@ while(true) {
        callInput(if (turnCount%2 == 0) {'X'} else {'O'})
        printField(playField)
        if (checkWinCondition(playField)) {
            print (if (turnCount%2 == 0) {'X'} else {'O'} + " wins")
            break@mainLoop
        }
        if (checkDraw()) {
            print("Draw")
            break@mainLoop
        }
        turnCount++
    }
}
