package com.xbaimiao.easylib.module.chat

import com.xbaimiao.easylib.module.utils.warn
import net.md_5.bungee.api.ChatColor
import kotlin.math.ceil

val Int.red: Int
    get() = (this shr 16) and 0xFF

val Int.green: Int
    get() = (this shr 8) and 0xFF

val Int.blue: Int
    get() = this and 0xFF

fun Int.mix(next: Int, d: Double): Int {
    val r = (this.red * (1 - d) + next.red * d).toInt()
    val g = (this.green * (1 - d) + next.green * d).toInt()
    val b = (this.blue * (1 - d) + next.blue * d).toInt()
    return (r shl 16) or (g shl 8) or b
}

/**
 * 对字符串上色
 */
fun String.colored() = HexColor.translate(this)

/**
 * 对字符串去色
 */
fun String.uncolored() = ChatColor.stripColor(this.colored())!!

/**
 * 对列表上色
 */
fun List<String>.colored() = map { it.colored() }

/**
 * 对列表去色
 */
fun List<String>.uncolored() = map { it.uncolored() }

/**
 * 获取颜色
 */
fun String.parseToHexColor(): Int {
    // HEX: #ffffff
    if (startsWith('#')) {
        return substring(1).toIntOrNull(16) ?: 0
    }
    // RGB: 255,255,255
    if (contains(',')) {
        return split(',').map { it.toIntOrNull() ?: 0 }.let { (r, g, b) -> (r shl 16) or (g shl 8) or b }
    }
    // RGB: 255-255-255
    if (contains('-')) {
        return split('-').map { it.toIntOrNull() ?: 0 }.let { (r, g, b) -> (r shl 16) or (g shl 8) or b }
    }
    // NAMED: white
    val knownColor = StandardColors.match(this)
    if (knownColor.isPresent) {
        return knownColor.get().chatColor.color.rgb
    }
    warn("Unknown color $this")
    return 0
}

/**
 * 创建渐变颜色
 */
fun String.toGradientColor(colors: List<Int>): String {
    // 每个颜色的长度
    val step = ceil(length.toDouble() / (colors.size - 1)).toInt()
    // 生成过渡颜色
    val gradientText = StringBuilder()
    forEachIndexed { index, c ->
        val current = colors[index / step]
        val next = colors[(index / step + 1).coerceAtMost(colors.size - 1)]
        val position = index % step
        val percent = position.toDouble() / step
        val color = current.mix(next, percent)
        gradientText.append("${HexColor.getColorCode(color)}$c")
    }
    return gradientText.toString()
}