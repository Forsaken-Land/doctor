package top.limbang.doctor.protocol.entity.text.style

import org.slf4j.LoggerFactory

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
    val fancy: Boolean
) {
    BLACK('0', "30m", 0),
    DARK_BLUE('1', "34m", 1),
    DARK_GREEN('2', "32m", 2),
    DARK_AQUA('3', "36m", 3),
    DARK_RED('4', "31m", 4),
    DARK_PURPLE('5', "35m", 5),
    GOLD('6', "33m", 6),
    GRAY('7', "37m", 7),
    DARK_GRAY('8', "90m", 8),
    BLUE('9', "94m", 9),
    GREEN('a', "92m", 10),
    AQUA('b', "96m", 11),
    RED('c', "91m", 12),
    LIGHT_PURPLE('d', "95m", 13),
    YELLOW('e', "93m", 14),
    WHITE('f', "97m", 15),
    OBFUSCATED('k', "5m", true),
    BOLD('l', "1m", true),
    STRIKETHROUGH('m', "9m", true),
    UNDERLINE('n', "4m", true),
    ITALIC('o', "3m", true),
    RESET('r', "0m", -1),

    EMPTY('u', "", -1, false);


    constructor(
        motdCode: Char,
        ansiCode: String,
        color: Int
    ) : this(motdCode, ansiCode, color, false)

    constructor(
        code: Char,
        ansiCode: String,
        fancy: Boolean
    ) : this(code, ansiCode, -1, fancy)

    override fun toString(): String {
        return if (formatType == Type.MOTD) MOTD_PREFIX + motdCode else ANSI_PREFIX + ansiCode
    }

    companion object {
        const val MOTD_PREFIX = "\u00a7"
        const val ANSI_PREFIX = "\u001b["
        val MOTD_REGEX = Regex("\u00a7[0-9a-flonmkr]")
        val logger = LoggerFactory.getLogger(this.javaClass)
        var formatType = Type.ANSI
        val formatMap = values().associateBy { it.name.toLowerCase() }
        val codeMap = values().associate { MOTD_PREFIX + it.motdCode to ANSI_PREFIX + it.ansiCode }
        fun getFormat(name: String): TextFormatting {
            return formatMap.getOrDefault(name, EMPTY).also {
                if (it == EMPTY) {
                    logger.warn("无法找到 $name 对应的格式")
                }
            }
        }
    }
}