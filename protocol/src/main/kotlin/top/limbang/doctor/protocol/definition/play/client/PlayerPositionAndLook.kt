package top.limbang.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.extension.readVarInt
import java.util.*

/**
 * @author Doctor_Yin
 * @date 2021/5/1
 * @time 21:15
 */

enum class Flags(val id: Int) {
    X(0),
    Y(1),
    Z(2),
    Y_ROT(3),
    X_ROT(4);

    companion object {


        fun unpack(flags: Int): Set<Flags>? {
            val set = EnumSet.noneOf(Flags::class.java)
            for (flag in values()) {
                if (flag.isSet(flags)) {
                    set.add(flag)
                }
            }
            return set
        }
    }

    private fun isSet(flags: Int): Boolean {
        return flags and this.getMask() == this.getMask()
    }

    private fun getMask(): Int {
        return 1 shl id
    }
}

@Serializable
data class PlayerPositionAndLookPacket(
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float,
    val flags: Set<Flags>?,//TODO 不确定
    val teleportId: Int
) : Packet {
    constructor(buf: ByteBuf) : this(
        buf.readDouble(),
        buf.readDouble(),
        buf.readDouble(),
        buf.readFloat(),
        buf.readFloat(),
        Flags.unpack(buf.readUnsignedByte().toInt()),
        buf.readVarInt()
    )
}

@Serializable
data class Position(
    val x: Long,
    val y: Long,
    val z: Long
)

class PlayerPositionAndLookDecoder : PacketDecoder<PlayerPositionAndLookPacket> {
    override fun decoder(buf: ByteBuf): PlayerPositionAndLookPacket {
        return PlayerPositionAndLookPacket(buf)
    }

}
