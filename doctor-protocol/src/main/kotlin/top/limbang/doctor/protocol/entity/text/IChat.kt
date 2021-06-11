package top.limbang.doctor.protocol.entity.text

import top.limbang.doctor.protocol.entity.text.style.Style

/**
 *
 * @author WarmthDawn
 * @since 2021-05-11
 */
interface IChat : Iterable<IChat> {
    var style: Style
    fun appendSibling(sibling: IChat): IChat
    fun appendText(text: String): IChat
    fun getUnformattedComponentText(): String
    fun getUnformattedText(): String
    fun getFormattedText(): String
    fun getSiblings(): List<IChat>
    fun copy(): IChat
}