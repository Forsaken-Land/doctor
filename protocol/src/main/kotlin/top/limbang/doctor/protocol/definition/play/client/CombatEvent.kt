package top.limbang.doctor.protocol.definition.play.client

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder

@Serializable
data class CombatEventPacket(
    val event: Int = 0,
    val duration: Int? = null,
    val entityID: Int? = null,
    val playerID: Int? = null,
    val message: String? = null
) : Packet

class CombatEventDecoder : PacketDecoder<CombatEventPacket> {
    override fun decoder(buf: ByteBuf): CombatEventPacket {
        return when (buf.readVarInt()) {
            0 -> CombatEventPacket()
            1 -> CombatEventPacket(event = 1, duration = buf.readVarInt(), entityID = buf.readInt())
            2 -> CombatEventPacket(
                event = 2,
                playerID = buf.readVarInt(),
                entityID = buf.readInt(),
                message = buf.readString()
            )
            else -> CombatEventPacket()
        }
    }
}