package top.limbang.doctor.protocol.entity.extra

import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.readString
import top.limbang.doctor.protocol.extension.writeString

/**
 * 原版CustomPayload的解析，反正用的不多先写死
 * @author WarmthDawn
 * @since 2021-05-15
 */
enum class CustomPayloadType(
    val channelName: String
) {
    UNKNOWN(""),
    MC_Brand("MC|Brand") {
        override fun readPacket(buf: ByteBuf, out: MutableMap<String, Any>) {
            out["brand"] = buf.readString()
        }

        override fun writePacket(buf: ByteBuf, map: Map<String, Any>) {
            if (map.containsKey("brand"))
                buf.writeString(map["brand"] as String)
        }
    };

    open fun readPacket(buf: ByteBuf, out: MutableMap<String, Any>) {

    }

    open fun writePacket(buf: ByteBuf, map: Map<String, Any>) {

    }

    companion object {
        private val nameMap = values().associateBy { it.channelName }
        fun get(channelName: String) = nameMap[channelName] ?: UNKNOWN
    }
}

