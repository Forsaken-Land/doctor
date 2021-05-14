package top.limbang.doctor.protocol.entity.text

import com.google.common.collect.Iterators
import top.limbang.doctor.protocol.entity.text.style.Style
import top.limbang.doctor.protocol.entity.text.translation.I18n
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 *
 * @author WarmthDawn
 * @since 2021-05-11
 */
class TranslationChat(
    val key: String,
    private var formatArgs: Array<Any> = emptyArray()
) : AbstractChat() {
    init {
        this.formatArgs.forEach {
            when (it) {
                is IChat -> {
                    it.style.parent = this.style
                }
            }
        }
    }

    private var children = ArrayList<IChat>()
    private var isInit = false

    companion object {
        private val STRING_VARIABLE_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)")
    }


    private fun addChild(chat: IChat) {
        this.children.add(chat.also {
            it.style.parent = this.style
        })
    }

    private fun addChild(str: String) {
        addChild(PlainChat(str))
    }

    private fun initializeFromFormat(format: String) {
        val matcher: Matcher = STRING_VARIABLE_PATTERN.matcher(format)
        var i = 0
        try {
            var current = 0
            while (matcher.find(current)) {
                val matchStart = matcher.start()
                val matchEnd = matcher.end()
                if (matchStart > current) {
                    addChild(String.format(format.substring(current, matchStart)))
                }
                val formatOption = matcher.group(2)
                val s = format.substring(matchStart, matchEnd)
                if ("%" == formatOption && "%%" == s) {
                    addChild("%")
                } else {
                    if ("s" != formatOption) {
                        throw TranslationChatFormatError(this, "Unsupported format: '$s'")
                    }
                    val argsIndex = matcher.group(1)
                    val index = if (argsIndex != null) argsIndex.toInt() - 1 else i++
                    if (index < formatArgs.size) {
                        addChild(this.getFormatArgumentAsComponent(index))
                    }
                }
                current = matchEnd
            }
            if (current < format.length) {
                addChild(String.format(format.substring(current)))
            }
        } catch (e: IllegalFormatException) {
            throw TranslationChatFormatError(this, e)
        }
    }

    private fun getFormatArgumentAsComponent(index: Int): IChat {
        if (index >= formatArgs.size) {
            throw TranslationChatFormatError(this, index)
        } else {
            val obj = formatArgs[index]
            return if (obj is IChat) {
                obj
            } else {
                PlainChat(obj.toString())
            }
        }
    }

    private fun tryInit() {
        if (isInit) {
            return
        }
        isInit = true
        try {
            initializeFromFormat(I18n.translate(key))
        } catch (e: TranslationChatFormatError) {
            e.printStackTrace()
            initializeFromFormat(key)
        }
    }

    override fun getUnformattedComponentText(): String = children.joinToString("") {
        tryInit()
        it.getUnformattedComponentText()
    }

    override var style: Style
        get() = super.style
        set(value) {
            super.style = value
            formatArgs.forEach {
                when (it) {
                    is IChat -> it.style.parent = value
                }
            }
            children.forEach {
                it.style.parent = value
            }
        }

    override fun iterator(): Iterator<IChat> {
        tryInit()
        return Iterators.concat(createDeepCopyIterator(this.children), createDeepCopyIterator(this.siblings))
    }


    override fun copy(): IChat {
        val argsCopy = formatArgs.map {
            when (it) {
                is IChat ->
                    it.copy()
                else ->
                    it
            }
        }.toTypedArray()

        return TranslationChat(key, argsCopy).copyFrom(this)
    }
}