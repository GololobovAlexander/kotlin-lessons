package cryptography
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.lang.Integer.toBinaryString
import javax.imageio.IIOException
import javax.imageio.ImageIO


fun main() {
    while(true) {
        println("Task (hide, show, exit):")
        when (val input = readln()) {
            "show" -> show()
            "hide" -> hide()
            "exit" -> {
                println("Bye!")
                break
            }
            else -> println("Wrong task: $input")
        }
    }
}

fun show() {
    println("Input image file:")
    val inputImagePath = readln()
    println("Password:")
    val password = readln()
    decryptMessage(inputImagePath, password)
}

fun hide() {
    println("Input image file:")
    val inputImagePath = readln()
    println("Output image file:")
    val outputImagePath = readln()
    println("Message to hide:")
    val messageToHide = readln()
    println("Password:")
    val password = readln()
    encryptMessage(inputImagePath, outputImagePath, messageToHide, password)
}

fun encryptMessage(inputImagePath: String, outputImagePath: String, message: String, password: String) {
    try {
        val inputFile = File(inputImagePath)
        val image: BufferedImage = ImageIO.read(inputFile)
        val bits = encode(message, password)
        if (bits.size >= image.width * image.height)
            println("The input image is not large enough to hold this message.")
        else {
            loop@ for (y in 0 until image.height) {
                for (x in 0 until image.width) {
                    val indexOf = y * image.width + x
                    if (indexOf >= bits.size) break@loop
                    val shift = bits[indexOf].toInt()
                    val color = Color(image.getRGB(x, y))
                    val blue = color.blue
                    val red = color.red
                    val green = color.green
                    image.setRGB(x, y, Color(red, green, blue.and(254).or(shift) % 256).rgb)
                }
            }
            val outputFile = File(outputImagePath)
            ImageIO.write(image, "png", outputFile)
            println("Message saved in $outputImagePath image.")
        }
    }
    catch (e: IIOException) {
        println("Can't read input file!")
    }
}

fun messageToBinaryList(message: String): List<Int> {
    return message
        .encodeToByteArray()
        .joinToString("") { it.toInt().toString(2).padStart(8, '0') }
        .chunked(1)
        .map { it.toInt() }
}

fun encode(message: String, password: String): List<Int> {
    val binaryMessage = messageToBinaryList(message)
    val binaryPassword = messageToBinaryList(password)
    val encoded = mutableListOf<Int>()
    for (i in binaryMessage.indices) {
        encoded += binaryMessage[i] xor binaryPassword[i % binaryPassword.size]
    }
    encoded.addAll(listOf(0,0,0,0,0,0,0,0,
                          0,0,0,0,0,0,0,0,
                          0,0,0,0,0,0,1,1))
    return encoded
}

fun decode(bits: List<String>, password: String): List<Int> {
    val binaryPassword = messageToBinaryList(password)
    val decoded = mutableListOf<Int>()
    for (i in bits.indices) {
        decoded += bits[i].toInt() xor binaryPassword[i % binaryPassword.size]
    }
    return decoded
}

fun decryptMessage(inputImagePath: String, password: String) {
    val inputFile = File(inputImagePath)
    val image: BufferedImage = ImageIO.read(inputFile)
    var bitsList = mutableListOf<Int>()
    for (y in 0 until image.height) {
        for (x in 0 until image.width) {
            val color = Color(image.getRGB(x, y))
            val blue = color.blue
            val bit = blue % 2
            bitsList += bit
        }
    }
    val binaryList = bitsList.chunked(8).map { it.joinToString("")}
    var separator = 0
    for (i in binaryList.indices) {
        if (binaryList[i] == "00000000" &&
            binaryList[i + 1] == "00000000" &&
            binaryList[i + 2] == "00000011") {
            break
        }
        else separator++
    }
    val bits = decode(binaryList
        .map { it.chunked(1) }
        .flatten()
        .joinToString("")
        .chunked(1)
        , password)
    val bytes = bits.chunked(8).map { it.joinToString("").toInt(2).toChar()}
    println("Message:")
    val ans = bytes.joinToString("").take(separator)
    println(ans)
}