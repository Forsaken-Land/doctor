package top.limbang.doctor.plugin.forge.api

import top.limbang.doctor.plugin.forge.registry.IForgePacketRegistry

/**
 *
 * @author limbang
 * @since 2021-05-14
 */
interface ForgeProtocol : IForgePacketRegistry {

    val sendId: Int
    val readId: Int

}

enum class ForgeProtocolState {
    HANDSHAKE,
    STATUS,
    LOGIN,
    PLAY
}
