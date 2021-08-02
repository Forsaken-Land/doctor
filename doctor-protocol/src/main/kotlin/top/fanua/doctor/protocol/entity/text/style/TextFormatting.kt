package top.fanua.doctor.protocol.entity.text.style

import org.slf4j.LoggerFactory
import java.util.*

/**
 *
 * @author WarmthDawn
 * @since 2021-05-11
 */

enum class Type {
    MOTD,
    ANSI
}

enum class TextFormatting(
    val motdCode: Char,
    val ansiCode: String,
    val color: Int,
    val colorCode: Int?,
    val fancy: Boolean
) {

    BLACK('0', "30m", 0, 0),
    DARK_BLUE('1', "34m", 1, 170),
    DARK_GREEN('2', "32m", 2, 43520),
    DARK_AQUA('3', "36m", 3, 43690),
    DARK_RED('4', "31m", 4, 11141120),
    DARK_PURPLE('5', "35m", 5, 11141290),
    GOLD('6', "33m", 6, 16755200),
    GRAY('7', "37m", 7, 11184810),
    DARK_GRAY('8', "90m", 8, 5592405),
    BLUE('9', "94m", 9, 5592575),
    GREEN('a', "92m", 10, 5635925),
    AQUA('b', "96m", 11, 5636095),
    RED('c', "91m", 12, 16733525),
    LIGHT_PURPLE('d', "95m", 13, 16733695),
    YELLOW('e', "93m", 14, 16777045),
    WHITE('f', "97m", 15, 16777215),
    OBFUSCATED('k', "5m", true),
    BOLD('l', "1m", true),
    STRIKETHROUGH('m', "9m", true),
    UNDERLINE('n', "4m", true),
    ITALIC('o', "3m", true),
    RESET('r', "0m", -1, null, false),

    EMPTY('u', "", -1, null, false);


    constructor(
        motdCode: Char,
        ansiCode: String,
        color: Int,
        colorCode: Int
    ) : this(motdCode, ansiCode, color, colorCode, false)

    constructor(
        code: Char,
        ansiCode: String,
        fancy: Boolean
    ) : this(code, ansiCode, -1, null, fancy)

    override fun toString(): String {
        return if (formatType == Type.MOTD) MOTD_PREFIX + motdCode else ANSI_PREFIX + ansiCode
    }

    companion object {
        const val MOTD_PREFIX = "\u00a7"
        const val ANSI_PREFIX = "\u001b["
        val MOTD_REGEX = Regex("\u00a7[0-9a-flonmkr]")
        val logger = LoggerFactory.getLogger(this.javaClass)
        var formatType = Type.ANSI
        val formatMap = values().associateBy { it.name.lowercase(Locale.getDefault()) }
        val colorCodeMap = values().filter { it.colorCode != null }.associateBy { it.colorCode!! }
        val codeMap = values().associate { MOTD_PREFIX + it.motdCode to ANSI_PREFIX + it.ansiCode }
        fun getFormat(name: String): TextFormatting {
            val intColor = hexToInt(name)
            return if (intColor != null) {
                colorCodeMap.getOrDefault(intColor, EMPTY)
            } else {
                formatMap.getOrDefault(name, EMPTY)
            }.also {
                if (it == EMPTY) {
                    logger.warn("无法找到 $name 对应的格式")
                }
            }
        }

        private fun hexToInt(hexString: String): Int? {
            return if (hexString.startsWith("#")) {
                try {
                    hexString.substring(1).toInt(16)
                } catch (e: NumberFormatException) {
                    null
                }
            } else {
                null
            }
        }
    }
}
