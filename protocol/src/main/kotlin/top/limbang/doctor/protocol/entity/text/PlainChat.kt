package top.limbang.doctor.protocol.entity.text

/**
 *
 * @author WarmthDawn
 * @since 2021-05-11
 */
class PlainChat(
    val text: String
): AbstractChat() {
    override fun getUnformattedComponentText(): String  = text;

    override fun copy(): IChat {
        return PlainChat(text).copyFrom(this)
    }
}