package top.fanua.doctor.protocol.entity.text.style

/**
 *
 * @author WarmthDawn
 * @since 2021-05-11
 */
data class StyleData(
    var color: TextFormatting? = null,
    var bold: Boolean? = null,
    var italic: Boolean? = null,
    var underlined: Boolean? = null,
    var strikethrough: Boolean? = null,
    var obfuscated: Boolean? = null,
    var clickEvent: ClickEvent? = null,
    var hoverEvent: HoverEvent? = null,
    var insertion: String? = null
) {
    companion object {
        val EMPTY = StyleData()
    }
}

class Style {
    fun getFormattingCode(): String {
        return if (this.isEmpty) {
            this.parent?.getFormattingCode() ?: ""
        } else {
            val builder = StringBuilder()
            if (this.color != null) {
                builder.append(this.color)
            }
            if (this.bold == true) {
                builder.append(TextFormatting.BOLD)
            }
            if (this.italic == true) {
                builder.append(TextFormatting.ITALIC)
            }
            if (this.underlined == true) {
                builder.append(TextFormatting.UNDERLINE)
            }
            if (this.obfuscated == true) {
                builder.append(TextFormatting.OBFUSCATED)
            }
            if (this.strikethrough == true) {
                builder.append(TextFormatting.STRIKETHROUGH)
            }
            builder.toString()
        }
    }

    fun deepCopy(): Style {
        return Style().also {
            it.bold = bold
            it.italic = italic
            it.strikethrough = strikethrough
            it.underlined = underlined
            it.obfuscated = obfuscated
            it.color = color
            it.clickEvent = clickEvent
            it.hoverEvent = hoverEvent
            it.parent = this.parent
            it.insertion = insertion
        }
    }


    fun shallowCopy(): Style {
        return Style().also {
            it.parent = parent
            it.data = data
        }
    }

    override fun toString(): String {
        return "Style(color=${color?.name}, bold=$bold, italic=$italic, underlined=$underlined, strikethrough=$strikethrough, obfuscated=$obfuscated, clickEvent=$clickEvent, hoverEvent=$hoverEvent, insertion=$insertion)"
    }

    private var data = StyleData()
    var parent: Style? = null

    var color: TextFormatting?
        get() = data.color ?: parent?.color
        set(value) {
            data.color = value
        }
    var bold: Boolean?
        get() = data.bold ?: parent?.bold
        set(value) {
            data.bold = value
        }
    var italic: Boolean?
        get() = data.italic ?: parent?.italic
        set(value) {
            data.italic = value
        }
    var underlined: Boolean?
        get() = data.underlined ?: parent?.underlined
        set(value) {
            data.underlined = value
        }
    var strikethrough: Boolean?
        get() = data.strikethrough ?: parent?.strikethrough
        set(value) {
            data.strikethrough = value
        }
    var obfuscated: Boolean?
        get() = data.obfuscated ?: parent?.obfuscated
        set(value) {
            data.obfuscated = value
        }
    var clickEvent: ClickEvent?
        get() = data.clickEvent ?: parent?.clickEvent
        set(value) {
            data.clickEvent = value
        }
    var hoverEvent: HoverEvent?
        get() = data.hoverEvent ?: parent?.hoverEvent
        set(value) {
            data.hoverEvent = value
        }
    var insertion: String?
        get() = data.insertion ?: parent?.insertion
        set(value) {
            data.insertion = value
        }

    val isEmpty: Boolean
        get() = StyleData.EMPTY == data
}
