package connectfour


class ConnectFourGame {

    data class Player(val name: String, val symbol: Char, var score: Int = 0)

    private val firstPlayerSymbol = 'o'
    private val secondPlayerSymbol = '*'
    private lateinit var players: Array<Player>

    private lateinit var firstPlayerName: String
    private lateinit var secondPlayerName: String
    private lateinit var board: Array<Array<Char>>
    private var numberOfGames = 1
    private var rows: Int = 0
    private var cols: Int = 0
    private var curPlayer = 1
    private var curGame = 1


    fun setup(): Triple<String, String, Pair<Int, Int>> {
        // setup game
        println("First player's name:")
        firstPlayerName = readLine()!!

        println("Second player's name:")
        secondPlayerName = readLine()!!

        var rows = 0
        var cols = 0
        do {
            println(
                "Set the board dimensions (Rows x Columns)\n" +
                        "Press Enter for default (6 x 7)"
            )
            val inp = readLine()!!.ifBlank {
                "6 x 7"
            }
            val values = inp.lowercase().split("x")
            if (values.size < 2 || values[0] == "" || values[1] == "") {
                println("Invalid input")
                continue
            }
            try {
                rows = values[0].trim().toInt()
            } catch (e: NumberFormatException) {
                println("Invalid input")
                continue
            }
            if (rows !in 5..9) {
                println("Board rows should be from 5 to 9")
                continue
            }
            try {
                cols = values[1].trim().toInt()
            } catch (e: NumberFormatException) {
                println("Invalid input")
                continue
            }
            if (cols !in 5..9) {
                println("Board columns should be from 5 to 9")
                continue
            }
        } while (rows !in 5..9 || (cols !in 5..9))

        board = Array(rows) {
            Array(cols) {
                ' '
            }
        }
        this.rows = rows
        this.cols = cols
        players = arrayOf(
            Player(firstPlayerName, firstPlayerSymbol),
            Player(secondPlayerName, secondPlayerSymbol)
        )

        do {
            println(
                """
                Do you want to play single or multiple games?
                For a single game, input 1 or press Enter
                Input a number of games:
            """.trimIndent()
            )
            val inp = readLine()!!.ifBlank {
                "1"
            }
            try {
                numberOfGames = inp.toInt()
                if (numberOfGames == 0) throw NumberFormatException("Invalid input")
            } catch (e: NumberFormatException) {
                println("Invalid input")
                continue
            }
        } while (numberOfGames == 0)

        return Triple(firstPlayerName, secondPlayerName, Pair(rows, cols))
    }

    fun printSetup() {
        println("$firstPlayerName VS $secondPlayerName\n$rows X $cols board")
        println(if (numberOfGames == 1) "Single game" else "Total $numberOfGames games")
    }

    private fun printBoard() {
        var columnHeading = (1..cols).joinToString("") {
            "$it "
        }
        columnHeading = " $columnHeading"

        var boardBottom = (1 until cols).joinToString("") {
            "╩═"
        }
        boardBottom = "╚═$boardBottom╝"

        println(message = columnHeading)
        for (r in rows downTo 1) {
            val boardRow = board[r - 1].joinToString("║")
            println("║$boardRow║")
        }
        println(message = boardBottom)
    }

    private fun makeMove(colNum: Int, curPlayer: Int): Boolean {
        for (row in 0 until rows) {
            if (board[row][colNum - 1] == ' ') {
                board[row][colNum - 1] = players[curPlayer].symbol
                return true
            }
        }
        return false
    }

    private fun weHaveWinner(curPlayer: Int): Boolean {
        // 1. check horizontal
        for (row in board) {
            if (row.joinToString("").contains(players[curPlayer].symbol.toString().repeat(4))) {
                return true
            }
        }
        // 2. check vertical
        for (c in 1..cols) {
            val col = board.map { it[c - 1] }
            if (col.joinToString("").contains(players[curPlayer].symbol.toString().repeat(4))) {
                return true
            }
        }
        // 3. check main diagonal
        var r = 1
        for (c in 1..cols - 4 + 1) {
            // collect diagonal in board[row][col]
            val diagonal = collectMainDiagonal(r - 1, c - 1)
            if (diagonal.joinToString("").contains(players[curPlayer].symbol.toString().repeat(4))) {
                return true
            }
        }

        var c = 1
        for (r in 2..rows - 4 + 1) {
            // collect diagonal in board[row][col]
            val diagonal = collectMainDiagonal(r - 1, c - 1)
            if (diagonal.joinToString("").contains(players[curPlayer].symbol.toString().repeat(4))) {
                return true
            }
        }

        // 4. check secondary diagonal
        r = 1
        for (c in 4..cols) {
            // collect diagonal board[row][col]
            val diagonal = collectSecondaryDiagonal(r - 1, c - 1)
            if (diagonal.joinToString("").contains(players[curPlayer].symbol.toString().repeat(4))) {
                return true
            }
        }
        c = cols
        for (r in 2..rows - 4 + 1) {
            // collect diagonal board[row][col]
            val diagonal = collectSecondaryDiagonal(r - 1, c - 1)
            if (diagonal.joinToString("").contains(players[curPlayer].symbol.toString().repeat(4))) {
                return true
            }
        }

        return false
    }

    private fun collectMainDiagonal(row: Int, col: Int): Array<Char> {
        var diagonal = emptyArray<Char>()
        var (r, c) = arrayOf(row, col)
        do {
            diagonal += board[r][c]
            r++
            c++
        } while (r < rows && c < cols)
        return diagonal
    }

    private fun collectSecondaryDiagonal(row: Int, col: Int): Array<Char> {
        var diagonal = emptyArray<Char>()
        var (r, c) = arrayOf(row, col)
        do {
            diagonal += board[r][c]
            r++
            c--
        } while (r < rows && c > 0)
        return diagonal
    }

    private fun isBoardFull(): Boolean {
        fun Char.isNotSpace() = this != ' '
        return board.all { row ->
            row.all {
                it.isNotSpace()
            }
        }
    }

    private fun printScore() {
        println(
            """
                Score
                ${players[0].name}: ${players[0].score} ${players[1].name}: ${players[1].score}
                """.trimIndent()
        )
    }

    private fun resetBoard() {
        board = Array(rows) {
            Array(cols) {
                ' '
            }
        }
    }

    private fun playOneGame(): Boolean {
        var input = "go"
        curPlayer = if (curPlayer == 0) 1 else 0

        while (true) {
            println("${players[curPlayer].name}'s turn:")
            try {
                input = readLine()!!
                val colNum = input.toInt()
                if (colNum !in 1..cols) throw ColumnOutOfRangeException("The column number is out of range (1 - $cols)")
                if (makeMove(colNum, curPlayer)) {
                    printBoard()
                    if (weHaveWinner(curPlayer)) {
                        println("Player ${players[curPlayer].name} won")
                        players[curPlayer].score += 2
                        break
                    }
                    if (isBoardFull()) {
                        println("It is a draw")
                        players[0].score += 1
                        players[1].score += 1
                        break
                    }
                    curPlayer = (++curPlayer) % 2
                } else {
                    println("Column $colNum is full")
                }

            } catch (e: NumberFormatException) {
                if (input == "end") {
                    return false
                }
                println("Incorrect column number")
            } catch (e: ColumnOutOfRangeException) {
                println(e.message)
            }
        }
        return true
    }

    fun play() {
        if (numberOfGames == 1) {
            printBoard()
            playOneGame()
        } else {
            gameOver@ while (curGame <= numberOfGames) {
                println("Game #$curGame")
                printBoard()
                if (!playOneGame()) {
                    break@gameOver
                }
                printScore()
                curGame++
                resetBoard()
            }
        }
        println("Game over!")
    }
}

class ColumnOutOfRangeException(message: String) : Exception(message)


fun main() {

    val game = ConnectFourGame()
    println("Connect Four")
    game.setup()
    game.printSetup()
    game.play()
}
