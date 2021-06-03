package top.limbang.doctor.plugin.forge.api

import top.limbang.doctor.plugin.forge.registry.IChannelPacketRegistry

/**
 *
 * @author limbang
 * @since 2021-05-14
 */
interface ForgeProtocol : IChannelPacketRegistry {

}

enum class ForgeProtocolState {
    HELLO,
    MODLIST,
    HANDSHAKE,
    REGISTERDATA,
    PLAY
}