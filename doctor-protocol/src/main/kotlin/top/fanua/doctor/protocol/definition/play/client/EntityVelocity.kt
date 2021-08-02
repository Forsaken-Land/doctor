package top.fanua.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.extension.readVarInt

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
