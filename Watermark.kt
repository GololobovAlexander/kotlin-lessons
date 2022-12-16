package watermark

import java.awt.Color
import java.awt.Transparency
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.system.exitProcess

enum class BlendType {
    DEFAULT,
    ALPHA,
    COLOR
}

enum class PositionMethod {
    SINGLE,
    GRID
}

fun checkImage(imagePath: String, type: String) {
    val file = File(imagePath)
    when {
        !file.exists() -> {
            println("The file $imagePath doesn't exist.")
            exitProcess(0)
        }
        ImageIO.read(file).colorModel.numColorComponents != 3 -> {
            println("The number of $type color components isn't 3.")
            exitProcess(0)
        }
        ImageIO.read(file).colorModel.pixelSize != 24 &&
                ImageIO.read(file).colorModel.pixelSize != 32 -> {
            println("The $type isn't 24 or 32-bit.")
            exitProcess(0)
        }
    }
}

fun compareDimensions(image: BufferedImage, watermark: BufferedImage) {
    if (image.width < watermark.width || image.height < watermark.height) {
        println("The watermark's dimensions are larger.")
        exitProcess(0)
    }
}

fun getTransparency(): Int {
    println("Input the watermark transparency percentage (Integer 0-100):")
    val transparency = readln()
    when {
        !transparency.all { it.isDigit() } -> {
            println("The transparency percentage isn't an integer number.")
            exitProcess(0)
        }
        transparency.toInt() !in 0..100 -> {
            println("The transparency percentage is out of range.")
            exitProcess(0)
        }
    }
    return transparency.toInt()
}

fun getOutputFileName(): String {
    val outputName = readln()
    if (!outputName.endsWith(".jpg") && !outputName.endsWith(".png")) {
        println("The output file extension isn't \"jpg\" or \"png\".")
        exitProcess(0)
    }
    return outputName
}

fun hasAlpha(watermark: BufferedImage): Boolean {
    return watermark.transparency == Transparency.TRANSLUCENT
}

fun useAlpha(): Boolean {
    println("Do you want to use the watermark's Alpha channel?")
    return readln() == "yes"
}

fun useTransparencyColor(): Boolean {
    println("Do you want to set a transparency color?")
    return readln() == "yes"
}

fun getBlendType(hasAlpha: Boolean): BlendType {
    return if (hasAlpha) {
        val useAlpha = useAlpha()
        if (useAlpha) BlendType.ALPHA
        else BlendType.DEFAULT
    }
    else {
        val useTransparencyColor = useTransparencyColor()
        if (useTransparencyColor) BlendType.COLOR
        else BlendType.DEFAULT
    }
}

fun getPositionMethod(): PositionMethod {
    println("Choose the position method (single, grid):")
    return when (readln()) {
        "single" -> PositionMethod.SINGLE
        "grid" -> PositionMethod.GRID
        else -> {
            println("The position method input is invalid.")
            exitProcess(0)
        }
    }
}

fun getStartingPoint(image: BufferedImage, watermark: BufferedImage): Pair<Int, Int> {
    val xDiff = image.width - watermark.width
    val yDiff = image.height - watermark.height
    val regex = Regex("-?\\d+ -?\\d+")
    println("Input the watermark position ([x 0-${xDiff}] [y 0-${yDiff}]):")
    val answer = readln()
    if (!answer.matches(regex)) {
        println("The position input is invalid.")
        exitProcess(0)
    }
    val nums = answer.split(" ").map { it.toInt() }
    if (nums[0] < 0 || nums[1] < 0 || nums[0] > xDiff || nums[1] > yDiff) {
        println("The position input is out of range.")
        exitProcess(0)
    }
    return nums[0] to nums[1]
}

fun getTransparencyColor(): Color {
    println("Input a transparency color ([Red] [Green] [Blue]):")
    val input = readln().trim().split(" ")
    if (input.size == 3 && input.all{ it.all { x -> x.isDigit() }} && input.map { it.toInt() }.all { it in 0..255 })
        return Color(input[0].toInt(), input[1].toInt(), input[2].toInt())
    println("The transparency color input is invalid.")
    exitProcess(0)
}

fun blendImages(image: BufferedImage,
                watermark: BufferedImage,
                type: BlendType): BufferedImage {
    val transparencyColor = if (type == BlendType.COLOR) getTransparencyColor() else null
    val transparency = getTransparency()
    val positionMethod = getPositionMethod()
    val start = if (positionMethod == PositionMethod.SINGLE) getStartingPoint(image, watermark) else 0 to 0
    for (y in start.second until image.height) {
        if (y >= watermark.height + start.second && positionMethod == PositionMethod.SINGLE) break
        for (x in start.first until image.width) {
            if (x >= watermark.width + start.first && positionMethod == PositionMethod.SINGLE) break
            val imageColor = Color(image.getRGB(x, y), true)
            val watermarkColor =
                if (positionMethod == PositionMethod.GRID) Color(watermark.getRGB(x % watermark.width, y % watermark.height), true)
                else Color(watermark.getRGB(x - start.first, y - start.second), true)
            val newColor = when (type) {
                BlendType.DEFAULT -> weightedColor(imageColor, watermarkColor, transparency)
                BlendType.ALPHA -> if (watermarkColor.alpha == 255) weightedColor(imageColor, watermarkColor, transparency)
                                   else imageColor
                BlendType.COLOR -> if (watermarkColor == transparencyColor) imageColor
                                   else weightedColor(imageColor, watermarkColor, transparency)
            }
            image.setRGB(x, y, newColor.rgb)
        }
    }
    return image
}

fun weightedColor(imageColor: Color, watermarkColor: Color, transparency: Int): Color {
    return Color(
        (transparency * watermarkColor.red + (100 - transparency) * imageColor.red) / 100,
        (transparency * watermarkColor.green + (100 - transparency) * imageColor.green) / 100,
        (transparency * watermarkColor.blue + (100 - transparency) * imageColor.blue) / 100,
        imageColor.alpha
    )
}

fun main() {
    println("Input the image filename:")
    val imagePath = readln()
    checkImage(imagePath, "image")
    val myImage: BufferedImage = ImageIO.read(File(imagePath))
    println("Input the watermark image filename:")
    val watermarkPath = readln()
    checkImage(watermarkPath, "watermark")
    val myWatermark: BufferedImage = ImageIO.read(File(watermarkPath))
    compareDimensions(myImage, myWatermark)
    val hasAlpha = hasAlpha(myWatermark)
    val type = getBlendType(hasAlpha)
    val newImage = blendImages(myImage, myWatermark, type)
    println("Input the output image filename (jpg or png extension):")
    val outputName = getOutputFileName()
    val format = if (outputName.endsWith(".png")) "png" else "jpg"
    ImageIO.write(newImage, format, File(outputName))
    println("The watermarked image $outputName has been created.")
}
