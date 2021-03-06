package top.fanua.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.core.annotation.VersionExpandPacket

/**
 * @author Doctor_Yin
 * @date 2021/5/1
 * @time 13:44
 */
interface ServerDifficultyPacket : Packet

@Serializable
@VersionExpandPacket(ServerDifficultyPacket::class)
data class ServerDifficultyType1Packet(
    val difficulty: ServerDifficulty,
    val difficultyIsLocked: Boolean
) : ServerDifficultyPacket

@Serializable
@VersionExpandPacket(ServerDifficultyPacket::class)
data class ServerDifficultyType0Packet(
    val difficulty: ServerDifficulty
) : ServerDifficultyPacket


class ServerDifficultyType1Decoder : PacketDecoder<ServerDifficultyType1Packet> {
    override fun decoder(buf: ByteBuf): ServerDifficultyType1Packet {
        val difficulty = ServerDifficulty.getValues(buf.readUnsignedByte().toInt())!!
        return ServerDifficultyType1Packet(difficulty, buf.readBoolean())
    }

}

class ServerDifficultyType0Decoder : PacketDecoder<ServerDifficultyType0Packet> {
    override fun decoder(buf: ByteBuf): ServerDifficultyType0Packet {
        val difficulty = ServerDifficulty.getValues(buf.readUnsignedByte().toInt())!!
        return ServerDifficultyType0Packet(difficulty)
    }

}

enum class ServerDifficulty(val id: Int) {
    PEACEFUL(0),
    EASY(1),
    NORMAL(2),
    HARD(3);

    companion object {
        private val VALUES = values()
        fun getValues(value: Int) = VALUES.firstOrNull { it.id == value }
    }

}
