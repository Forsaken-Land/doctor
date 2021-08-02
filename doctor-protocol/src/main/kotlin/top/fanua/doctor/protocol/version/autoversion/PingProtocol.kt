package top.fanua.doctor.protocol.version.autoversion

import top.fanua.doctor.protocol.api.ProtocolState
import top.fanua.doctor.protocol.core.PacketDirection
import top.fanua.doctor.protocol.definition.client.HandshakeEncoder
import top.fanua.doctor.protocol.definition.status.client.PingEncoder
import top.fanua.doctor.protocol.definition.status.client.RequestEncoder
import top.fanua.doctor.protocol.definition.status.server.PongDecoder
import top.fanua.doctor.protocol.definition.status.server.ResponseDecoder
import top.fanua.doctor.protocol.registry.IPacketRegistry
import top.fanua.doctor.protocol.registry.PacketRegistryImpl

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

        packetMap(PacketDirection.C2S, ProtocolState.HANDSHAKE) {
            register(0x00, HandshakeEncoder())
        }
    }
}
