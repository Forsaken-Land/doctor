package top.limbang.doctor.plugin.laggoggles.definations

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.limbang.doctor.plugin.laggoggles.api.LagPacket
import top.limbang.doctor.protocol.api.PacketDecoder

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/5:22:17
 */
@Serializable
data class ServerDataPacket(
    val hasResult: Boolean,
    val permission: Permission,
    val maxProfileTime: Int,
    val canSeeEventSubScribes: Boolean
) : LagPacket

class ServerDataDecoder : PacketDecoder<ServerDataPacket> {
    override fun decoder(buf: ByteBuf): ServerDataPacket {
        val hasResult = buf.readBoolean()
        val permission = Permission.getValue(buf.readInt())
        val maxProfileTime = buf.readInt()
        val canSeeEventSubScribes = buf.readBoolean()
        return ServerDataPacket(hasResult, permission, maxProfileTime, canSeeEventSubScribes)
    }
}

@Serializable
enum class Permission(val id: Int) {
    NONE(0),
    GET(1),
    START(2),
    FULL(3);

    companion object {
        private val VALUES = values()

        fun getValue(value: Int) = VALUES.firstOrNull { it.id == value } ?: NONE
    }
}
