package top.limbang.doctor.protocol.definition.play.client

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder

/**
 * @author Doctor_Yin
 * @date 2021/5/1
 * @time 14:07
 */
@Serializable
data class ExplosionPacket(
    val x: Float,
    val y: Float,
    val z: Float,
    val strength: Float,
    val recordCount: Int,
    val record: Records,
    val playerMotionX: Float,
    val playerMotionY: Float,
    val playerMotionZ: Float,
) : Packet

@Serializable
data class Records(
    val byte1: Byte,
    val byte2: Byte,
    val byte3: Byte
) {
    constructor(buf: ByteBuf) : this(buf.readByte(), buf.readByte(), buf.readByte())
}

class ExplosionDecoder : PacketDecoder<ExplosionPacket> {
    override fun decoder(buf: ByteBuf): ExplosionPacket {
        val x = buf.readFloat()
        val y = buf.readFloat()
        val z = buf.readFloat()
        val strength = buf.readFloat()
        val recordCount = buf.readInt()
        val record = Records(buf)
        val playerMotionX = buf.readFloat()
        val playerMotionY = buf.readFloat()
        val playerMotionZ = buf.readFloat()
        return ExplosionPacket(
            x = x,
            y = y,
            z = z,
            strength = strength,
            recordCount = recordCount,
            record = record,
            playerMotionX = playerMotionX,
            playerMotionY = playerMotionY,
            playerMotionZ = playerMotionZ
        )

    }
}
