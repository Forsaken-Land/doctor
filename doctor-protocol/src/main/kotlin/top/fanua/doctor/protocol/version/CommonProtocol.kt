package top.fanua.doctor.protocol.version

import top.fanua.doctor.protocol.api.ProtocolState
import top.fanua.doctor.protocol.core.PacketDirection
import top.fanua.doctor.protocol.definition.client.HandshakeEncoder
import top.fanua.doctor.protocol.definition.status.client.PingEncoder
import top.fanua.doctor.protocol.definition.status.client.RequestEncoder
import top.fanua.doctor.protocol.definition.status.server.PongDecoder
import top.fanua.doctor.protocol.definition.status.server.ResponseDecoder
import top.fanua.doctor.protocol.registry.ICommonPacketGroup
import top.fanua.doctor.protocol.registry.IPacketRegistry

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
object CommonProtocol : ICommonPacketGroup<IPacketRegistry> {
    override fun registerPackets(registry: IPacketRegistry) {
        registry.run {

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

}
