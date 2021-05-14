package top.limbang.doctor.protocol.definition.play.client

import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.Packet

@Serializable
data class PacketPlayerPacket(
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float,
    val onGround: Boolean
) : Packet