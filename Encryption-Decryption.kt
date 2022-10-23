package encryptdecrypt
import java.io.File
import java.lang.Exception
import java.lang.IndexOutOfBoundsException

fun unicode(str: String, key: Int, mode: String):String {
    var result = ""
    if (mode == "enc") {
        for (i in str) {
            result += (i.code + key).toChar()
        }
    }
    else {
        for (i in str) {
            result += (i.code - key).toChar()
        }
    }
    return result
}

fun shift(str: String, key: Int, mode: String): String {
    val alphabetLower = "abcdefghijklmnopqrstuvwxyz"
    val alphabetUpper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    fun applyKey(mode:String, value: Int, key: Int): Int {
        return if (mode == "enc") value + key
        else value - key + alphabetLower.length
    }
    var result = ""
    for (char in str) {
        result += when (char) {
            in 'a'..'z' -> alphabetLower[applyKey(mode, alphabetLower.indexOf(char), key) % alphabetLower.length]
            in 'A'..'Z' -> alphabetUpper[applyKey(mode, alphabetUpper.indexOf(char), key) % alphabetLower.length]
            else -> char
        }
    }
    return result
}

fun parseArguments(args: Array<String>): Array<String> {
    val values = arrayOf("enc", "0", "", "", "", "shift")
    for (i in args.indices step 2) {
        when (args[i]) {
            "-mode" -> values[0] = args[i + 1]
            "-key" -> values[1] = args[i + 1]
            "-data" -> values[2] = args[i + 1]
            "-out" -> values[3] = args[i + 1]
            "-in" -> values[4] = args[i + 1]
            "-alg" -> values[5] = args[i + 1]
        }
    }
    return values
}

private operator fun <T> Array<T>.component6() = this[5]

fun main(args: Array<String>) {
    try {
        val arguments = parseArguments(args)
        val (mode, key, data, out, input, alg) = arguments
        val func = if (alg == "shift") ::shift else ::unicode
        val dataToDecrypt = when {
            data == "" && input != "" -> File(input).readText()
            else -> data
        }
        var result = ""
        when (mode) {
            "enc" -> result = func(dataToDecrypt, key.toInt(), mode)
            "dec" -> result = func(dataToDecrypt, key.toInt(), mode)
        }
        if (out == "") {
            println(result)
        } else {
            File(out).writeText(result)
        }
    }
    catch (e: IndexOutOfBoundsException) {
        println("Error: index out of bounds")
    }
    catch (e: Exception) {
        println("Other error")
    }
}


