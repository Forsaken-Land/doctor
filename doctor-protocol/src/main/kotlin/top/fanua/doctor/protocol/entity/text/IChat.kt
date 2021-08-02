package top.fanua.doctor.protocol.entity.text

import top.fanua.doctor.protocol.entity.text.style.Style

/**
 * ### Chat 消息
 *
 * @author WarmthDawn
 * @since 2021-05-11
 */
interface IChat : Iterable<IChat> {
    var style: Style

    /**
     * ### 在元素结尾插入 [IChat]
     * @param sibling 需要插入的同级元素
     */
    fun appendSibling(sibling: IChat): IChat

    /**
     * ### 在元素结尾插入普通聊天
     * @param text 聊天消息
     */
    fun appendText(text: String): IChat

    fun getUnformattedComponentText(): String

    /**
     * ### 获取未格式化文本,不带颜色
     */
    fun getUnformattedText(): String

    /**
     * ### 获取格式化文本,带颜色输出
     */
    fun getFormattedText(): String

    /**
     * ### 获取 [IChat] 的同级元素列表
     */
    fun getSiblings(): List<IChat>
    fun copy(): IChat
}
