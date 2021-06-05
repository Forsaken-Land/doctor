package top.limbang.doctor.plugin.forge.api

import top.limbang.doctor.plugin.forge.registry.IFML1PacketRegistry

/**
 *
 * @author limbang
 * @since 2021-05-14
 */
interface ForgeProtocol : IFML1PacketRegistry

enum class ForgeProtocolState {
    HELLO,
    MODLIST,
    HANDSHAKE,
    REGISTERDATA,
    PLAY
}
