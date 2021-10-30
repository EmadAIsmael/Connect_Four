package connectfour

fun setup(): Triple<String, String, Pair<Int, Int>> {
    //
    println("First player's name:")
    val fpn = readLine()!!

    println("Second player's name:")
    val spn = readLine()!!

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

    return Triple(fpn, spn, Pair(rows, cols))
}

fun printBoard(rows: Int, columns: Int) {
    var columnHeading = (1..columns)
        .map {
            "$it "
        }.joinToString("")
    columnHeading = " $columnHeading"

    val boardRow = (1..columns + 1)
        .map {
            "║ "
        }.joinToString("")

    var boardBottom = (1 until columns)
        .map {
            "╩═"
        }.joinToString("")
    boardBottom = "╚═$boardBottom╝"

    println(message = columnHeading)
    for (r in 1..rows) {
        println(boardRow)
    }
    println(message = boardBottom)
}

fun main() {
    println("Connect Four")
    val game = setup()
    println("${game.first} VS ${game.second}\n${game.third.first} X ${game.third.second} board")
    printBoard(game.third.first, game.third.second)
}