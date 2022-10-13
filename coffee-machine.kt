package machine

enum class State {
    DEFAULT,
    BUY,
    WATER,
    MILK,
    BEANS,
    CUPS
}

enum class Coffee(val water: Int, val milk: Int, val beans: Int, val cups: Int, val price: Int) {
    ESPRESSO(250, 0, 16, 1, 4),
    LATTE(350, 75, 20, 1, 7),
    CAPPUCCINO(200, 100, 12, 1, 6)
}

class Main {
    private var waterAmount = 400
    private var milkAmount = 540
    private var beansAmount = 120
    private var cupsAmount = 9
    private var moneyAmount = 550
    private var state = State.DEFAULT

    fun run(input: String) {
        when (state) {
            State.DEFAULT -> fromDefault(input)
            State.BUY -> fromBuy(input)
            State.WATER -> {
                println("Write how many ml of water you want to add:")
                waterAmount += input.toInt()
                state = State.MILK
            }
            State.MILK -> {
                println("Write how many disposable cups you want to add:")
                milkAmount += input.toInt()
                state = State.BEANS
            }
            State.BEANS -> {
                println("Write how many ml of milk you want to add:")
                beansAmount += input.toInt()
                state = State.CUPS
            }
            State.CUPS -> {
                println("Write how many grams of coffee beans you want to add:")
                cupsAmount += input.toInt()
                state = State.DEFAULT
            }
        }
    }

    private fun fromDefault(input: String) {
        when (input) {
            "buy" -> {
                println("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:")
                state = State.BUY}
            "fill" -> state = State.WATER
            "take" -> take()
            "remaining" -> printState()
            "exit" -> state = State.DEFAULT
        }
    }

    private fun take() {
        println("I gave you $moneyAmount")
        moneyAmount = 0
        state = State.DEFAULT
    }

    private fun fromBuy(input: String) {
        when (input) {
            "1" -> makeCoffee(1)
            "2" -> makeCoffee(2)
            "3" -> makeCoffee(3)
            "back" -> state = State.DEFAULT
            else -> state = State.DEFAULT
        }
    }

    private fun makeCoffee(num: Int) {
        val coffeeType = Coffee.values()[num-1]
        when {
            coffeeType.water > waterAmount -> println("Sorry, not enough water!")
            coffeeType.milk > milkAmount -> println("Sorry, not enough milk!")
            coffeeType.cups > cupsAmount -> println("Sorry, not enough disposable cups!")
            coffeeType.beans > beansAmount -> println("Sorry, not enough coffee beans!")
            else -> {
                println("I have enough resources, making you a coffee!")
                waterAmount -= coffeeType.water
                milkAmount -= coffeeType.milk
                cupsAmount -= coffeeType.cups
                beansAmount -= coffeeType.beans
                moneyAmount += coffeeType.price
                state = State.DEFAULT
            }

        }
        state = State.DEFAULT
    }

    private fun printState() {
        println("The coffee machine has:")
        println("$waterAmount ml of water")
        println("$milkAmount ml of milk")
        println("$beansAmount g if coffee beans")
        println("$cupsAmount disposable cups")
        println("$$moneyAmount of money")
    }
}

fun main() {
    val coffeeM = Main()
    while(true){
        val sc = readln()
        if (sc == "exit") break
        coffeeM.run(sc)
    }
}
