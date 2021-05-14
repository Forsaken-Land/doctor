package top.limbang.doctor.protocol.entity.text

import com.google.common.collect.Iterators
import top.limbang.doctor.protocol.entity.text.style.Style
import top.limbang.doctor.protocol.entity.text.style.TextFormatting
import top.limbang.doctor.protocol.entity.text.style.Type

/**
 *
 * @author WarmthDawn
 * @since 2021-05-11
 */
abstract class AbstractChat : IChat {
    protected val siblings = ArrayList<IChat>()
    override var style: Style = Style()
        set(value) {
            field = value
            this.siblings.forEach {
                it.style.parent = value
            }
        }

    override fun appendSibling(sibling: IChat): IChat {
        siblings.add(sibling.also {
            sibling.style.parent = this.style
        })
        return this
    }

    override fun appendText(text: String): IChat {
        siblings.add(PlainChat(text))
        return this
    }

    override fun getUnformattedText(): String = joinToString("") {
        it.getUnformattedComponentText()
    }.let {
        //防止某些硬编码
        if (it.contains(TextFormatting.MOTD_PREFIX)) {
            it.replace(TextFormatting.MOTD_REGEX, "")
        } else {
            it
        }
    }

    override fun getFormattedText(): String =
        this.flatMap {
            val s = it.getUnformattedComponentText()
            if (s.isEmpty()) {
                emptyList()
            } else {
                listOf(
                    it.style.getFormattingCode(),
                    it.getUnformattedComponentText(),
                    TextFormatting.RESET.toString()
                )
            }
        }.joinToString("").let {
            //防止某些硬编码
            if (TextFormatting.formatType == Type.ANSI && it.contains(TextFormatting.MOTD_PREFIX)) {
                val result = StringBuilder(it)
                it.replace(TextFormatting.MOTD_REGEX) { r ->
                    TextFormatting.codeMap[r.value] ?: r.value
                }
            } else {
                it
            }
        }


    open fun createDeepCopyIterator(components: Iterable<IChat>): Iterator<IChat> {
        return Iterators
            .concat(Iterators.transform(components.iterator()) { it!!.iterator() })
            .let {
                Iterators.transform(it) { chat ->
                    chat!!.copy().apply {
                        style = style.deepCopy()
                    }
                }
            }
    }

    override fun iterator(): Iterator<IChat> =
        Iterators.concat(Iterators.forArray(this), createDeepCopyIterator(this.siblings))

    override fun getSiblings(): List<IChat> = this.siblings

    protected fun copyFrom(chat: AbstractChat): AbstractChat {
        this.style = chat.style.shallowCopy()
        chat.getSiblings().forEach {
            this.appendSibling(it)
        }
        return this
    }
}