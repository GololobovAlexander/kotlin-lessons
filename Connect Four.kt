package connectfour

enum class Marker(val s: String) {
    ROUND("o"),
    ASTERISK("*"),
    EMPTY(" ")
}

enum class Player {
    FIRST,
    SECOND
}

class ConnectFour {
    private var x: Int = 6
    private var y: Int = 7
    private var firstPlayerScore = 0
    private var secondPlayerScore = 0

    private fun makeBoard(x: Int, y: Int): Array<Array<Marker>> {
        return Array(x) { Array(y) { Marker.EMPTY} }
    }

    private fun printBoard(arr: Array<Array<Marker>>) {
        for (i in arr[0].indices) print(" ${i+1}")
        println()
        for (i in arr.indices) {
            print("║")
            for (j in arr[i]) {
                print("${j.s}║")
            }
            println()
        }
        print("╚═")
        for (i in arr[0].indices.drop(1)) print("╩═")
        println("╝")
    }

    private fun getBoardSize(): Pair<Int, Int> {
        while (true) {
            println("Set the board dimensions (Rows x Columns)")
            println("Press Enter for default (6 x 7)")
            val inputNoSpaces = readln().replace("\\s".toRegex(), "")
            val reg = "\\d+[X|x]\\d+".toRegex()
            val lst = inputNoSpaces.split(Regex("[x|X]"))
            return when {
                inputNoSpaces == "" -> x to y
                !inputNoSpaces.matches(reg) -> {
                    println("Invalid input")
                    continue
                }
                lst[0][0] !in '5'..'9' -> {
                        println("Board rows should be from 5 to 9")
                        continue
                }
                lst[1][0] !in '5'..'9' -> {
                    println("Board columns should be from 5 to 9")
                    continue
                }
                else -> lst[0].toInt() to lst[1].toInt()
            }
        }
    }

    private fun makeMove(col: Int, player: Player, board: Array<Array<Marker>>): Boolean {
        for (i in board.indices.reversed()) {
            return if (board[i][col-1] == Marker.EMPTY) {
                if (player == Player.FIRST) {
                    board[i][col-1] = Marker.ROUND
                    printBoard(board)
                    true
                } else {
                    board[i][col-1] = Marker.ASTERISK
                    printBoard(board)
                    true
                }
            } else continue

        }
        println("Column $col is full")
        return false
    }

    private fun checkWinConditionHorizontal(board: Array<Array<Marker>>): Boolean {
        for (i in board.indices) {
            for (j in 0..board[i].size - 4) {
                if (board[i][j] == board[i][j + 1] &&
                    board[i][j] == board[i][j + 2] &&
                    board[i][j] == board[i][j + 3] &&
                    board[i][j] != Marker.EMPTY) return true
                else continue
            }
        }
        return false
    }

    private fun checkWinConditionVertical(board: Array<Array<Marker>>): Boolean {
        for (i in 0..board.size - 4) {
            for (j in 0 until board[i].size) {
                if (board[i][j] == board[i + 1][j] &&
                    board[i][j] == board[i + 2][j] &&
                    board[i][j] == board[i + 3][j] &&
                    board[i][j] != Marker.EMPTY) return true
                else continue
            }
        }
        return false
    }

    private fun checkWinConditionDiagonalInc(board: Array<Array<Marker>>): Boolean {
        for (i in 0..board.size - 4) {
            for (j in 0..board[i].size - 4) {
                if (board[i][j] == board[i + 1][j + 1] &&
                    board[i][j] == board[i + 2][j + 2] &&
                    board[i][j] == board[i + 3][j + 3] &&
                    board[i][j] != Marker.EMPTY) return true
                else continue
            }
        }
        return false
    }

    private fun checkWinConditionDiagonalDec(board: Array<Array<Marker>>): Boolean {
        for (i in 3 until board.size) {
            for (j in 0..board[i].size - 4) {
                if (board[i][j] == board[i - 1][j + 1] &&
                    board[i][j] == board[i - 2][j + 2] &&
                    board[i][j] == board[i - 3][j + 3] &&
                    board[i][j] != Marker.EMPTY) return true
                else continue
            }
        }
        return false
    }

    private fun checkWinCondition(board: Array<Array<Marker>>): Boolean {
        return  checkWinConditionHorizontal(board) ||
                checkWinConditionVertical(board) ||
                checkWinConditionDiagonalInc(board) ||
                checkWinConditionDiagonalDec(board)
    }

    private fun checkDraw(board: Array<Array<Marker>>): Boolean {
        for (i in board) {
            for (j in i) {
                if (j == Marker.EMPTY) return false
            }
        }
        return true
    }

    private fun getNumberOfGames(): Int {
        val numberRegex = Regex("\\d+")
        while (true) {
            println("Do you want to play single or multiple games?")
            println("For a single game, input 1 or press Enter")
            println("Input a number of games:")
            val num = readln()
            if (num == "") return 1
            if (num.matches(numberRegex) && num.toInt() > 0) return num.toInt()
            else {
                println("Invalid input")
            }
        }
    }

    fun run() {
        println("Connect Four")
        println("First player's name:")
        val firstName = readln()
        println("Second player's name:")
        val secondName = readln()
        val boardDimensions = getBoardSize()
        x = boardDimensions.first
        y = boardDimensions.second
        val numberOfGames = getNumberOfGames()
        println("$firstName VS $secondName")
        println("$x X $y board")
        var player = Player.FIRST
        if (numberOfGames == 1) {
            println("Single game")
        }
        else println("Total $numberOfGames games")
        for (i in 1..numberOfGames) {
            var board = makeBoard(x, y)
            if (numberOfGames > 1) println("Game #$i")
            printBoard(board)
            loop@while (true) {
                if (player == Player.FIRST) println("$firstName's turn:") else println("$secondName's turn:")
                val inp = readln()
                when {
                    inp == "end" -> {
                        println("Game over!")
                        break@loop
                    }
                    !inp.matches(Regex("\\d+")) -> println("Incorrect column number")
                    inp.toInt() !in 1..y-> println("The column number is out of range (1 - $y)")
                    else -> {
                        val flag = makeMove(inp.toInt(), player, board)
                        if (checkWinCondition(board)) {
                            val plName = if (player == Player.FIRST) firstName else secondName
                            println("Player $plName won")
                            if (player == Player.FIRST) firstPlayerScore += 2 else secondPlayerScore += 2
                            break
                        }
                        if (checkDraw(board)) {
                            println("It is a draw")
                            firstPlayerScore += 1
                            secondPlayerScore += 1
                            break
                        }
                        if (flag) {
                            player = if (player == Player.FIRST) Player.SECOND else Player.FIRST
                        }
                    }
                }
            }
            player = if (player == Player.FIRST) Player.SECOND else Player.FIRST
            println("Score")
            println("$firstName: $firstPlayerScore $secondName: $secondPlayerScore")
        }
        println("Game over!")
    }


    fun gameLoop(firstName: String, secondName: String, board: Array<Array<Marker>>, firstTurnPlayer: Player) {

    }
}

fun main() {
    val connectFour = ConnectFour()
    connectFour.run()
}


