package top.limbang.doctor.protocol.definition.play.client

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder

/**
 * @author Doctor_Yin
 * @date 2021/5/1
 * @time 22:57
 */
@Serializable
data class CKeepAlivePacket(
    val keepAliveId: Long
) : Packet

@Serializable
data class SKeepAlivePacket(
    val keepAliveId: Long
) : Packet

class KeepAliveDecoder : PacketDecoder<SKeepAlivePacket> {
    override fun decoder(buf: ByteBuf): SKeepAlivePacket {
        return SKeepAlivePacket(keepAliveId = buf.readLong())
    }

}

class KeepAliveEncoder : PacketEncoder<CKeepAlivePacket> {
    override fun encode(buf: ByteBuf, packet: CKeepAlivePacket): ByteBuf {
        buf.writeLong(packet.keepAliveId)
        return buf
    }
}