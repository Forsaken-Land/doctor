package top.fanua.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.extension.readVarInt

/**
 *
 * @author Doctor_Yin
 * @since 2021/12/9 9:32
 */
@Serializable
data class UpdateHealthPacket(
    val health: Float,
    val food: Int,
    val foodSaturation: Float
) : Packet

class UpdateHealthDecoder : PacketDecoder<UpdateHealthPacket> {
    override fun decoder(buf: ByteBuf): UpdateHealthPacket {
        val health = buf.readFloat()
        val food = buf.readVarInt()
        val foodSaturation = buf.readFloat()
        return UpdateHealthPacket(health, food, foodSaturation)
    }
}