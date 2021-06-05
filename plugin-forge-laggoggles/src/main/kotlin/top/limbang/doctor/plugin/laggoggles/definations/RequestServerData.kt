package top.limbang.doctor.plugin.laggoggles.definations

import io.netty.buffer.ByteBuf
import top.limbang.doctor.plugin.laggoggles.api.LagPacket
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/5:21:31
 */
class RequestServerDataPacket : LagPacket

class RequestServerDataDecoder : PacketDecoder<RequestServerDataPacket> {
    override fun decoder(buf: ByteBuf): RequestServerDataPacket {
        return RequestServerDataPacket()
    }
}

class RequestServerDataEncoder : PacketEncoder<RequestServerDataPacket> {
    override fun encode(buf: ByteBuf, packet: RequestServerDataPacket): ByteBuf {
        return buf
    }
}
