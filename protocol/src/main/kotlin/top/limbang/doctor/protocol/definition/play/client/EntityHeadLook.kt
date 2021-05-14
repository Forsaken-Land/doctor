package top.limbang.doctor.protocol.definition.play.client

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder

/**
 * @author Doctor_Yin
 * @date 2021/5/1
 * @time 13:52
 */
@Serializable
data class EntityHeadLookPacket(
    val entityId: Int,
    val headYaw: Int
) : Packet

class EntityHeadLookDecoder : PacketDecoder<EntityHeadLookPacket> {
    override fun decoder(buf: ByteBuf): EntityHeadLookPacket {
        val entityId = buf.readVarInt()
        //TODO 需要添加角度
        val headYaw = buf.readableBytes()
        return EntityHeadLookPacket(
            entityId = entityId,
            headYaw = headYaw
        )
    }
}
