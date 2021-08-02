package top.fanua.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder

/**
 * @author Doctor_Yin
 * @date 2021/5/1
 * @time 14:14
 */
@Serializable
data class EntityStatusPacket(
    val entityID: Int,
    val entityStatus: Byte
) : Packet

class EntityStatusDecoder : PacketDecoder<EntityStatusPacket> {
    override fun decoder(buf: ByteBuf): EntityStatusPacket {
        val entityID = buf.readInt()
        val entityStatusPacket = buf.readByte()
        return EntityStatusPacket(
            entityID = entityID,
            entityStatus = entityStatusPacket
        )
    }

}
