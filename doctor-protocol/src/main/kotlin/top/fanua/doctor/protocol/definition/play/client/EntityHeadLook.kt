package top.fanua.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.extension.readVarInt

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
