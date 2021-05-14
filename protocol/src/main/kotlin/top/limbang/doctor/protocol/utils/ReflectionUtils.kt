package top.limbang.doctor.protocol.utils

import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketCodec
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.protocol.core.ProtocolException
import java.lang.reflect.ParameterizedType

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */

fun <T> tryGetGenericType(cls: Class<in PacketCodec>): Class<T>? {
    val type = cls.genericInterfaces.find { it is ParameterizedType }
    if (type != null) {
        try {
            (type as ParameterizedType).actualTypeArguments.forEach {
                if (it is Class<*>) {
                    return it.cast()
                }
            }
        } catch (e: Exception) {
        }
    }
    return null
}

fun <T : Packet> PacketEncoder<T>.packetClass(): Class<T> {
    return tryGetGenericType(this.javaClass) ?: throw ProtocolException("未能成功注册Encoder对应的class，请手动指定。")
}

fun <T : Packet> PacketDecoder<T>.packetClass(): Class<T> {
    return tryGetGenericType(this.javaClass) ?: throw ProtocolException("未能成功注册Decoder对应的class，请手动指定。")
}

fun <T: Any> Any.cast(): T {
    return this as T
}