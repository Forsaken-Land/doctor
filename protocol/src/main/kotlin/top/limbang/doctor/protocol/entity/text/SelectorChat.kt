package top.limbang.doctor.protocol.entity.text

/**
 *
 * @author WarmthDawn
 * @since 2021-05-11
 */
class SelectorChat(
    val selector: String
) : AbstractChat() {
    override fun getUnformattedComponentText(): String = selector

    override fun copy(): IChat {
        return SelectorChat(selector).copyFrom(this)
    }
}