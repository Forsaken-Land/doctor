package top.limbang.doctor.plugin.forge.entity

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.extension.readResourceLocation
import top.limbang.doctor.protocol.extension.readString
import top.limbang.doctor.protocol.extension.readVarInt
import top.limbang.doctor.protocol.utils.ResourceLocation

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/1 下午8:14
 */
@Serializable
data class Snapshot(
    val ids: Map<ResourceLocation, Int>,
    val aliases: Map<ResourceLocation, ResourceLocation>,
    val blocked: Set<Int>,
    val dummied: Set<ResourceLocation>,
    val overrides: Map<ResourceLocation, String>
)

fun ByteBuf.readSnapshot(): Snapshot {
    var len = readVarInt()
    val ids = mutableMapOf<ResourceLocation, Int>()
    for (i in 0 until len) {
        ids[readResourceLocation()] = readVarInt()
    }
    len = readVarInt()
    val aliases = mutableMapOf<ResourceLocation, ResourceLocation>()
    for (i in 0 until len) {
        aliases[readResourceLocation()] = readResourceLocation()
    }
    len = readVarInt()
    val overrides = mutableMapOf<ResourceLocation, String>()
    for (i in 0 until len) {
        overrides[readResourceLocation()] = readString()
    }
    len = readVarInt()
    val blocked = mutableSetOf<Int>()
    for (i in 0 until len) {
        blocked.add(readVarInt())
    }
    val dummied = mutableSetOf<ResourceLocation>()
    for (i in 0 until len) {
        dummied.add(readResourceLocation())
    }
    return Snapshot(ids, aliases, blocked, dummied, overrides)
}