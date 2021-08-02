package top.limbang.doctor.plugin.exNihiloSequentia.protocol

import top.limbang.doctor.plugin.exNihiloSequentia.definations.HandshakeMessages
import top.limbang.doctor.plugin.forge.registry.ChannelAndId
import top.limbang.doctor.plugin.forge.registry.IFML2PacketRegistry
import top.limbang.doctor.protocol.core.PacketDirection
import top.limbang.doctor.protocol.registry.ICommonPacketGroup

/**
 *
 * @author Doctor_Yin
 * @since 2021/8/2:21:34
 */
object ExNihiloSequentia : ICommonPacketGroup<IFML2PacketRegistry> {
    override fun registerPackets(registry: IFML2PacketRegistry) {
        registry.fml2PacketMap(PacketDirection.S2C) {
            register(ChannelAndId("exnihilosequentia:handshake", 1), HandshakeMessages.LoginIndexedMessageDecoder())
        }
        registry.fml2PacketMap(PacketDirection.C2S) {
            register(ChannelAndId("exnihilosequentia:handshake", 99), HandshakeMessages.C2SAcknowledgeEncoder())
        }
    }

}
