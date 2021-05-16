package top.limbang.doctor.protocol.version.autoversion

import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.definition.status.client.PingEncoder
import top.limbang.doctor.protocol.definition.status.client.RequestEncoder
import top.limbang.doctor.protocol.definition.status.server.PongDecoder
import top.limbang.doctor.protocol.definition.status.server.ResponseDecoder
import top.limbang.doctor.protocol.registry.IPacketRegistry
import top.limbang.doctor.protocol.registry.PacketRegistryImpl

/**
 *
 * @author WarmthDawn
 * @since 2021-05-16
 */
class PingProtocol : IPacketRegistry by PacketRegistryImpl() {
    init {
        packetMap(ProtocolState.STATUS) {
            whenS2C {
                register(0x00, ResponseDecoder())
                register(0x01, PongDecoder())
            }
            whenC2S {
                register(0x00, RequestEncoder())
                register(0x01, PingEncoder())
            }
        }
    }
}