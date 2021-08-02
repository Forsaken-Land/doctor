package top.fanua.doctor.protocol.definition.play.client

import kotlinx.serialization.Serializable

@Serializable
data class EntityPacket(
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float,
    val onGround: Boolean
)
