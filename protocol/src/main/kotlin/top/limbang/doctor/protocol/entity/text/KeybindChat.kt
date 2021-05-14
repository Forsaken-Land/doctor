package top.limbang.doctor.protocol.entity.text

/**
 *
 * @author WarmthDawn
 * @since 2021-05-11
 */
class KeybindChat(
    val keybind: String
) : AbstractChat() {
    override fun getUnformattedComponentText(): String = keybind
    override fun copy(): IChat {
        return KeybindChat(keybind).copyFrom(this)
    }
}