package top.limbang.doctor.protocol.definition.play.client

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder

/**
 * @author Doctor_Yin
 * @date 2021/5/1
 * @time 14:52
 */
@Serializable
data class EntityVelocityPacket(
    val entityId: Int,
    val velocityX: Short,
    val velocityY: Short,
    val velocityZ: Short
) : Packet {
    constructor(buf: ByteBuf) : this(
        buf.readVarInt(),
        buf.readShort(),
        buf.readShort(),
        buf.readShort()
    )
}

class EntityVelocityDecoder : PacketDecoder<EntityVelocityPacket> {
    override fun decoder(buf: ByteBuf): EntityVelocityPacket {
        return EntityVelocityPacket(buf)
    }

}