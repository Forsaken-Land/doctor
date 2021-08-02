package top.fanua.doctor.plugin.laggoggles.definations

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.plugin.laggoggles.api.LagPacket
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.api.PacketEncoder

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/5:13:08
 */
@Serializable
data class RequestScanPacket(
    val length: Int = 5
) : LagPacket

class RequestScanEncoder : PacketEncoder<RequestScanPacket> {
    override fun encode(buf: ByteBuf, packet: RequestScanPacket): ByteBuf {
        buf.writeInt(packet.length)
        return buf
    }
}

class RequestScanDecoder : PacketDecoder<RequestScanPacket> {
    override fun decoder(buf: ByteBuf): RequestScanPacket {
        val length = buf.readInt()
        return RequestScanPacket(length)
    }
}
