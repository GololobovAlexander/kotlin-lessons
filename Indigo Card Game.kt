package indigo

import kotlin.system.exitProcess

val SUITS = listOf("♦", "♥", "♠", "♣")
val RANKS = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")

class Card(val rank: String, val suit: String) {
    fun cardToString(): String {
        return "$rank$suit"
    }
}

class Deck {
    private val deck = mutableListOf<Card>()

    init {
        for (i in RANKS) {
            for (j in SUITS) deck += Card(i, j)
        }
    }

    fun shuffle() {
        deck.shuffle()
    }

    fun get(n: Int): List<Card> {
        val gotFromDeck = deck.take(n)
        deck.removeAll(gotFromDeck)
        return gotFromDeck
    }

    fun remaining(): Int = deck.size
}

open class CardCollection {
    val cardsWon = mutableListOf<Card>()
    val collection = mutableListOf<Card>()

    fun showCollection(): String {
        return collection.joinToString(" ") { it.cardToString() }
    }

    fun isEmpty(): Boolean {
        return collection.size == 0
    }

    fun getRandomCard(): Card {
        return collection.random()
    }

    fun size(): Int {
        return collection.size
    }

    fun first(): Card {
        return collection[0]
    }
}

open class PlayersHand : CardCollection() {

    private fun printableHand(): String {
        val printableHand = this.collection.mapIndexed { i, card ->
            "${i + 1})${card.cardToString()}"}
        return printableHand.joinToString (" ")
    }

    fun refillHand(deck: Deck) {
        this.collection += deck.get(6)
    }

    private fun chooseCard(): Card {
        while (true) {
            println("Choose a card to play (1-${collection.size}):")
            val answer = readln()
            if (answer == "exit") {
                println("Game Over")
                exitProcess(0)
            }
            if (!answer.all { it.isDigit() }) continue
            if (answer.toInt() in 1..collection.size) return collection[answer.toInt() - 1]
        }
    }

    open fun makeMove(table: Table, deck: Deck): Card {
        if (this.isEmpty()) refillHand(deck)
        println("Cards in hand: ${printableHand()}")
        val chosenCard = chooseCard()
        collection.remove(chosenCard)
        return chosenCard
    }
}

class ComputersHand: PlayersHand() {

    override fun makeMove(table: Table, deck: Deck): Card {
        if (collection.size == 0) refillHand(deck)
        println(this.showCollection())
        val chosenCard = makeDecision(table)
        collection.remove(chosenCard)
        println("Computer plays ${chosenCard.cardToString()}")
        return chosenCard
    }

    private fun makeDecision(table: Table): Card {
        if (size() == 1) return first()
        val candidateCards = candidateCards(table)
        if (candidateCards.size() == 1) return candidateCards.first()
        when {
            table.isEmpty() -> return when {
                !getSameSuitCards(this).isEmpty() -> getSameSuitCards(this).getRandomCard()
                !getSameRankCards(this).isEmpty() -> getSameRankCards(this).getRandomCard()
                else -> this.getRandomCard()
            }
            !candidateCards.isEmpty() -> return when {
                !getSameSuitCards(candidateCards).isEmpty() -> getSameSuitCards(candidateCards).getRandomCard()
                !getSameRankCards(candidateCards).isEmpty() -> getSameRankCards(candidateCards).getRandomCard()
                else -> candidateCards.getRandomCard()
            }
            else -> return when {
                !getSameSuitCards(this).isEmpty() -> getSameSuitCards(this).getRandomCard()
                !getSameRankCards(this).isEmpty() -> getSameRankCards(this).getRandomCard()
                else -> this.getRandomCard()
            }
        }
    }

    private fun candidateCards(table: Table): CardCollection {
        val candidateCards = CardCollection()
        val topCardSuit = table.getTopCardOrNull()?.suit
        val topCardRank = table.getTopCardOrNull()?.rank
        val sameSuits = collection.filter { it.suit == topCardSuit }
        val sameRanks = collection.filter { it.rank == topCardRank }
        candidateCards.collection += sameSuits
        candidateCards.collection += sameRanks
        return candidateCards
    }

    private fun getSameSuitCards(cards: CardCollection): CardCollection {
        val sameSuitCards = CardCollection()
        for (suit in SUITS) {
            if (cards.collection.count { it.suit == suit } > 1) sameSuitCards.collection +=
                cards.collection.filter { it.suit == suit }
        }
        return sameSuitCards
    }

    private fun getSameRankCards(cards: CardCollection): CardCollection {
        val sameRankCards = CardCollection()
        for (rank in RANKS) {
            if (cards.collection.count { it.rank == rank } > 1) sameRankCards.collection +=
                cards.collection.filter { it.rank == rank }
        }
        return sameRankCards
    }
}

class Table: CardCollection() {

    fun initTable(deck: Deck) {
        collection += deck.get(4)
        println("Initial cards on the table: ${showCollection()}")
    }

    fun put(card: Card) {
        collection.add(card)
    }

    fun getTopCardOrNull(): Card? {
        return collection.lastOrNull()
    }

    fun clearTable() {
        collection.removeAll(collection)
    }

    fun printTable() {
        println()
        if (collection.isEmpty()) println("No cards on the table")
        else println("${collection.size} cards on the table, and the top card is ${getTopCardOrNull()?.cardToString()}")
    }
}

class IndigoCardGame {

    enum class Turn(val s: String) {
        PLAYER("Player"),
        COMPUTER("Computer")
    }

    private fun chooseTurn(): Turn {
        while (true) {
            println("Play first?")
            val answer = readln()
            if (answer == "yes") return Turn.PLAYER
            if (answer == "no") return Turn.COMPUTER
            if (answer == "exit") {
                println("Game Over")
                exitProcess(0)
            }
        }
    }

    private fun calculateScore(playersHand: PlayersHand, computersHand: ComputersHand): Pair<Int, Int> {
        var playerScore = 0
        var computerScore = 0
        for (card in playersHand.cardsWon) {
            if (listOf("A", "J", "Q", "K", "10").contains(card.rank)) playerScore += 1
        }
        for (card in computersHand.cardsWon) {
            if (listOf("A", "J", "Q", "K", "10").contains(card.rank)) computerScore += 1
        }

        return Pair(playerScore, computerScore)
    }

    private fun printScore(playersHand: PlayersHand, computersHand: ComputersHand, player: Turn) {
        println("${player.s} wins cards")
        val (playerScore, computerScore) = calculateScore(playersHand, computersHand)
        println("Score: Player $playerScore - Computer $computerScore")
        println("Cards: Player ${playersHand.cardsWon.size} - Computer ${computersHand.cardsWon.size}")
    }

    private fun printFinalScore(playersHand: PlayersHand, computersHand: ComputersHand, first: Turn) {
        var (playerScore, computerScore) = calculateScore(playersHand, computersHand)
        when {
            playersHand.cardsWon.size > computersHand.cardsWon.size -> playerScore += 3
            playersHand.cardsWon.size < computersHand.cardsWon.size -> computerScore += 3
            else -> if (first == Turn.PLAYER) playerScore += 3 else computerScore += 3
        }
        println("Score: Player $playerScore - Computer $computerScore")
        println("Cards: Player ${playersHand.cardsWon.size} - Computer ${computersHand.cardsWon.size}")
    }

    fun run() {
        println("Indigo Card Game")
        var currentPlayer = chooseTurn()
        var lastWon = currentPlayer
        val firstPlayer = currentPlayer
        val deck = Deck()
        deck.shuffle()
        val table = Table()
        table.initTable(deck)
        val playersHand = PlayersHand()
        val computersHand = ComputersHand()
        while (!(deck.remaining() == 0 && playersHand.isEmpty() && computersHand.isEmpty())) {
            table.printTable()
            val hand = if (currentPlayer == Turn.PLAYER) playersHand else computersHand
            val playedCard = hand.makeMove(table, deck)
            if (playedCard.suit == table.getTopCardOrNull()?.suit || playedCard.rank == table.getTopCardOrNull()?.rank) {
                hand.cardsWon += (table.collection + playedCard)
                lastWon = currentPlayer
                table.clearTable()
                printScore(playersHand, computersHand, currentPlayer)
            }
            else table.put(playedCard)
            currentPlayer = if (currentPlayer == Turn.PLAYER) Turn.COMPUTER else Turn.PLAYER
        }
        if (lastWon == Turn.COMPUTER) computersHand.cardsWon += table.collection
        else playersHand.cardsWon += table.collection
        table.printTable()
        printFinalScore(playersHand, computersHand, firstPlayer)
        println("Game Over")
        exitProcess(0)
    }
}

fun main() {
    val game = IndigoCardGame()
    game.run()
}