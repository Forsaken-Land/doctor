package top.limbang.doctor.protocol.definition.play.client

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketEncoder

enum class ClientStatusEnum(val id: Int) {
    PerformRespawn(0),
    RequestStats(1);
}

@Serializable
data class ClientStatusPacket(
    val actionId: ClientStatusEnum
) : Packet

class ClientStatusEncoder : PacketEncoder<ClientStatusPacket> {
    override fun encode(buf: ByteBuf, packet: ClientStatusPacket): ByteBuf {
        buf.writeVarInt(packet.actionId.id)
        return buf
    }
}

