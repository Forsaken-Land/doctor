package top.limbang.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder

/**
 * @author Doctor_Yin
 * @date 2021/5/1
 * @time 13:49
 */
data class OpenBookPacket(
    val hand: Int
) : Packet

class OpenBookDecoder : PacketDecoder<OpenBookPacket> {
    override fun decoder(buf: ByteBuf): OpenBookPacket {
        val hand = buf.readVarInt()
        return OpenBookPacket(hand = hand)
    }
}
