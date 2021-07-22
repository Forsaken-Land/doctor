package top.limbang.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.protocol.extension.writeVarInt

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/22:19:07
 */
@Serializable
data class EntityActionPacket(
    val entityIds: Int,
    val actionId: Int,
    val jumpBoost: Int
) : Packet {
//    @Serializable
//    enum class Action(private val id: Int){
//
//    }

}

class EntityActionEncoder : PacketEncoder<EntityActionPacket> {
    override fun encode(buf: ByteBuf, packet: EntityActionPacket): ByteBuf {
        buf.writeVarInt(packet.entityIds)
        buf.writeVarInt(packet.actionId)
        buf.writeVarInt(packet.jumpBoost)
        return buf
    }
}
