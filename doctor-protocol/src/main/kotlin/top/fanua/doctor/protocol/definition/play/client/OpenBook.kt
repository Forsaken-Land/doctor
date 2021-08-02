package top.fanua.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.extension.readVarInt

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
