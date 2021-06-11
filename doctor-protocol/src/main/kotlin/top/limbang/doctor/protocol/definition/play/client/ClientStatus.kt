package top.limbang.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.protocol.definition.play.client.ClientStatusEnum.PerformRespawn
import top.limbang.doctor.protocol.definition.play.client.ClientStatusEnum.RequestStats
import top.limbang.doctor.protocol.extension.writeVarInt

/**
 * ### 客户端状态
 * - [PerformRespawn] 执行重生
 * - [RequestStats] 请求统计
 */
enum class ClientStatusEnum(val id: Int) {
    PerformRespawn(0),
    RequestStats(1);
}

/**
 * ### 客户端状态包
 * - [actionId] 动作id
 * @see [ClientStatusEnum]
 */
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

