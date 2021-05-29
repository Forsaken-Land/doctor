package top.limbang.doctor.protocol.version

import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.core.PacketDirection
import top.limbang.doctor.protocol.definition.client.HandshakeEncoder
import top.limbang.doctor.protocol.definition.status.client.PingEncoder
import top.limbang.doctor.protocol.definition.status.client.RequestEncoder
import top.limbang.doctor.protocol.definition.status.server.PongDecoder
import top.limbang.doctor.protocol.definition.status.server.ResponseDecoder
import top.limbang.doctor.protocol.registry.ICommonPacketGroup
import top.limbang.doctor.protocol.registry.IPacketRegistry

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
