package top.limbang.doctor.plugin.exNihiloSequentia.definations

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.limbang.doctor.plugin.forge.api.FML2Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder

/**
 *
 * @author Doctor_Yin
 * @since 2021/8/2:21:30
 */
class HandshakeMessages {
    @Serializable
    data class LoginIndexedMessagePacket(
        override var messageId: Int = 0
    ) : FML2Packet

    @Serializable
    data class C2SAcknowledgePacket(
        override var messageId: Int = 0
    ) : FML2Packet

    class LoginIndexedMessageDecoder : PacketDecoder<LoginIndexedMessagePacket> {
        override fun decoder(buf: ByteBuf): LoginIndexedMessagePacket {
            return LoginIndexedMessagePacket()
        }
    }

    class C2SAcknowledgeEncoder : PacketEncoder<C2SAcknowledgePacket> {
        override fun encode(buf: ByteBuf, packet: C2SAcknowledgePacket): ByteBuf {
            return buf
        }
    }
}
