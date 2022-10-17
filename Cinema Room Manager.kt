package cinema

var ticketsPurchased = 0 // that's bad
var currentIncome = 0

fun createSeats(x: Int, y: Int): MutableList<MutableList<Char>> {
    val list = mutableListOf<MutableList<Char>>()
    val row = mutableListOf<Char>()
    for (i in 0 until y) row.add('S')
    for (j in 0 until x) list.add(row.toMutableList())
    return list
}

fun printSeats(list2D: List<List<Char>>) {
    println()
    println("Cinema:")
    var rowNumbers = " "
    for (i in 1..list2D[0].size) rowNumbers += " $i"
    println(rowNumbers)
    for (j in 1..list2D.size) {
        println("$j " + list2D[j-1].joinToString(" "))
    }
    println()
}

fun buyTicket(seats: MutableList<MutableList<Char>>, rows: Int, cols: Int) {
    val seatsAmount = rows * cols
    while (true) {
        println()
        println("Enter a row number:")
        val row = readln().toInt()
        println("Enter a seat number in that row:")
        val seat = readln().toInt()
        if (row > seats.size || seat > seats[0].size) {
            println()
            println("Wrong input!")
            continue
        }
        if (seats[row - 1][seat - 1] == 'B') println("\nThat ticket has already been purchased!")
        else {
            seats[row - 1][seat - 1] = 'B'
            val price = if (seatsAmount > 60 && row > rows / 2) 8 else 10
            println()
            println("Ticket price: $$price")
            currentIncome += price
            ticketsPurchased++
            break
        }
    }
}

fun printStatistics(rows: Int, cols: Int) {
    println()
    println("Number of purchased tickets: $ticketsPurchased")
    val percentage = ticketsPurchased.toDouble() * 100 / (rows.toDouble() * cols.toDouble())
    val formatPercentage = "%.2f".format(percentage)
    println("Percentage: ${formatPercentage}%")
    println("Current income: $$currentIncome")
    val totalIncome = if (rows * cols > 60) {rows / 2 * 10 * cols + (rows + 1) / 2 * 8 * cols} else {rows * cols * 10}
    println("Total income: $$totalIncome")
}

fun main() {
    println("Enter the number of rows:")
    val rows = readln().toInt()
    println("Enter the number of seats in each row:")
    val cols = readln().toInt()
    val seats = createSeats(rows, cols)
    while (true) {
        println()
        println("1. Show the seats")
        println("2. Buy a ticket")
        println("3. Statistics")
        println("0. Exit")
        when(readln()) {
            "1" -> printSeats(seats)
            "2" -> buyTicket(seats, rows, cols)
            "3" -> printStatistics(rows, cols)
            "0" -> break
            else -> println("Wrong input!")
        }
    }
}
