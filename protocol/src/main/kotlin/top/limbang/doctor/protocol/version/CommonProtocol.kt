package top.limbang.doctor.protocol.version

import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.definition.client.*
import top.limbang.doctor.protocol.definition.login.client.*
import top.limbang.doctor.protocol.definition.login.server.*
import top.limbang.doctor.protocol.definition.status.client.*
import top.limbang.doctor.protocol.definition.status.server.*
import top.limbang.doctor.protocol.registry.ICommonPacketGroup
import top.limbang.doctor.protocol.registry.IPacketRegistry
import top.limbang.doctor.protocol.core.PacketDirection

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
object CommonProtocol : ICommonPacketGroup {
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

            packetMap(ProtocolState.LOGIN) {
                whenS2C {
                    register(0x00, DisconnectDecoder())
                    register(0x01, EncryptionRequestDecoder())
                    register(0x02, LoginSuccessDecoder())
                    register(0x03, SetCompressionDecoder())
                    register(0x04, LoginPluginRequestDecoder())
                }
                whenC2S {
                    register(0x00, LoginStartEncoder())
                    register(0x01, EncryptionResponseEncoder())
                    register(0x02, LoginPluginResponseEncoder())
                }
            }

            packetMap(PacketDirection.S2C, ProtocolState.HANDSHAKE) {
                register(0x00, HandshakeEncoder())
            }


        }
    }

}