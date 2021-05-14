package top.limbang.doctor.protocol.definition.play.client

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
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

    private val bit: Int = id

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
        return 1 shl bit
    }
}

@Serializable
data class PlayerPositionAndLookPacket(
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float,
    val flags: Set<Flags>?,
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