package top.limbang.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder

/**
 * @author Doctor_Yin
 * @date 2021/5/1
 * @time 13:44
 */
data class ServerDifficultyPacket(
    val difficulty: Byte
) : Packet

class ServerDifficultyDecoder : PacketDecoder<ServerDifficultyPacket> {
    override fun decoder(buf: ByteBuf): ServerDifficultyPacket {
        val difficulty = buf.readByte()
        return ServerDifficultyPacket(difficulty = difficulty)
    }

}
