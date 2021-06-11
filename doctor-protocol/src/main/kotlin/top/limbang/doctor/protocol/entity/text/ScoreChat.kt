package top.limbang.doctor.protocol.entity.text

/**
 *
 * @author WarmthDawn
 * @since 2021-05-11
 */
class ScoreChat(
    val name: String,
    val objective: String,
    var value: String? = null
) : AbstractChat() {
    override fun getUnformattedComponentText(): String = value ?: ""

    override fun copy(): IChat {
        return ScoreChat(name, objective, value).copyFrom(this)
    }
}